# 📝 Complaint Management System

A simple **Spring Boot** REST API for managing **Customers** and their **Complaints**.

## 🚀 Features

- Manage **Customers** with name & email
- Manage **Complaints** linked to Customers
- Pagination, Sorting, and Filtering support
- Input validation and error handling
- Kafka & Kafka UI implementation
- Swagger UI for API documentation
- Integration tests with Testcontainers
- Dockerized with PostgreSQL database

## 🛠️ Technologies

- Java 24+ & Spring Boot 3
- Spring Data JPA & Hibernate
- PostgreSQL (Dockerized)
- Kafka & UI
- Spring Security (basic auth)
- Swagger/OpenAPI UI
- Testcontainers for integration tests
- Docker & Docker Compose
- Elasticsearch – Search and analytics engine for logs. 
- Logstash – Collects and processes logs from the application.
- Kibana – Web interface for visualizing logs.

## Prerequisites

Before running the application, ensure you have the following installed:

- **Java 24 or higher**
- **Docker** (optional, for running the app in a container)
- **IntelliJ IDEA** (or any Java IDE)
- **PostgreSQL** (if not using Docker)

## Running the Application

### 1. **Running via IntelliJ IDEA**

To run the application directly from IntelliJ IDEA:

1. Open the project in IntelliJ IDEA.
2. Navigate to the `src/main/java/pl/kkozera/recruitment_task/` directory.
3. Import project as Maven/Gradle project.
2. Ensure **PostgreSQL** is running locally on port 5432 with these credentials:
    - DB: complains
    - User: appuser
    - Password: apppass
    - you can use this command if using docker:
    ```bash
       docker run -d --name my-postgres -e POSTGRES_USER=appuser -e POSTGRES_PASSWORD=apppass -e POSTGRES_DB=complains -p 5432:5432 postgres:15
    ```
3. Ensure **Kafka** is running locally on port 9092
4. Right-click on the main class (e.g., `RecruitmentTaskApplication.java`) and select **Run**.
5. The application should start running on **localhost:8080**.

### 2. **Running via Docker Compose**

You can run the application, kafka with zookeeper, kafka UI and the PostgreSQL database using Docker Compose. This
allows you to avoid manually setting up the database.

#### Steps to run via Docker Compose:

1. Ensure Docker is running on your machine.
2. Ensure the `docker-compose.yml` and `Dockerfile` are in the root directory.
3. From the project root, run the following commands:
    ```bash
    ./gradlew clean build
    ```
    ```bash
    docker-compose up --build
    ```

4. The application and the other dependencies will be spun up in Docker containers. You can access the API at **http://localhost:8080**.
5. Use /login page to login using these credentials:
    - username: user
    - password: fetch the password from logs of the application

#### Configuration:

- **Database**: PostgreSQL will be set up automatically by Docker Compose.
- **Ports**:
    - Application: **8080**
    - PostgreSQL: **5432**

### 3. **Swagger UI**

Swagger UI is available for API documentation.

- **Access Swagger UI** at: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Here, you can view all the API endpoints and interact with the API directly.

### 4. 📊 Kafka UI (Kafdrop)

Kafka messages can be viewed through the Kafka web UI:

URL: http://localhost:9000

Use this to inspect topics, messages, consumer groups, etc.

### 5. 📊 Viewing Logs in Kibana
Open http://localhost:5601

Go to Management → Stack Management → Index Patterns

Create an index pattern (e.g., app-logs-*)

Use Discover to explore logs in real time

---