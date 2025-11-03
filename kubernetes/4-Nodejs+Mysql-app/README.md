# Kubernetes Microservices Deployment — MySQL + Node.js App

This project demonstrates a full production-style setup for deploying a **Node.js application with a MySQL database** on Kubernetes.  
It implements **Stateful workloads, storage, secrets, init-containers, probes, resource limits, and network security policies**.

---

## Project Structure

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
  
![docsec](https://github.com/user-attachments/assets/f6c86bde-8862-4801-a90d-93c0356a84fc)

---

### 3️⃣ MySQL ConfigMap and secret

Stores MySQL configuration (database name, etc.).

![5](https://github.com/user-attachments/assets/bc7f804e-6fed-4f9d-9e7d-b7a8f2a8e482)

![6](https://github.com/user-attachments/assets/c091ba45-a7be-47f3-8df1-a858821c874b)

### Verify:-

![5 5](https://github.com/user-attachments/assets/4113219c-a434-4dc2-a7eb-888ebc1cee7e)

---

### 5️⃣ Persistent Storage for MySQL

**PV & PVC** for MySQL logs & data.

![7](https://github.com/user-attachments/assets/be2c60fc-61a5-499c-b33f-978e89dbd4cb)

```bash
kubectl apply -f mysql-pv.yml
kubectl apply -f mysql-pvc.yml
```

![9edit](https://github.com/user-attachments/assets/47f3e67a-d71b-4002-85c1-a303965c4cdf)

---

### 6️⃣ Headless Service

```bash
kubectl apply -f headless-svc-mysql.yml
```

![10](https://github.com/user-attachments/assets/46872b98-6898-4d41-95ae-3879fef3530a)

---

### 7️⃣ MySQL StatefulSet Deployment

- Uses secrets for credentials

- Uses PV/PVC for persistence

- Headless Service for stable hostnames

- Toleration added for worker node taint

> ![11](https://github.com/user-attachments/assets/5131d2e7-603e-4bba-a2e0-0f0ec02ad600)
> 
> **Outcome:** MySQL pod starts with persistent storage & private DNS

---

### 8️⃣ PV & PVC for Application Logs

Storage for Node.js application logs.

```bash
kubectl apply -f myapp-pv.yml
kubectl apply -f myapp-pvc.yml
```

![14](https://github.com/user-attachments/assets/63113e1e-1d1a-4f53-a62e-4523d78508a3)

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

![1 1](https://github.com/user-attachments/assets/5ac114f0-8dd1-4084-b2e2-c82b1997968d)

- Init-Container:

![1 2](https://github.com/user-attachments/assets/c432e013-0f47-40ba-9874-f3962fa145a3)

- mysql commands to create (db+user)

![3](https://github.com/user-attachments/assets/3d6c438f-a0ff-422c-85a4-84baf79feb25)

- nodejs-app:

![4](https://github.com/user-attachments/assets/a9ef42d6-3896-4b65-9431-b9187fac0113)

- Probes, Resources & Limits:

![5](https://github.com/user-attachments/assets/bb4949bd-a272-4f4f-8603-258b05e2ec20)

- Volume &  nodejs-app internal service:

![1 6](https://github.com/user-attachments/assets/1d340123-6682-4e17-9556-ae3b38cae46a)

- Verify resources and limits:

![20](https://github.com/user-attachments/assets/62276cfe-6947-4c9a-9360-ae3aae321df1)

- Monitor with kubectl top: (Note: should be installed first.)

![22](https://github.com/user-attachments/assets/43f79665-8768-4f66-b913-655a95f83af2)

- Notice one of the pods is pending because of the lack of resources.

![23](https://github.com/user-attachments/assets/58ecbf19-4c70-4106-874d-080f35933068)

![24](https://github.com/user-attachments/assets/879e2f4d-6433-41b2-bcbe-877b2367e34d)

---

### 🔐 Network Policy

![netpolicy1 1](https://github.com/user-attachments/assets/d23a56d6-a7d4-4b6e-9ec4-ecaade35f963)

Only allow Node.js app → MySQL access on port **3306**.

---

### Connectivity Test

- Test external pod (blocked):

![netpolicy1 2](https://github.com/user-attachments/assets/4caa0883-b53f-4c2c-9533-97032a0ea5fa)

![netpolicy1 3](https://github.com/user-attachments/assets/d3b4408e-6330-437f-88b5-bfed75f6232d)

---

## 🌐 **Verify app is working**

- inside the container verify dtabase ivolve is created and ivolve-user exists

![17](https://github.com/user-attachments/assets/ef7c2360-5764-4c36-a557-0b57c5fe2aea)

- use port-forwarding then access the app using the browser on port 3000. 

```bash
kubectl port-forward svc/nodejs-service 8080:3000 -n ivolve
```

![19](https://github.com/user-attachments/assets/31e8af01-1e66-449e-a30a-9c430720c079)
