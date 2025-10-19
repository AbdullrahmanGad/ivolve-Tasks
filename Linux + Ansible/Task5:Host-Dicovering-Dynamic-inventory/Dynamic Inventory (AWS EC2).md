# Automated Host Discovery with Ansible Dynamic Inventory (AWS EC2)

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
