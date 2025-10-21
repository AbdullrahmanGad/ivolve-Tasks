# **3- Managing Docker Environment Variables Across Build and Runtime**

This Task demonstrates how to manage environment variables in Docker across **build time** and **runtime**, using a simple Flask web application.

---

## **🧠 Objectives**

- Understand how to pass environment variables to Docker containers.

- Set variables using:
  
  - Command-line (`docker run -e`)
  
  - Environment file (`--env-file`)
  
  - Inside the Dockerfile (`ENV` instruction)

---

## **📂 Application Overview**

A basic Flask application prints the current environment configuration:

```python
# app.py
import os
from flask import Flask

app = Flask(__name__)

@app.route('/')
def show_env():
    mode = os.getenv("APP_MODE", "default")
    region = os.getenv("APP_REGION", "unknown")
    return f"App mode: {mode}, Region: {region}"

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8080)
```

---

## **<u>*Dockerfiles*</u>**

### **Dockerfile 1 — Runtime Variables**

Used for passing variables during container runtime.

```dockerfile
FROM python:3.12-slim

WORKDIR /app

COPY . .

RUN pip install flask

EXPOSE 8080

CMD ["python", "app.py"]
```

---

### **Dockerfile 2 — Build-Time Variables**

Used for defining variables within the image.

```dockerfile
FROM python:3.12-slim

WORKDIR /app

ENV APP_MODE=production
ENV APP_REGION=canada-west

COPY . .

RUN pip install flask

EXPOSE 8080

CMD ["python", "app.py"]
```

---

## **Steps**

### **1️⃣ Clone the Repository**

```bash
git clone https://github.com/Ibrahim-Adel15/Docker-3.git
cd Docker-3
```

---

### **2️⃣ Build Docker Image**

```bash
docker build -t flaskapp:latest -f Dockerfile1 .
```

---

### **3️⃣ Run Container with Environment Variables (Command Line)**

```bash
docker run -d -p 8080:8080 --name flaskapp-1 \
  -e APP_MODE=development \
  -e APP_REGION=us-east \
  flaskapp:latest
```

✅ Output:

```
App mode: development, Region: us-east
```

---

### **4️⃣ Run Container Using Environment File**

Create a file named `.env`:

```
APP_MODE=staging
APP_REGION=us-west
```

Then run:

```bash
docker run -d -p 8081:8080 --name flaskapp-2 --env-file .env flaskapp:latest
```

✅ Output:

```
App mode: staging, Region: us-west
```

---

### **5️⃣ Build Image with Predefined Variables (Inside Dockerfile)**

```bash
docker build -t flask-w-env -f Dockerfile2 .
docker run -d -p 8082:8080 --name flaskapp-3 flask-w-env
```

✅ Output:

```
App mode: production, Region: canada-west
```



![runs.jpg](C:\Users\bodey\Desktop\runs.jpg)
