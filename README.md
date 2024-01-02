![example workflow](https://github.com/ivanolmo/kanban-spring-backend/actions/workflows/build.yml/badge.svg)

# Spring Boot, Spring Data, Spring Security, JWT, PostgreSQL Rest API deployed to AWS

This is a backend API for a kanban task manager built with Spring Boot. The frontend for this API is live and hosted on Vercel, and the repository is located at <https://github.com/ivanolmo/kanban-react-frontend>.

## Steps to Setup

**1. Clone the application**

```
git clone https://github.com/ivanolmo/kanban-spring-backend.git
```

**2. Download and install PostgreSQL database**

Download the correct version for your operating system at:

<https://www.postgresql.org/download/>

**3. Change postgresql username and password to fit your installation**

This repository includes several profiles used for development, testing, and production. The instructions in this guide are for local development:

- open `src/main/resources/application-dev.properties`
- change `spring.datasource.username` and `spring.datasource.password` as per your postgresql installation

Please note that you can also add those items to the `.env` file discussed in the next section. It is always good practice not to hardcode these variables.

**4. Create a file for environment variables**

There are several ways to handle environment variables in a Spring Boot project. For local development, I chose to use a `.env` file with a Spring-specific package called `spring-dotenv`. If you are used to `.env` files in your `js/ts` projects, this will feel very familiar.

Please note that the env variables for my production environment on AWS are handled with a build script that you can see in my GitHub Workflows [here](https://github.com/ivanolmo/kanban-spring-backend/blob/main/.github/workflows/build.yml). Using the `spring-dotenv` package and a `.env` file are for local development only.

In the root folder of the project, create a `.env` file. Please use the included `.env.sample` file to find the required environment variables you will need to run this project locally. The variables include:

- A secret key for JWT verification
- The allowed origin(s) for CORS
- The URL to your local postgresql database
- Your postgresql username
- Your postgresql password

**5. Verify the application profile**

An application profile contains most of the configuration settings you may want in your Spring Boot project. As mentioned previously, this project contains several profiles. For local development, we will use the `application-dev.yml` profile.

As seen in the `application-dev.yml` profile below, there are a total of 5 environment variables that need to be configured. For quick testing, feel free to hardcode your variables directly in the profile, but remember that this is not a good practice.

```bash
security:
  cors:
    allowed-origins: ${CORS_ALLOWED_ORIGINS_DEV}
  jwt:
    secret-key: ${JWT_SECRET_KEY_DEV}
    expiration-time: 86400000
spring:
  datasource:
    url: ${DB_URL_DEV}
    username: ${DB_USER_DEV}
    password: ${DB_PASS_DEV}
    driver-class-name: org.postgresql.Driver
  jpa:
    database: postgresql
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
logging:
  level:
    root: info
management:
  endpoints:
    web:
      exposure:
        include: 'health'
```

The other settings seen in the profile are related to the database, logging, and the Spring Actuator endpoints.

- The `ddl-auto` setting has several options, but I leave it set to `update` for most local development because the database will persist between each application run, and it will update if you make a change to the schema.
- The `show-sql` and `format-sql` settings provide visual feedback in the terminal anytime the database is interacted with.
- The `management.endpoints.web.exposure.include: health` setting enables the [Spring Actuator](https://docs.spring.io/spring-boot/docs/2.5.6/reference/html/actuator.html) health endpoint, which is a fast way to check the status of the API. This is shown below in the enpoint documentation.

**6. Run the app using Gradle**

The following command runs the application from the command line. Notice the active profile being used is the `dev` profile:

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

If you are using an IDE like IntelliJ Idea, you can also run the app using the built-in `Services` tab. If you are using VS Code, you can run the app using the `Spring Boot Dashboard` extension.

The app will start running at <http://localhost:8080>

**7. Test the app using JUnit**

This repository includes unit and integration tests. The coverage is around 50%, and the bulk of the tests revolve around the `services` package.

You can run all tests with the following terminal command:

```bash
./gradlew test
```

As mentioned in the previous section, you can also run these tests using your IDE.

## Explore the Kanban Task Manager API

This API requires a valid JWT token be sent with every request (except for the auth endpoints). This means that if you are using a client like Postman to test, you first need to acquire a valid token by sending a request to the `register` endpoint to create an account, then the `login` endpoint to get a valid token.

For example, here is a sample request body sent to the `http://localhost/8080/api/v1/auth/login` endpoint:

```bash
{
  "email": "test@test.com",
  "password": "asdf"
}
```

Those are valid credentials in my local development environment, so Postman receives the following response from the API:

```bash
{
    "data": {
        "user_id": "c0fe0f74-ba58-408f-b7b1-e5b79832e824",
        "email": "test@test.com",
        "access_token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIiwiaWF0IjoxNzA0MTU2NDkyLCJleHAiOjE3MDQyNDI4OTJ9.f4iuxPVp1W4C4bzZOKoBmPdBLpUjxFRZCo-3aXwj2BI"
    },
    "success": true,
    "message": "Successful user login",
    "error": null,
    "status": "OK"
}
```

The field `access_token` contains a valid token for that user and must be included as a `Bearer` token in every request.

The following endpoints are available:

### Auth

| Method | URL                   | Decription | Sample Valid Request Body |
| ------ | --------------------- | ---------- | ------------------------- |
| POST   | /api/v1/auth/register | Register   | [JSON](#register)         |
| POST   | /api/v1/auth/login    | Log in     | [JSON](#login)            |

### Boards

| Method | URL                 | Description                                                                                          | Sample Valid Request Body |
| ------ | ------------------- | ---------------------------------------------------------------------------------------------------- | ------------------------- |
| GET    | /api/v1/boards      | Get logged in users boards                                                                           | N/A                       |
| POST   | /api/v1/boards      | Add a new board to the logged in users boards                                                        | [JSON](#boardcreate)      |
| PUT    | /api/v1/boards/{id} | Update an existing board (updates a board name and/or columns)                                       | [JSON](#boardupdate)      |
| DELETE | /api/v1/boards/{id} | Delete an existing board (cascades to delete all board data, including columns, tasks, and subtasks) | N/A                       |

### Tasks

| Method | URL                 | Description                                                                                          | Sample Valid Request Body |
| ------ | ------------------- | ---------------------------------------------------------------------------------------------------- | ------------------------- |
| POST   | /api/v1/tasks       | Create a new task and add it to a board column                                                       | [JSON](#taskcreate)       |
| PUT    | /api/v1/tasks/{id}  | Update an existing task (includes updating the task data or the column to which it belongs)          | [JSON](#taskupdate)       |
| DELETE | /api/v1/tasks/{id}  | Delete an existing task (cascades to delete all task data, including child subtasks)                 | N/A                       |

### Subtasks

| Method | URL                   | Description                                               | Sample Valid Request Body |
| ------ | --------------------- | --------------------------------------------------------- | ------------------------- |
| PUT    | /api/v1/subtasks/{id} | Toggle subtask completion status (incomplete -> complete) | N/A                       |

### Miscellaneous

| Method | URL                           | Description                                                                                                                                                | Sample Valid Request Body |
| ------ | ----------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------- |
| GET    | /api/v1/actuator/health       | Provides a simple health check of the API. It returns a JSON body with the status `{"status": "UP"}` if the API is running properly.                       | N/A                       |
| GET    | /api/v1/v3/api-docs           | This endpoint uses OpenAPI to present detailed documentation of all the API's endpoints. It returns a detailed JSON description of the entire API.         | N/A                       |
| GET    | /api/v1/swagger-ui/index.html | Displays a visual and interactive documentation of the API endpoints using Swagger UI, which is useful for exploring and testing the API in a simpler way. | N/A                       |

## Sample Valid JSON Request Bodies

##### <a id="register">Register -> /api/v1/auth/register</a>

```json
{
  "email": "string",
  "password": "string"
}
```

##### <a id="login">Log In -> /api/v1/auth/login</a>

```json
{
  "email": "string",
  "password": "string"
}
```

##### <a id="boardcreate">Create Board -> /api/v1/boards</a>

```json
{
  "name": "string",
  "columns": [
    {
      "name": "string",
      "color": "#9489Df"
    }
  ]
}
```

##### <a id="boardupdate">Update Board -> /api/v1/boards/{id}</a>

```json
{
  "id": "string",
  "name": "string",
  "columns": [
    {
      "id": "string",
      "name": "string",
      "color": "#8fceC3",
      "tasks": [
        {
          "id": "string",
          "title": "string",
          "description": "string",
          "subtasks": [
            {
              "id": "string",
              "title": "string",
              "completed": true
            }
          ],
          "columnId": "string"
        }
      ]
    }
  ]
}
```

##### <a id="taskcreate">Create Task -> /api/v1/tasks</a>

```json
{
  "task": {
    "title": "string",
    "description": "string",
    "subtasks": [
      {
        "title": "string",
        "completed": false
      }
    ]
  },
  "columnId": "string"
}
```

##### <a id="taskupdate">Update Task -> /api/v1/tasks/{id}</a>

```json
{
  "task": {
    "id": "string",
    "title": "string",
    "description": "string",
    "subtasks": [
      {
        "id": "string",
        "title": "string",
        "completed": true
      }
    ],
    "columnId": "string"
  },
  "columnId": "string"
}
```

If you have any questions about building this project locally or have any issues, please feel free to create a new issue!