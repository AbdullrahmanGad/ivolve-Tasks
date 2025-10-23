# Build-Tools-Tasks
## Building and Packaging Java Applications with Gradle:-

### Objective

Learn how to use **Gradle** to build, test, and package a Java application into a `.jar` file.

### Steps

1️⃣ **Install Gradle**

```bash
sudo apt update
sudo apt install -y wget unzip openjdk-17-jdk
wget https://services.gradle.org/distributions/gradle-9.1-bin.zip -P /tmp
sudo mkdir /opt/gradle
sudo unzip -d /opt/gradle /tmp/gradle-9.1-bin.zip
echo 'export PATH=$PATH:/opt/gradle/gradle-9.1/bin' >> ~/.bashrc
source ~/.bashrc
gradle -v
```

2️⃣ **Clone Source Code**

```bash

git clone https://github.com/Ibrahim-Adel15/build1.git
cd build1
```

3️⃣ **Run Unit Tests**

```bash
gradle test
```

4️⃣ **Build the Application**

```bash
gradle build
```

Artifact generated at:

```bash
build/libs/ivolve-app.jar
```

5️⃣ **Run the Application**

```bash
java -jar build/libs/ivolve-app.jar
```

6️⃣ **Verify the Application**

- Check terminal output or visit the app’s port if it’s a web app.

✅ Expected Outcome:

- Gradle installed successfully

- Unit tests pass

- `ivolve-app.jar` created

- App runs successfully

