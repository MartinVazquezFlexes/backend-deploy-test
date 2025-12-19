# Back Portal Recluting

# Index

- [Description](#description)
- [Technologies Used](#technologies-used)
- [Project Structure](#project-structure)
- [Installation and Configuration](#installation-and-configuration)
- [Main Endpoints](#main-endpoints)

## 📌 Description

Back Portal Recruiting is a REST API developed with Spring Boot, which provides functionalities for authentication via
Firebase and employee recruitment management. It connects to MySQL as the database and uses JWT for secure
authentication.

## 🚀 Technologies Used

- **Spring Boot 3.4.3** - Main framework
- **Spring Security** - Security and authentication
- **Firebase Authentication** - User management and authentication
- **JWT (JSON Web Token)** - Token validation
- **Spring Data JPA** - Database interaction
- **MySQL** - Relational database
- **Lombok** - Reduction of boilerplate code

## 📂 Project Structure

```plaintext
back-portal-recruiting/
│── src/main/java/com/techforb/apiportalrecruiting/
│   ├── core/          
│   │   ├── config/      
│   │   ├── exceptions/  
│   │   ├── security/      
│   │   ├── utils/        
│   │   └── dtos/ 
│   ├── modules/
│   │   ├── portal/
│   │   │   ├── applications/
│   │   │   ├── home/
│   │   │   └── vacancies/
│   │   ├── backoffice/
│   │   │   ├── applications/
│   │   │   ├── dashboard/
│   │   │   └── vacancies/
│
│── src/main/resources/
│   ├── application.properties
│   ├── application-dev.properties
│   └── application-prod.properties
```

## 📜 Installation and Configuration

### 1️⃣ Clone the repository

```plaintext
git clone https://github.com/diegosanchezschenone/techforb-recruting-backend.git
cd back-portal-recluting
```

### 2️⃣ Configure Firebase

Go to the Firebase Console.

Create a new project and enable Authentication with the method you prefer (email, Google, etc.).

Download the serviceAccountKey.json file from the Firebase configuration tab.

Save the file in src/main/resources/firebase/.

### 3️⃣ Configure MySQL

Create a databse in MySQL:
```
CREATE DATABASE portal_recluting;
```
Modify application.properties:

```plaintext
spring:
datasource:
url: jdbc:mysql://localhost:3306/portal_recruiting1sword
jpa:
hibernate:
ddl-auto: update
```
### 4️⃣ Execute the application

The API will be available at: http://localhost:8080


### Build and Run (Docker)
To compile and run the application with Docker Compose, follow these steps:

1. **Navigate to the project's root directory**
   ```sh
   cd /path/to/project
   ```

2. **Compile the project and generate the package** (skipping tests to speed up the process):
   ```sh
   mvn clean package -DskipTests
   ```

3. **Start the containers with Docker Compose** (building images if necessary):
   ```sh
   docker-compose up --build
   ```

## Verification
Once the services are running, you can check their status with:

```sh
docker ps
```

To view the application logs, run:

```sh
docker-compose logs -f
```

### Accessing the Application
Once the application is running, it will be available at:

```
http://localhost:8080
```

You can open this link in your browser to check that everything is working correctly.

## Stopping the Application
To stop and remove the containers, run:

```sh
docker-compose down
```

If you want to remove associated volumes, use:

```sh
docker-compose down -v
```

## Additional Notes
- To completely rebuild images without using the cache, use:
  ```sh
  docker-compose build --no-cache && docker-compose up
  ```

### Applying Changes to the Application
If you make changes to the project's code and want to see them reflected in the application, follow these steps:

1. **Recompile the project:**
   ```sh
   mvn clean package -DskipTests
   ```

2. **Restart the containers with the applied changes:**
   ```sh
   docker-compose up --build
   ```

This will ensure that the application reflects the latest changes in the source code.

## 🛠️ Git Flow Convention

```

ADD: agregar funcionalidades
CHG: cambiar funcionalidades
DEL: eliminar funcionalidades
fix: corregir errores
refactor: se mejora el código sin cambiar su funcionalidad
docs: cambios en la documentación
test: agregar o modificar pruebas
style: cambios de formato o estilo (sin afectar funcionalidad)
revert: revertir un commit previo
```

## 🛠️ Main Endpoints

### **1. Register with Google**

- **Path:** `/api/auth/register-google`
- **Method:** POST
- **Description:** Allows a user to register using Google authentication.
- **Data (JSON):**

```json
{
  "email": "user@example.com"
}
```

- **Response Example:**

```json
{
  "token": "new_token",
  "role": "POSTULANT"
}
```

