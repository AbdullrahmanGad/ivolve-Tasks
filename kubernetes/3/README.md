# Task 3: Managing Configuration and Sensitive Data with ConfigMaps and Secrets

## Objective

Demonstrate how to securely manage application configuration using Kubernetes ConfigMaps for non-sensitive data and Secrets for sensitive credentials.

---

## Overview

This task demonstrates the separation of configuration from application code using:

- **ConfigMaps**: Store non-sensitive configuration data in plain text
- **Secrets**: Store sensitive data with base64 encoding

This approach enables:

- Configuration externalization
- Environment-specific configurations
- Secure credential management
- Application portability
- Configuration updates without rebuilding images

---

## Prerequisites

- Kubernetes cluster with `ivolve` namespace
- `kubectl` configured and connected to the cluster
- Basic understanding of base64 encoding

**Note:** If the `ivolve` namespace doesn't exist:

```bash
kubectl create namespace ivolve
```

---

## Task Requirements

### ConfigMap Requirements

✅ Store non-sensitive MySQL configuration:

- `DB_HOST`: MySQL StatefulSet service hostname
- `DB_USER`: Database user for application connection

### Secret Requirements

✅ Store sensitive MySQL credentials (base64 encoded):

- `DB_PASSWORD`: Password for DB_USER
- `MYSQL_ROOT_PASSWORD`: Root password for MySQL

---

## Implementation Steps

### Step 1: Create ConfigMap

**File: `cm.yaml`**

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: ivolve-cm
  namespace: ivolve
data:
  DB_HOST: mysql
  DB_USER: ivolve
```

Apply the ConfigMap:

```bash
kubectl apply -f cm.yaml
```

**Expected Output:**

```
configmap/ivolve-cm created
```

**Explanation:**

- `DB_HOST`: Points to the MySQL service (for StatefulSet: `mysql-0.mysql-service`)
- `DB_USER`: Application database user (not root user)
- Data stored in **plain text** (not sensitive information)

---

### Step 2: Encode Passwords with Base64

Before creating the Secret, encode your passwords:

```bash
# Encode DB_PASSWORD
echo -n '1234' | base64
```

**Output:**

```
MTIzNA==
```

```bash
# Encode MYSQL_ROOT_PASSWORD
echo -n 'root' | base64
```

**Output:**

```
cm9vdA==
```

> ⚠️ **Important**: Use the `-n` flag to prevent encoding the newline character!

---

### Step 3: Create Secret

**File: `mysql-secret.yaml`**

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: ivolve-secret
  namespace: ivolve
data:
  DB_PASSWORD: MTIzNA==
  MYSQL_ROOT_PASSWORD: cm9vdA==
```

Apply the Secret:

```bash
kubectl apply -f mysql-secret.yaml
```

**Expected Output:**

```
secret/mysql-secret created
```

![3](https://github.com/user-attachments/assets/3ef9d24b-eacb-457f-90ee-d3b919c2f0ef)

---

## Verification

### View ConfigMap

```bash
kubectl get configmap mysql-config -n ivolve
```

### View Secret

```bash
kubectl get secret mysql-secret -n ivolve
```

![4](https://github.com/user-attachments/assets/71bbc89f-6b16-49f1-afb4-bfefe5c21959)

Detailed view:

```bash
kubectl describe configmap mysql-config -n ivolve
```

---

### Decode Secret Values

```bash
# Decode DB_PASSWORD
kubectl get secret mysql-secret -n ivolve -o jsonpath='{.data.DB_PASSWORD}' | base64 --decode
echo
```

**Output:**

```
1234
```

```bash
# Decode MYSQL_ROOT_PASSWORD
kubectl get secret mysql-secret -n ivolve -o jsonpath='{.data.MYSQL_ROOT_PASSWORD}' | base64 --decode
echo
```

**Output:**

```
root**

```
