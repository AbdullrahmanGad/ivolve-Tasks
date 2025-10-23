
# 3- Structured Configuration Management with Ansible Roles
  
## Objectives

- Use Ansible roles for structured configuration
- Install and configure Docker, kubectl, and Jenkins
- Verify installations on managed node

## Master Playbook: `roles.yml`

```yaml
---
- name: Configure Tools
  hosts: web
  become: true
  roles:
    - docker
    - kubectl
    - jenkins
```

## Roles Overview

### 1. Docker Role

- Checks if Docker is installed

- Installs dependencies and Docker Engine if missing

- Starts and enables Docker service

- Verifies installation

- **Tasks**: `roles/docker/tasks/main.yml`

### 2. kubectl Role

- Ensures `curl` is installed

- Checks for existing `kubectl`

- Downloads and installs latest stable `kubectl` if missing

- Verifies installation

- **Tasks**: `roles/kubectl/tasks/main.yml`

### 3. Jenkins Role

- Checks if Jenkins is installed

- Installs dependencies and Jenkins package if missing

- Starts and enables Jenkins service

- Verifies installation

- **Tasks**: `roles/jenkins/tasks/main.yml`

## Steps

1. **Run Master Playbook**:

`ansible-playbook -i inventory roles.yml -k`

- Use `-K` to enter sudo password.
2. **Verify Installation**
   
   `one the managed Node or Via SSH.`
- **Docker**:

`docker --version`

- **kubectl**:

`kubectl version --client=true`

- **Jenkins**:

`systemctl status jenkins`

