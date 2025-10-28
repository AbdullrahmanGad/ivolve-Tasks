# Task 1: Node Isolation Using Taints in Kubernetes

## Objective

Implement node isolation using Kubernetes taints to control pod scheduling on specific nodes.

---

## Overview

This task demonstrates how to use Kubernetes taints to prevent pods from being scheduled on specific nodes unless they have matching tolerations. This is essential for:

- Dedicating nodes for specific workloads
- Isolating production from development environments
- Reserving nodes with special hardware (GPUs, high memory, etc.)
- Managing resource allocation efficiently

---

## Prerequisites

- Kubernetes cluster with 2 nodes (master and worker)
- `kubectl` configured and connected to the cluster
- Sufficient permissions to taint nodes

**Cluster Setup Used:**

- 1 Master Node
- 1 Worker Node
- Kubernetes v1.34+

---

## Task Requirements

1. ✅ Run a Kubernetes cluster with 2 nodes
2. ✅ Taint the second (worker) node with key-value pair `workload=worker` and effect `NoSchedule`
3. ✅ Describe all nodes to verify the taint is applied

---

## Implementation Steps

### Step 1: Check Available Nodes

```bash
kubectl get nodes
```

### Step 2: Apply Taint to Worker Node

```bash
kubectl taint nodes worker workload=worker:NoSchedule
```

![Screenshot 2025-10-27 112615.jpg](F:\Git-hub\pics\cluster\1\Screenshot%202025-10-27%20112615.jpg)

**Explanation:**

- `workload=worker`: Key-value pair for the taint
- `NoSchedule`: Effect that prevents new pods from scheduling on this node

### Step 3: Verify Taint on Worker Node

```bash
kubectl describe node worker | grep -A 3 Taints
```

![2.jpg](F:\Git-hub\pics\cluster\1\2.jpg)

### Step 4: Describe All Nodes

```bash
kubectl describe nodes
```

![33.jpg](F:\Git-hub\pics\cluster\1\33.jpg)

Look for the `Taints:` section in each node's output.

---

### Testing the Taint

#### Create Pod WITHOUT Toleration

**File: `nginx-no-toleration.yaml`**

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: nginx-no-taint
  labels:
    app: nginx-no-taint
spec:
  containers:
  - name: nginx
    image: nginx:latest
    ports:
    - containerPort: 80
```

Apply:

```bash
kubectl apply -f nginx-no-toleration.yaml
kubectl get pods -o wide
```

![no.jpg](F:\Git-hub\pics\cluster\1\no.jpg)

**Result:** Pod will NOT schedule on the worker node (will be Pending or schedule on master if master taint is removed).

#### Create Pod WITH Toleration

**File: `nginx-with-toleration.yaml`**

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: nginx-toleration
  labels:
    app: nginx-toleration
spec:
  tolerations:
  - key: "workload"
    operator: "Equal"
    value: "worker"
    effect: "NoSchedule"
  containers:
  - name: nginx
    image: nginx:latest
    ports:
    - containerPort: 80
```

Apply:

```bash
kubectl apply -f nginx-with-toleration.yaml
kubectl get pods -o wide
```

![yes.jpg](F:\Git-hub\pics\cluster\1\yes.jpg)

**Result:** Pod CAN schedule on the worker node.

---

# 

### Taint Effects

| Effect               | Behavior                                                  |
| -------------------- | --------------------------------------------------------- |
| **NoSchedule**       | New pods won't be scheduled (existing pods remain)        |
| **PreferNoSchedule** | Kubernetes tries to avoid scheduling here                 |
| **NoExecute**        | New pods won't be scheduled AND existing pods are evicted |