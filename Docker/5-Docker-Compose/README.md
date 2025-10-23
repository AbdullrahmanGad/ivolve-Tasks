# Containerized Node.js and MySQL Stack Using Docker Compose

---

This project demonstrates how to containerize a simple **Node.js web application** and a **MySQL database** using **Docker Compose**.  
The setup builds the app from source, connects it to a MySQL database, persists data using Docker volumes, and pushes the built image to DockerHub.

## 📁 Project Structure

.  
├── Dockerfile  
├── docker-compose.yml  
├── logs/  
├── frontend/
├── server.js
├── db.js
├── package.json  
└── README.md

---

## Setup Steps

### 1️⃣ Clone the Source Code

```bash
git clone https://github.com/Ibrahim-Adel15/kubernets-app.git
cd kubernets-app
```

---

### 2️⃣ Create the Dockerfile

```dockerfile
FROM node:18

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .

EXPOSE 3000

CMD ["npm", "start"]
```

---

### 3️⃣ Create `docker-compose.yml`

```yaml
version: "3.9"

services:
  db:
    image: mysql:5.7
    container_name: mysql-db
    environment:
      MYSQL_ROOT_PASSWORD: 12345
      MYSQL_DATABASE: ivolve
      MYSQL_USER: user
      MYSQL_PASSWORD: 1234
    volumes:
      - db_data:/var/lib/mysql
    ports:
      - "3306:3306" 
  app:
    build: .
    container_name: node-app
    ports:
      - "3000:3000"
    environment:
      DB_HOST: db
      DB_USER: user
      DB_PASSWORD: 1234
    depends_on:
      - db
    volumes:
      - ./logs:/app/logs  

volumes:
  db_data:
```

---

### 4️⃣ Build and Run the Stack

```bash
docker-compose up -d --build
```

![3](https://github.com/user-attachments/assets/99f46d5e-dd7b-417d-b444-9f577eb9688d)

Check running containers:

```bash
docker ps
```

![8](https://github.com/user-attachments/assets/a7577f02-9ccc-400c-a31e-f711fa217bfa)

---

## ✅ Verification

### 1. Access the App

Open in your browser:

```
http://localhost:3000
```

![4](https://github.com/user-attachments/assets/a03cb0e4-b289-43f6-983f-5040f4df706e)

### 

### 2. Health Endpoints

Check:

```
http://localhost:3000/health
http://localhost:3000/ready
```
![5](https://github.com/user-attachments/assets/08a4d062-001e-419d-b508-3849f9ad8fcf)![6](https://github.com/user-attachments/assets/6e318991-68e0-4681-906b-c84d22ec51f5)

### 3. View Live Logs

```bash
docker-compose logs -f app
```

or from the mounted folder:

```bash
tail -f logs/access.log
```

![7 5](https://github.com/user-attachments/assets/0c99a970-b75a-4720-b160-bafd3ce2a681)

---

## Data Persistence

MySQL data is stored in a named volume:

```
db_data:/var/lib/mysql
```

This ensures the database remains even if the container restarts.

---

## Push App Image to DockerHub

### Tag the Image

```bash
docker tag kubernets-app_app:latest yourdockerhubusername/kubernets-app:latest
```

### Login & Push

```bash
docker login
docker push yourdockerhubusername/kubernets-app:latest
```

![7](https://github.com/user-attachments/assets/5bc099eb-1510-40b0-a756-3b1a29e7f803)


Verify at:  
https://hub.docker.com/repositories

---

## Summary

- Built and containerized a Node.js + MySQL stack

- Used Docker Compose for multi-container orchestration

- Persisted database data using volumes

- Verified app health and logs

- Pushed the final image to DockerHub

