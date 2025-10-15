# Automated Web Server Configuration Using Ansible Playbooks

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

                   
