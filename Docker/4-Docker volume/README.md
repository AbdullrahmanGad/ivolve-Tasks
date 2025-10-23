## Docker Volume and Bind Mount with Nginx

### Objective

Understand the difference between **Docker Volumes** and **Bind Mounts** by running an Nginx container that:

- Persists its **logs** using a Docker Volume.

- Serves a **custom HTML page** from a Bind Mount directory on the host.

---

## Steps

### **1️⃣ Create a Docker Volume**

Create a volume named `nginx_logs` to store Nginx logs persistently:

```bash
docker volume create nginx_logs
```

Verify it was created:

```bash
docker volume ls
```

```bash
docker volume inspect nginx_logs
```

![Docker_volume_1](https://github.com/user-attachments/assets/7190f9fe-f7c4-4303-bddb-cdc83e5e7e12)

---

### **2️⃣ Create a Directory for Bind Mount**

```bash
mkdir -p nginx-bind/html
```

---

### **3️⃣ Create a Custom HTML Page**

```bash
echo "Hello from Bind Mount" > nginx-bind/html/index.html
```

![Docker_volume_2](https://github.com/user-attachments/assets/16f55767-c967-4974-b423-f7f85a218a9f)

---

### **4️⃣ Run the Nginx Container**

Run Nginx with:

- A **Volume** for `/var/log/nginx`

- A **Bind Mount** for `/usr/share/nginx/html`

- Port mapping `8080:80`

```bash
docker run -d --name nginx-test \
  -v nginx_logs:/var/log/nginx \
  -v "$(pwd)/nginx-bind/html:/usr/share/nginx/html" \
  -p 8080:80 nginx
```

![3](https://github.com/user-attachments/assets/5e65bcd0-9adb-405d-ac6d-327609f9d8ec)

---

### **5️⃣ Verify the Web Page**

Access the Nginx page:

```bash
curl http://localhost:8080
```

You should see:

![4](https://github.com/user-attachments/assets/390dcac3-dede-44ed-99df-09b4f59248c4)

---

### **6️⃣ Update HTML and Verify Live Change**

Edit your local file:

```bash
echo "Hello again from Bind Mount!" > nginx-bind/html/index.html
```

Run curl again:

```bash
curl http://localhost:8080
```

![5](https://github.com/user-attachments/assets/aa5d87c6-daae-4f25-bb54-bc06ec7c5255)

---

### **7️⃣ Check Persisted Logs**

Verify that logs are written inside the container:

```bash
docker exec -it nginx-test ls /var/log/nginx
```

Now confirm they exist on the host volume path:

```bash
sudo ls /var/lib/docker/volumes/nginx_logs/_data
```

![6](https://github.com/user-attachments/assets/1616657e-968a-428e-86e4-601c5da2f3e3)

---

### **8️⃣ Clean Up**

Stop and remove the container:

```bash
docker rm -f nginx-test
```

Remove the volume:

```bash
docker volume rm nginx_logs
```

---

## Summary

| Type           | Mounted Path            | Purpose                | Behavior                              |
| -------------- | ----------------------- | ---------------------- | ------------------------------------- |
| **Volume**     | `/var/log/nginx`        | Store logs             | Data persists after container removal |
| **Bind Mount** | `/usr/share/nginx/html` | Serve local HTML files | Live sync with host filesystem        |

---

## ✅

- Learned how **Docker Volumes** persist data across containers.

- Understood how **Bind Mounts** sync host files in real time.

- Verified both concepts using Nginx web server and logs.

