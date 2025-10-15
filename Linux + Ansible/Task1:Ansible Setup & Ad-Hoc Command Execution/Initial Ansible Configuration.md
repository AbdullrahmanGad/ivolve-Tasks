# Initial Ansible Configuration

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
