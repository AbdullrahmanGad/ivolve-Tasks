## These Three tasks contains **Three approaches** to containerizing a Java Spring Boot application, showing the impact on **build time** and **image size**.
---
- [1-Java Maven Build in Docker](#1--java-maven-build-in-docker)
- [2-Dockerizing a Pre-Built Spring Boot App](#2--dockerizing-a-pre-built-spring-boot-app)
- [3- Multi-Stage Build for a Java Maven App](#3--multi-stage-build-for-a-java-maven-app)

### 1-Java Maven Build in Docker
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
![d2](https://github.com/user-attachments/assets/f32502a0-801c-4385-a866-373d301b554f)

- Note the **image size** (usually larger because Maven and build tools are included)
![d3](https://github.com/user-attachments/assets/4004e3e7-aa81-4a99-83a4-1017de3c1079)
  
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

### 2-Dockerizing a Pre-Built Spring Boot App
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

The First build:- a lot Faster than before.
-![E1](https://github.com/user-attachments/assets/911ebf8c-ef3e-4a76-88a5-280882b0494d)
 
- Note the **image size** (much smaller, ~400 MB)
![e11](https://github.com/user-attachments/assets/61a7f575-3ee7-4026-8dc9-66b97a7a6269)
   
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

### **3- Multi-Stage Build for a Java Maven App**
<details>
<summary><strong>Click to expand</strong></summary>
This Task demonstrates how to use **Docker multi-stage builds** to create lightweight production images for Java applications.  
The project uses **Maven** to build the application and **Temurin JDK** to run it.

---

## **Objectives**

- Learn how to use Docker **multi-stage builds** for optimization.

- Understand how to separate build and runtime environments.

- Compare image sizes between full Maven builds and optimized runtime images.

---

## **📂 Application Overview**

This is a simple Java Spring Boot application that runs on port **8080** and displays a message confirming the app is running.

---

## **Dockerfile (Multi-Stage Build)**

```dockerfile
# ---------- Build Stage ----------
FROM maven:sapmachine AS build

WORKDIR /app


COPY . .

RUN mvn package

# ---------- Run Stage ----------
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
```

---

## **Steps**

### **1️⃣ Clone the Repository**

```bash
git clone https://github.com/Ibrahim-Adel15/Docker-1.git
cd Docker-1
```

---

### **2️⃣ Build the Multi-Stage Image**

```bash
docker build -t app3-multistage .
```

✅ **Note the image size** — it should be much smaller than a single-stage build, because only the JAR file and runtime dependencies are included.

![a2.jpg](C:\Users\bodey\Desktop\a2.jpg)

---

### **3️⃣ Run the Container**

```bash
docker run -d -p 8080:8080 --name app3 app3-multistage
```

---

### **4️⃣ Test the Application**

Use `curl` or your browser:

```bash
curl localhost:8080
```

✅ Expected output:

```
Hello from Spring Boot! (or similar app response)
```

---

### **5️⃣ Stop and Remove the Container**

```bash
docker stop app3
docker rm app3
```
</details>

