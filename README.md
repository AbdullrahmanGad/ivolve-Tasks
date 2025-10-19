## 📚 Table of Contents

- [1- Initial Ansible Configuration](#1--initial-ansible-configuration)
- [2- Automated Web Server Configuration Using Ansible Playbooks](#2--automated-web-server-configuration-using-ansible-playbooks)
- [3- Structured Configuration Management with Ansible Roles](#3--structured-configuration-management-with-ansible-roles)
- [4- Securing Sensitive Data with Ansible Vault](#4--securing-sensitive-data-with-ansible-vault)
- [5- Automated Host Discovery with Ansible Dynamic Inventory (AWS EC2)](#5--automated-host-discovery-with-ansible-dynamic-inventory-aws-ec2)

# 1- Initial Ansible Configuration

## Objectives

- Install and configure Ansible on the control node
- Set up passwordless SSH to managed node
- Create inventory file
- Execute a basic ad-hoc command

## Steps

1. **Install Ansible** on control node:
   
```bash
sudo apt install ansible   # or yum/dnf depending on your OS
```

2. **Generate SSH Key** on control node:

```bash
ssh-keygen -t rsa -b 4096
```

3. **Copy Public Key** to managed node:

```bash
ssh-copy-id user@managed_node_ip
```

4. **Create Inventory** `inventory`:

```ini
[managed] 
managed_node_ip ansible_user=user
```

5. **Run Ad-Hoc Command**:

```bash
ansible managed -i inventory.ini -m command -a "df -h"
```

## Notes

- Ensure **OpenSSH** is installed on managed node.

- Verify network connectivity and SSH access.

- Use sudo if needed.

- Firewall should allow SSH.

# 2- Automated Web Server Configuration Using Ansible Playbooks

## Objectives

- Automate web server setup using Ansible
- Install and configure Nginx
- Deploy a custom web page
- Verify the web server status

## Playbook: `playbook1.yml`

```yaml
---
- name: Configure Nginx
  hosts: web
  become: true

  tasks:
    - name: Install Nginx
      ansible.builtin.package:
        name: nginx
        state: present

    - name: Start and enable Nginx service
      ansible.builtin.service:
        name: nginx
        state: started
        enabled: true

    - name: Deploy custom index.html
      ansible.builtin.copy:
        dest: /var/www/html/index.html
        content: |
          <html>
          <head>
            <title>Welcome</title>
          </head>
          <body>
            <h1>Configured by Ansible 🚀</h1>
            <p>This web page was deployed using an Ansible Playbook.</p>
          </body>
          </html>

    - name: Verify Nginx is running
      ansible.builtin.shell: systemctl is-active nginx
      register: nginx_status

    - name: Display status
      ansible.builtin.debug:
        msg: "Nginx service status: {{ nginx_status.stdout }}"
```

## Steps

1. **Run Playbook**:

```bash
ansible-playbook -i inventory.ini playbook1.yml -k
```

- Use `-K` to enter sudo password.
2. **Verify Configuration**:

```bash
curl http://managed_node_ip
```

## Notes

- Ensure the managed node has **internet access** for package installation.

- Use `become: true` for tasks requiring root privileges.

- Firewall should allow **HTTP (port 80)**.

- Check that no other web server is running to avoid conflicts.

- ### Additional Notes:
  
  1. **Separate HTML File Option**
     
     - Instead of writing the HTML directly in the playbook, you can store it in a separate file (e.g., `index.html`) and use `copy` or `template` module:
       
       ```yml
       - name: Deploy custom index.html
         ansible.builtin.copy:
           src: index.html
           dest: /var/www/html/index.html
       ```


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
                   
# 4- Securing Sensitive Data with Ansible Vault

## Objectives

- Install and configure MySQL server using Ansible
- Create a database and user with privileges
- Secure sensitive data (DB password) using Ansible Vault
- Validate database setup

## Playbook: `playbook.yml`

```yaml
---
- name: Secure MySQL Setup with Ansible Vault
  hosts: all
  become: yes
  vars_files:
    - group_vars/all/vault.yml

  tasks:
    - name: Install MySQL server
      apt:
        name: mysql-server
        state: present
        update_cache: yes

    - name: Install Python MySQL dependencies
      apt:
        name: python3-pymysql
        state: present

    - name: Ensure MySQL service is running
      service:
        name: mysql
        state: started
        enabled: yes

    - name: Create iVolve database
      community.mysql.mysql_db:
        name: "{{ db_name }}"
        state: present
        login_unix_socket: /var/run/mysqld/mysqld.sock

    - name: Create user with privileges
      community.mysql.mysql_user:
        name: "{{ db_user }}"
        password: "{{ db_password }}"
        priv: "{{ db_name }}.*:ALL"
        state: present
        login_unix_socket: /var/run/mysqld/mysqld.sock

    - name: Validate DB connection and list databases
      community.mysql.mysql_query:
        login_user: "{{ db_user }}"
        login_password: "{{ db_password }}"
        query: "SHOW DATABASES;"
      register: db_list

    - name: Display databases
      debug:
        var: db_list.query_result
```

## Vault File: `group_vars/all/vault.yml`

```yml
db_name: iVolve
db_user: ivolve_user
db_password: 1234
```

## Steps

1. **Encrypt Vault File** (if not already encrypted):

```bash
ansible-vault encrypt group_vars/all/vault.yml
```

2. **Run Playbook**:

```bash
ansible-playbook -i inventory.ini secure_mysql.yml --ask-vault-pass
```

3. **Verify Database**:

```bash
mysql -u ivolve_user -p -e "SHOW DATABASES;"
```

- ### Optional: To Edit the Vault Later
  
  ```bash
  ansible-vault edit group_vars/all/vault.yml
  ```
  
  ## Notes

- Ensure **Python MySQL module (`python3-pymysql`)** is installed for Ansible modules to work.

- Use `become: yes` for tasks requiring root privileges.

- Vault ensures sensitive info like DB passwords are not exposed in playbooks.

- Use `ansible-vault view` to read encrypted files securely.

- Ensure MySQL service is running and accessible on the managed node.

- Use `login_unix_socket: /var/run/mysqld/mysqld.sock` to let Ansible connect as MySQL root via socket (no password needed)

#4- Securing Sensitive Data with Ansible Vault

## Objectives

- Install and configure MySQL server using Ansible
- Create a database and user with privileges
- Secure sensitive data (DB password) using Ansible Vault
- Validate database setup

## Playbook: `playbook.yml`

```yaml
---
- name: Secure MySQL Setup with Ansible Vault
  hosts: all
  become: yes
  vars_files:
    - group_vars/all/vault.yml

  tasks:
    - name: Install MySQL server
      apt:
        name: mysql-server
        state: present
        update_cache: yes

    - name: Install Python MySQL dependencies
      apt:
        name: python3-pymysql
        state: present

    - name: Ensure MySQL service is running
      service:
        name: mysql
        state: started
        enabled: yes

    - name: Create iVolve database
      community.mysql.mysql_db:
        name: "{{ db_name }}"
        state: present
        login_unix_socket: /var/run/mysqld/mysqld.sock

    - name: Create user with privileges
      community.mysql.mysql_user:
        name: "{{ db_user }}"
        password: "{{ db_password }}"
        priv: "{{ db_name }}.*:ALL"
        state: present
        login_unix_socket: /var/run/mysqld/mysqld.sock

    - name: Validate DB connection and list databases
      community.mysql.mysql_query:
        login_user: "{{ db_user }}"
        login_password: "{{ db_password }}"
        query: "SHOW DATABASES;"
      register: db_list

    - name: Display databases
      debug:
        var: db_list.query_result
```

## Vault File: `group_vars/all/vault.yml`

```yml
db_name: iVolve
db_user: ivolve_user
db_password: 1234
```

## Steps

1. **Encrypt Vault File** (if not already encrypted):

```bash
ansible-vault encrypt group_vars/all/vault.yml
```

2. **Run Playbook**:

```bash
ansible-playbook -i inventory.ini secure_mysql.yml --ask-vault-pass
```

3. **Verify Database**:

```bash
mysql -u ivolve_user -p -e "SHOW DATABASES;"
```

- ### Optional: To Edit the Vault Later
  
  ```bash
  ansible-vault edit group_vars/all/vault.yml
  ```
  
  ## Notes

- Ensure **Python MySQL module (`python3-pymysql`)** is installed for Ansible modules to work.

- Use `become: yes` for tasks requiring root privileges.

- Vault ensures sensitive info like DB passwords are not exposed in playbooks.

- Use `ansible-vault view` to read encrypted files securely.

- Ensure MySQL service is running and accessible on the managed node.

- Use `login_unix_socket: /var/run/mysqld/mysqld.sock` to let Ansible connect as MySQL root via socket (no password needed)

# 5- Automated Host Discovery with Ansible Dynamic Inventory (AWS EC2)

### Objective

Use **Ansible Dynamic Inventory** to automatically discover and manage running EC2 instances on AWS using the `amazon.aws.aws_ec2` plugin, instead of manually defining hosts.

---

### Steps Overview

1. **Create an EC2 Instance**
   
   - Launched a new EC2 (Ubuntu, t3.micro).
   
   - Added a tag: `Name = ivolve`.
   
   - Security group allows SSH (port 22).
   
   - Verified SSH access using:
     
     ```bash
     ssh -i key.pem ubuntu@<public-ip>
     ```

2. **Configure AWS CLI**
   
   ```bash
   aws configure
   ```
   
   - Added Access Key, Secret Key, region (`us-east-1`), and output format (`json`).

3. **Create Dynamic Inventory File (`aws_ec2.yml`)**
   
   ```yaml
   plugin: amazon.aws.aws_ec2
   regions:
    - us-east-1
   filters:
    tag:Name: ivolve
   hostnames:
    - public-ip-address
   compose:
    ansible_host: public_ip_address
   ```
   
   - `filters`: tells Ansible to only include instances tagged `Name=ivolve`.
   
   - `hostnames`: defines how hosts are named in the inventory (by public IP).
   
   - `compose`: maps `ansible_host` to the EC2’s public IP, so Ansible connects via SSH using it.
   
   **Verify Dynamic Inventory**
   
   ```bash
   ansible-inventory -i aws_ec2.yml --graph
   ```
   
   ✅ Output:
   
   ```bas
   @all:
    |--@aws_ec2:
    |  |--public-ip-address
    |--@tag_Name__ivolve:
    |  |--public-ip-address
   ```

4. **Run an Ad-Hoc Command**
   
   ```bash
   ansible -i aws_ec2.yml all -m ping --user ubuntu --private-key ~/path-to-key/key.pem
   ```
   
   ✅ Output:
   
   ```
   public-ip-address | SUCCESS => {
      "ping": "pong"
   }
   ```

5. **Run a Simple Playbook or an Ad-Hoc command (Verification Step)**  
   
   ```bash
   ansible -i aws_ec2.yml all -m shell -a "uptime" --user ubuntu --private-key ~/path-to-key/key.pem
   ```
   
   ```bash
   install htop on the discovered EC2 instance:
   
   ansible -i aws_ec2.yml all -m apt -a "name=htop state=present update_cache=true" \
   --user ubuntu --become --private-key ~/path-to-key/key.pem
   ```
   
   ### Key Takeaways
- Dynamic inventories automatically detect live AWS EC2 instances.

- No need to maintain a static `hosts` file.

- You can filter by region, VPC, or tags.

- Ad-hoc and playbook executions both confirm connectivity.


