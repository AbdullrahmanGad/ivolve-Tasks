# 📚 Table of Contents
# Ansible
- [1- Initial Ansible Configuration](#1--initial-ansible-configuration)
- [2- Automated Web Server Configuration Using Ansible Playbooks](#2--automated-web-server-configuration-using-ansible-playbooks)
- [3- Structured Configuration Management with Ansible Roles](#3--structured-configuration-management-with-ansible-roles)
- [4- Securing Sensitive Data with Ansible Vault](#4--securing-sensitive-data-with-ansible-vault)
- [5- Automated Host Discovery with Ansible Dynamic Inventory (AWS EC2)](#5--automated-host-discovery-with-ansible-dynamic-inventory-aws-ec2)
# Docker
- [Build-Tools-Tasks](#build-tools-tasks)
- [1- Build and Run Java Spring Boot App Using Maven Base Image](#1--build-and-run-java-spring-boot-app-using-maven-base-image)
- [2- Run Java Spring Boot App Using Java Runtime Only "Optimized"](#2--run-java-spring-boot-app-using-java-runtime-only-optimized)
## 1- Initial Ansible Configuration
<details>
  <summary><strong>Click to expand</strong></summary>
  
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

</details>

## 2- Automated Web Server Configuration Using Ansible Playbooks
<details>
  <summary><strong>Click to expand</strong></summary>
  
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
</details>

## 3- Structured Configuration Management with Ansible Roles
<details>
  <summary><strong>Click to expand</strong></summary> 
  
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

</details>
                   
## 4- Securing Sensitive Data with Ansible Vault
<details>
  <summary><strong>Click to expand</strong></summary>
  
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
</details>

## 5- Automated Host Discovery with Ansible Dynamic Inventory (AWS EC2)
<details>
  <summary><strong>Click to expand</strong></summary>
  
## Objective

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
</details>

# Build-Tools-Tasks
<details>
<summary><strong>1: Building and Packaging Java Applications with Gradle</strong></summary>

### Objective

Learn how to use **Gradle** to build, test, and package a Java application into a `.jar` file.

### Steps

1️⃣ **Install Gradle**

```bash
sudo apt update
sudo apt install -y wget unzip openjdk-17-jdk
wget https://services.gradle.org/distributions/gradle-9.1-bin.zip -P /tmp
sudo mkdir /opt/gradle
sudo unzip -d /opt/gradle /tmp/gradle-9.1-bin.zip
echo 'export PATH=$PATH:/opt/gradle/gradle-9.1/bin' >> ~/.bashrc
source ~/.bashrc
gradle -v
```

2️⃣ **Clone Source Code**

```bash

git clone https://github.com/Ibrahim-Adel15/build1.git
cd build1
```

3️⃣ **Run Unit Tests**

```bash
gradle test
```

4️⃣ **Build the Application**

```bash
gradle build
```

Artifact generated at:

```bash
build/libs/ivolve-app.jar
```

5️⃣ **Run the Application**

```bash
java -jar build/libs/ivolve-app.jar
```

6️⃣ **Verify the Application**

- Check terminal output or visit the app’s port if it’s a web app.

✅ Expected Outcome:

- Gradle installed successfully

- Unit tests pass

- `ivolve-app.jar` created

- App runs successfully

</details>

<details>
<summary><strong>2- Building and Packaging Java Applications with Maven</strong></summary>

### Objective

Learn how to use **Maven** to build, test, and package a Java application into a `.jar` file.

### Steps

1️⃣ **Install Maven**

```bash
sudo apt update
sudo apt install -y wget tar openjdk-17-jdk
wget https://downloads.apache.org/maven/maven-3/3.9.11/binaries/apache-maven-3.9.11-bin.tar.gz -P /tmp
sudo tar xf /tmp/apache-maven-3.9.11-bin.tar.gz -C /opt
echo 'export PATH=$PATH:/opt/apache-maven-3.9.11/bin' >> ~/.bashrc
source ~/.bashrc
mvn -v
```

2️⃣ **Clone Source Code**

```bash
git clone https://github.com/Ibrahim-Adel15/build2.git cd build2
```

3️⃣ **Run Unit Tests**

```bash
mvn test
```

4️⃣ **Build the Application**

```bash
mvn package
```

Artifact generated at:

```bash
target/hello-ivolve-1.0-SNAPSHOT.jar
```

5️⃣ **Run the Application**

```bash
java -jar target/hello-ivolve-1.0-SNAPSHOT.jar
```

6️⃣ **Verify the Application**

- Check terminal output or visit the app’s port if it’s a web app.

✅ Expected Outcome:

- Maven installed successfully

- Unit tests pass

- `hello-ivolve-1.0-SNAPSHOT.jar` created

- App runs successfully
</details>

# Docker Tasks:

## These two tasks contains **two approaches** to containerizing a Java Spring Boot application, showing the impact on **build time** and **image size**.
---
### 1- Build and Run Java Spring Boot App Using Maven Base Image
<details>
<summary><strong>Click to expand</strong></summary>

### **Steps**

1. **Clone the Application**

```bash
git clone https://github.com/Ibrahim-Adel15/Docker-1.git
cd Docker-1
```

2. **Dockerfile**

```dockerfile
FROM maven:sapmachine

WORKDIR /app

COPY . .

RUN mvn package

CMD ["java","-jar","target/demo-0.0.1-SNAPSHOT.jar"]



EXPOSE 8080
```

4. **Build Docker Image**

```bash
docker build -t app1 .

```

- The First build:-
  
  ![d2.jpg](C:\Users\bodey\Desktop\d2.jpg)
- Note the **image size** (usually larger because Maven and build tools are included)
  
  <img src="file:///C:/Users/bodey/Desktop/d3.jpg" title="" alt="d3.jpg" width="681">
5. **Run Container**

```bash
docker run -d -p 8081:8080 --name task-8 app1

```

6. **Test Application**

```bash
curl http://localhost:8081
```

7. **Stop and Delete Container**

```bash
docker stop task-8
docker rm task-8

```

**Observations:**

- **Image Size:** Large (~600-700 MB)

- **Build Time:** Long (Maven build inside image)
</details>
## 2- Run Java Spring Boot App Using Java Runtime Only "Optimized"
<details>
<summary><strong>Click to expand</strong></summary>

### **Steps**

1. **Build the JAR File**

```bash
mvn package
```

2. **Dockerfile**

```dockerfile

FROM eclipse-temurin:17-jdk

WORKDIR /app


COPY Docker-1/target/demo-0.0.1-SNAPSHOT.jar app.jar


EXPOSE 8080

CMD ["java","-jar","app.jar"]

```

3. **Build Docker Image**

```bash
docker build -t app2 .
```

- The First build:- a lot Faster than before.
  
  ![E1.jpg](C:\Users\bodey\Desktop\E1.jpg)
- Note the **image size** (much smaller, ~400 MB)
  
  
  
  ![e11.jpg](C:\Users\bodey\Desktop\e11.jpg)
4. **Run Container**

```bash
docker run -d -p 8081:8080 --name task-9 app2

```

5. **Test Application**

```bash
curl http://localhost:8081

```

6. **Stop and Delete Container**

```bash
docker stop task-9
docker rm task-9
```

**Observations:**

- **Image Size:** Smaller (~400 MB)

- **Build Time:** Faster (JAR already built outside Docker)
  
- [x]  Note:- all files + code are included as .zip in case you don't want to clone.
</details>




