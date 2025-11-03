# Kubernetes Microservices Deployment — MySQL + Node.js App

This project demonstrates a full production-style setup for deploying a **Node.js application with a MySQL database** on Kubernetes.  
It implements **Stateful workloads, storage, secrets, init-containers, probes, resource limits, and network security policies**.

---

## Project Structure

.
├── kubernetes/
│   ├── docker-secret.yml  (make your own)
│   ├── config-map.yml
│   ├── secret.yml
│   ├── mysql-pv.yml
│   ├── mysql-pvc.yml
│   ├── headless-svc-mysql.yml
│   ├── mysql-statefulset.yml
│   ├── myapp-pv.yml
│   ├── myapp-pvc.yml
│   ├── deployment.yml
│   ├── Net-policy.yml
└── README.md

---

## 🎯 Objective

✅ Deploy MySQL using StatefulSet with Persistent Storage  
✅ Deploy Node.js app using Deployment + ClusterIP Service  
✅ Secure credentials using Kubernetes Secrets  
✅ Use ConfigMaps for database connection details  
✅ Auto-create database + user via init container  
✅ Mount storage for logs  
✅ Implement health probes  
✅ Apply CPU/Memory resource limits  
✅ NetworkPolicy restricting DB access  
✅ Validate using port-forward & logs

---

## ✅ **Steps Completed**

### 1️⃣ Existing Namespace

Used an already-created namespace `ivolve`:

---

### 2️⃣ Docker Registry Secret

Created Docker secret to pull the app image:

1-login into your account

2- then

```bash
cat ~/.dokcer/config.json | base64 -w 0
```

- to get the credeintials and encode it.

- create docker-secret.yml
  
  ![docsec.jpg](F:\Git-hub\pics\cluster\from1\docsec.jpg)

---

### 3️⃣ MySQL ConfigMap and secret

Stores MySQL configuration (database name, etc.).

![5.jpg](F:\Git-hub\pics\cluster\from1\5.jpg)

![6.jpg](F:\Git-hub\pics\cluster\from1\6.jpg)

### Verify:-

![5.5.jpg](F:\Git-hub\pics\cluster\from1\5.5.jpg)



---

### 5️⃣ Persistent Storage for MySQL

**PV & PVC** for MySQL logs & data.

![7.jpg](F:\Git-hub\pics\cluster\from1\7.jpg)

```bash
kubectl apply -f mysql-pv.yml
kubectl apply -f mysql-pvc.yml
```

![9edit.jpg](F:\Git-hub\pics\cluster\3\9edit.jpg)

---

### 6️⃣ Headless Service

```bash
kubectl apply -f headless-svc-mysql.yml
```

![10.jpg](F:\Git-hub\pics\cluster\from1\10.jpg)

---

### 7️⃣ MySQL StatefulSet Deployment

- Uses secrets for credentials

- Uses PV/PVC for persistence

- Headless Service for stable hostnames

- Toleration added for worker node taint

> ![11.jpg](F:\Git-hub\pics\cluster\from1\11.jpg)
> 
> **Outcome:** MySQL pod starts with persistent storage & private DNS

---

### 8️⃣ PV & PVC for Application Logs

Storage for Node.js application logs.

```bash
kubectl apply -f myapp-pv.yml
kubectl apply -f myapp-pvc.yml
```

![14edit.jpg](F:\Git-hub\pics\cluster\3\14edit.jpg)

---

### 9️⃣ Node.js Application Deployment

- This step deploys the Node.js application to the `ivolve` namespace using a **Deployment** and **ClusterIP Service**.

- ### Key Details
  
  | Feature            | Explanation                                  |
  | ------------------ | -------------------------------------------- |
  | Replicas           | 2 Pods for high availability                 |
  | Tolerations        | App pods scheduled on worker node with taint |
  | Private Image Pull | Uses `dockerhub-secret`                      |
  | Init Container     | Waits for MySQL & initializes database/user  |
  | Probes             | Liveness & Readiness HTTP probes `/health`   |
  | Resources          | CPU & memory requests & limits               |
  | Persistent Logging | PVC mounted at `/usr/src/app/logs`           |
  | Environment Vars   | Loaded from ConfigMap & Secret               |
  | Service Type       | ClusterIP to expose app internally           |

- replicas & Tolerations:

![1.1.jpg](F:\Git-hub\pics\cluster\from1\dep\1.1.jpg)

- Init-Container:

![1.2.jpg](F:\Git-hub\pics\cluster\from1\dep\1.2.jpg)

- mysql commands to create (db+user)

![3.jpg](F:\Git-hub\pics\cluster\from1\dep\3.jpg)

- nodejs-app:

![4.jpg](F:\Git-hub\pics\cluster\from1\dep\4.jpg)

- Probes, Resources & Limits:

![5.jpg](F:\Git-hub\pics\cluster\from1\dep\5.jpg)

- Volume &  nodejs-app internal service:

![1.6.jpg](F:\Git-hub\pics\cluster\from1\dep\1.6.jpg)

- Verify resources and limits:

![20.jpg](F:\Git-hub\pics\cluster\from1\20.jpg)

- Monitor with kubectl top: (Note: should be installed first.)

![22.jpg](F:\Git-hub\pics\cluster\from1\22.jpg)

- Notice one of the pods is pending because of the lack of resources.

![23.jpg](F:\Git-hub\pics\cluster\from1\23.jpg)

![24.jpg](F:\Git-hub\pics\cluster\from1\24.jpg)

---

### 🔐 Network Policy

![netpolicy1.1.jpg](F:\Git-hub\pics\cluster\from1\netpolicy1.1.jpg)

Only allow Node.js app → MySQL access on port **3306**.

---

### Connectivity Test

- Test external pod (blocked):

![netpolicy1.2.jpg](F:\Git-hub\pics\cluster\from1\netpolicy1.2.jpg)

![netpolicy1.3.jpg](F:\Git-hub\pics\cluster\from1\netpolicy1.3.jpg)

---

## 🌐 **Verify app is working**

- inside the container verify dtabase ivolve is created and ivolve-user exists

![17.jpg](F:\Git-hub\pics\cluster\from1\17.jpg)

- use port-forwarding then access the app using the browser on port 3000. 

```bash
kubectl port-forward svc/nodejs-service 8080:3000 -n ivolve
```

![19.jpg](F:\Git-hub\pics\cluster\from1\19.jpg)
