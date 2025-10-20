# Docker Tasks:

### These two tasks contains **two approaches** to containerizing a Java Spring Boot application, showing the impact on **build time** and **image size**.
---

## **1: Build and Run Java Spring Boot App Using Maven Base Image**

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
  
  ![d2.jpg](C:\Users\bodey\Desktop\d2.jpg)
- Note the **image size** (usually larger because Maven and build tools are included)
  
  <img src="file:///C:/Users/bodey/Desktop/d3.jpg" title="" alt="d3.jpg" width="681">
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

---

## **2: Run Java Spring Boot App Using Java Runtime Only (Optimized)**

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

- The First build:- a lot Faster than before.
  
  ![E1.jpg](C:\Users\bodey\Desktop\E1.jpg)
- Note the **image size** (much smaller, ~400 MB)
  
  
  
  ![e11.jpg](C:\Users\bodey\Desktop\e11.jpg)
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
  
  

- [x]  Note:- all file and code are included as .zip in case you don't want to clone.




