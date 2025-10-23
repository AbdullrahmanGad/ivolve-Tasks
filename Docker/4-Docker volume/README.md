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

![Docker_volume_1.jpg](F:\Git-hub\pics\Docker-volume\Docker_volume_1.jpg)

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

![Docker_volume_2.jpg](F:\Git-hub\pics\Docker-volume\Docker_volume_2.jpg)

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

![3.jpg](F:\Git-hub\pics\Docker-volume\3.jpg)

---

### **5️⃣ Verify the Web Page**

Access the Nginx page:

```bash
curl http://localhost:8080
```

You should see:

![4.jpg](F:\Git-hub\pics\Docker-volume\4.jpg)

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

![5.jpg](F:\Git-hub\pics\Docker-volume\5.jpg)

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

![6.jpg](F:\Git-hub\pics\Docker-volume\6.jpg)

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
