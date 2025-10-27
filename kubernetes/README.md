# Kubernetes Cluster Setup with Kubeadm

A complete guide to building a production-ready Kubernetes cluster from scratch using kubeadm, featuring one master node and one worker node.




---

## 📋 Table of Contents

- [Architecture Overview](#-architecture-overview)
- [Prerequisites](#-prerequisites)
- [Infrastructure Setup](#-infrastructure-setup)
- [Installation Steps](#-installation-steps)
  - [Common Setup (Both Nodes)](#1️⃣-common-setup-both-nodes)
  - [Master Node Configuration](#2️⃣-master-node-configuration)
  - [Worker Node Configuration](#3️⃣-worker-node-configuration)
  - [Network Plugin Installation](#4️⃣-network-plugin-installation)
- [Verification](#-verification)

---

## Architecture Overview

```sql
┌─────────────────────────────────────────────────────┐
│                  Kubernetes Cluster                 │
│                                                     │
│  ┌──────────────────┐      ┌──────────────────┐     │
│  │   Master Node    │      │   Worker Node    │     │
│  │ <MASTER_NODE_IP> │◄─────┤ <WORKER_NODE_IP> │     │
│  │                  │      │                  │     │
│  │  • API Server    │      │  • Kubelet       │     │
│  │  • etcd          │      │  • Kube-proxy    │     │
│  │  • Scheduler     │      │  • Container     │     │
│  │  • Controller    │      │    Runtime       │     │
│  └──────────────────┘      └──────────────────┘     │
│                                                     │
│         Pod Network: 10.244.0.0/16 (Weave Net)      │
└─────────────────────────────────────────────────────┘
```

---

## Prerequisites

### Minimum Hardware Requirements (Per Node)

| Component   | Requirement                             |
| ----------- | --------------------------------------- |
| **RAM**     | 2 GB minimum                            |
| **CPU**     | 2 cores minimum                         |
| **Disk**    | 20 GB available space                   |
| **Network** | Full network connectivity between nodes |

### Software Requirements

- Ubuntu 20.04 LTS or later (recommended)
- Root or sudo privileges
- Unique hostname, MAC address, and IP for each node

---

## Infrastructure Setup

### Step 1: Create Virtual Machines

Create 2 VMs with the minimum specifications mentioned above.

### Step 2: Configure Unique Identifiers

Ensure each node has:

- **Unique MAC address**
- **Unique IP address**
- **Unique hostname**

```bash
# Set hostname (example for master node)
sudo hostnamectl set-hostname master

# Set hostname (example for worker node)
sudo hostnamectl set-hostname worker
```

---

## 📦 Installation Steps

### 1️⃣ Both Nodes:-

Perform these steps on **BOTH** master and worker nodes.

#### 1.1 Disable Swap

Kubernetes requires swap to be disabled to ensure proper kubelet functionality.

```bash
# Disable swap immediately
sudo swapoff -a

# Edit fstab to make it permanent
sudo nano /etc/fstab
```

Comment out the swap line by adding `#` at the beginning:

```
# /swap.img     none    swap    sw      0       0
```

Verify the changes:

```bash
sudo mount -a
free -h  # Verify swap shows 0
```

#### 1.2 Make sure Docker and Containerd are installed.

![1.jpg](F:\Git-hub\pics\cluster\1.jpg)

#### 1.3 Configure Containerd with systemd cgroup

```bash
# Generate default containerd configuration
containerd config default | sudo tee /etc/containerd/config.toml

# Edit the configuration file
sudo nano /etc/containerd/config.toml
```

![2.jpg](F:\Git-hub\pics\cluster\2.jpg)

Find the section:

```toml
[plugins."io.containerd.grpc.v1.cri".containerd.runtimes.runc.options]
```

![3new.jpg](F:\Git-hub\pics\cluster\3new.jpg)

Modify the `SystemdCgroup` parameter to `true`:

```toml
SystemdCgroup = true
```

Restart containerd:

```bash
sudo systemctl restart containerd
sudo systemctl enable containerd
```

#### 1.4 Enable IPv4 Packet Forwarding

```bash
# Create k8s sysctl configuration
cat <<EOF | sudo tee /etc/sysctl.d/k8s.conf
net.ipv4.ip_forward = 1
EOF

# Apply sysctl parameters without reboot
sudo sysctl --system

# Verify the setting
sysctl net.ipv4.ip_forward
```

#### 1.5 Install Kubeadm, Kubelet, and Kubectl

```bash
# Update package index and install prerequisites
sudo aptget update
sudo apt-get install -y apt-transport-https ca-certificates curl gpg

# Create keyrings directory if it doesn't exist
sudo mkdir -p -m 755 /etc/apt/keyrings

# Add Kubernetes GPG key
curl -fsSL https://pkgs.k8s.io/core:/stable:/v1.34/deb/Release.key | sudo gpg --dearmor -o /etc/apt/keyrings/kubernetes-apt-keyring.gpg

# Add Kubernetes repository
echo 'deb [signed-by=/etc/apt/keyrings/kubernetes-apt-keyring.gpg] https://pkgs.k8s.io/core:/stable:/v1.34/deb/ /' | sudo tee /etc/apt/sources.list.d/kubernetes.list

# Install Kubernetes components
sudo apt-get update
sudo apt-get install -y kubelet kubeadm kubectl

# Prevent automatic updates
sudo apt-mark hold kubelet kubeadm kubectl
```

![4.jpg](F:\Git-hub\pics\cluster\4.jpg)

> **Note**: You can skip installing `kubectl` on worker nodes if you don't plan to run kubectl commands there.

---

### 2️⃣ Master Node Configuration

Perform these steps **ONLY** on the master node.

#### 2.1 Initialize the Control Plane

```bash
sudo kubeadm init --pod-network-cidr 10.244.0.0/16 --apiserver-advertise-address <MASTER_NODE_IP>
```

![5new.jpg](F:\Git-hub\pics\cluster\5new.jpg)

**Important Parameters:**

- `--pod-network-cidr`: Specifies the IP range for pod networking (required for Weave Net)
- `--apiserver-advertise-address`: Your master node's IP address

#### 2.2 Configure kubectl for Regular User

After successful initialization, you'll see output with configuration commands. Run them:

```bash
mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config
```

![loading-ag-603](F:\Git-hub\pics\cluster\7.jpg)

#### 2.3 Save the Join Command

The `kubeadm init` output includes a join command. **Save it!** It looks like:

```bash
kubeadm join <MASTER_NODE_IP>:6443 --token qa7dzv.fv1e4s4p1d77wzjb \
--discovery-token-ca-cert-hash sha:629ca96e678bde75e79e40ea0c05ed56bcb28e92530b16db8cf4e68cb417b617
```

⚠️ **Important**: Tokens expire after 24 hours. If you need a new token later, run:

> ```bash
> kubeadm token create --print-join-command
> ```

---

### 3️⃣ Worker Node Configuration

Perform these steps **ONLY** on the worker node.

#### 3.1 Join the Cluster

Use the join command from the master node output:

```bash
sudo kubeadm join <MASTER_NODE_IP>:6443 --token qa7dzv.fv1e4s4p1d77wzjb \
--discovery-token-ca-cert-hash sha:629ca96e678bde75e79e40ea0c05ed56bcb28e92530b16db8cf4e68cb417b617
```

You should see output confirming the node has joined the cluster.

![8NEW.jpg](F:\Git-hub\pics\cluster\8NEW.jpg)

---

### 4️⃣ Network Plugin Installation

Kubernetes requires a pod network add-on for pod-to-pod communication. We'll use **Weave Net**.

Run this on the **master node**:

```bash
kubectl apply -f https://github.com/weaveworks/weave/releases/download/v2.8.1/weave-daemonset-k8s.yaml
```

![9.jpg](F:\Git-hub\pics\cluster\9.jpg)

Wait a few moments for the network plugin to initialize.

---

## ✅ Verification

### Check Cluster Status

On the master node:

![10.jpg](F:\Git-hub\pics\cluster\10.jpg)

### Verify Node Status

All nodes should show `STATUS: Ready`. If they show `NotReady`, wait a few minutes for the network plugin to fully initialize.

### Deploy a Test Application

```bash
# Create a test deployment
nano nginx.yml
# copy the yaml for deployment and service from k8s docs
```

![test.jpg](F:\Git-hub\pics\cluster\test.jpg)

![test1.jpg](F:\Git-hub\pics\cluster\test1.jpg)

![test2.jpg](F:\Git-hub\pics\cluster\test2.jpg)