# Task 2: Namespace Management and Resource Quota Enforcement

## Objective

Create a Kubernetes namespace and enforce resource quotas to limit the number of pods that can run within it.

---

## Overview

This task demonstrates how to use Kubernetes namespaces for resource isolation and ResourceQuota objects to enforce limits. This is crucial for:

---

## Prerequisites

- Kubernetes cluster (master and worker nodes)
- `kubectl` configured and connected to the cluster
- Sufficient permissions to create namespaces and resource quotas

---

## Task Requirements

1. ✅ Create a namespace called `ivolve`
2. ✅ Apply a ResourceQuota to limit pods to only 2 within the namespace

---

## Implementation Steps

### Step 1: Create Namespace

**File: `namespace.yaml`**

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: ivolve
```

Apply the namespace:

```bash
kubectl apply -f namespace.yaml
```

![ns.jpg](F:\Git-hub\pics\cluster\2\ns.jpg)

**Verify namespace is created:**

![ns2.jpg](F:\Git-hub\pics\cluster\2\ns2.jpg)

### Step 2: Create ResourceQuota

**File: `resourcequota.yaml`**

```yaml
apiVersion: v1
kind: ResourceQuota
metadata:
  name: pod-limit-quota
  namespace: ivolve
spec:
  hard:
    pods: "2"
```

Apply the quota:

```bash
kubectl apply -f res-quota.yaml
```

**Expected Output:**

```
resourcequota/pod-limit-quota created
```

### Step 4: Verify ResourceQuota

```bash
kubectl get resourcequota -n ivolve
```

**Expected Output:**

![res.jpg](F:\Git-hub\pics\cluster\2\res.jpg)



## Verification

### Test : Create 3 Pods since the limit is 2 (2 Should Succeed)

**File: `test-pod.yaml`**

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: nginx-1
  namespace: ivolve
spec:
  containers:
  - name: nginx
    image: nginx:latest
```

**File: `test-pod2.yaml`**

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: nginx-2
  namespace: ivolve
spec:
  containers:
  - name: nginx
    image: nginx:latest
```

**File: `test-pod3.yaml`**

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: nginx-3
  namespace: ivolve
spec:
  containers:
  - name: nginx
    image: nginx:latest
```

Apply:

```bash
kubectl apply -f test-pod.yaml
pod/nginx-1 created
kubectl apply -f test-pod2.yaml
pod/nginx-2 created
kubectl apply -f test-pod-3.yaml
Error from server (Forbidden): error when creating "test-pod-3.yaml": 
pods "nginx-3" is forbidden: exceeded quota: pod-limit-quota, 
requested: pods=1, used: pods=2, limited: pods=2
```

![test.jpg](F:\Git-hub\pics\cluster\2\test.jpg)

✅ **SUCCESS!** The ResourceQuota is working correctly - it blocks the third pod!