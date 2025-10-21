# 4- Securing Sensitive Data with Ansible Vault
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

