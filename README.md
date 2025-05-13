# Complaint API

This application provides an API for managing complaints. It allows users to add, view, and manage complaints related to products. The API also includes functionality to check for duplicate complaints based on the `product_id` and `submitted_by`.

## Features

- Create new complaints
- View complaints
- Handle duplicates by updating the `submission_count`
- Provides Swagger UI documentation for easy API exploration

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
3. Right-click on the main class (e.g., `RecruitmentTaskApplication.java`) and select **Run**.
4. The application should start running on **localhost:8080**.

### 2. **Running via Docker Compose**

You can run the application and the PostgreSQL database using Docker Compose. This allows you to avoid manually setting up the database.

#### Steps to run via Docker Compose:

1. Ensure Docker is running on your machine.
2. In the project root directory, create a `.env` file to store environment variables (e.g., database URL).
3. Ensure the `docker-compose.yml` and `Dockerfile` are in the root directory.
4. From the project root, run the following command:

    ```bash
    docker-compose up --build
    ```

5. The application and the PostgreSQL database will be spun up in Docker containers. You can access the API at **http://localhost:8080**.

#### Configuration:

- **Database**: PostgreSQL will be set up automatically by Docker Compose.
- **Ports**:
    - Application: **8080**
    - PostgreSQL: **5432**

### 3. **Swagger UI**

Swagger UI is available for API documentation.

- **Access Swagger UI** at: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Here, you can view all the API endpoints and interact with the API directly.

---

## API Endpoints

### 1. **Create a New Complaint**

- **Endpoint**: `POST /api/complaints`
- **Request Body**:
    ```json
    {
        "productId": 123,
        "content": "Complaint content",
        "submittedBy": "John Doe",
        "country": "USA"
    }
    ```
- **Response**:
    - `201 Created` if the complaint is successfully created.
    - If a duplicate complaint is found, the `submission_count` will be incremented.

### 2. **Get All Complaints**

- **Endpoint**: `GET /api/complaints`
- **Response**: List of all complaints in the system, with pagination and sorting by `created_at`.

### 3. **Get Complaint by ID**

- **Endpoint**: `GET /api/complaints/{id}`
- **Response**: Returns the complaint details by ID.

---

## Docker Configuration

### Dockerfile

To build and run the application using Docker, we have included a `Dockerfile` that sets up the application container.

#### Example `Dockerfile`:

```Dockerfile
# Use a Java base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the jar file into the container
COPY target/recruitment-task.jar app.jar

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
