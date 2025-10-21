# **3- Multi-Stage Build for a Java Maven App**

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
