# user-service

This service is part of the Property Management System.

## Description

This service manages user-related operations, such as user registration, authentication, and profile management.

## Getting Started

To get the project on your local machine, you can clone the repository using the following command:

```bash
git clone https://github.com/your-username/property-management.git
```

*Note: Replace `https://github.com/your-username/property-management.git` with the actual URL of your repository.*

## Building

To build the service, run the following command from the service's root directory (`user-service`):

```bash
mvn clean install
```

## Running the Service

After a successful build, you can run the service using:

```bash
java -jar target/user-service-0.0.1-SNAPSHOT.jar
```

The service will start on the port configured in `src/main/resources/application.yml` (default is usually 8080).

## API Endpoints

The following are the API endpoints exposed by the `user-service`.

### Authentication Endpoints

Base Path: `/api/auth`

| Method | Endpoint        | Description                               |
|--------|-----------------|-------------------------------------------|
| POST   | `/sign-in`      | Authenticates a user and returns a JWT token. |
| POST   | `/signup`       | Registers a new user.                     |
| GET    | `/me`           | Gets the current user.                    |

### User Management Endpoints

Base Path: `/api/users`

| Method | Endpoint          | Description                     |
|--------|-------------------|---------------------------------|
| GET    | `/`               | Get all users (Admin only).     |
| GET    | `/{id}`           | Get user by ID.                 |
| GET    | `/by-username`    | Get user by username.           |
| POST   | `/by-ids`         | Get users by IDs.               |
| DELETE | `/{id}`           | Delete user (Admin only).       |
| POST   | `/`               | Create a new user (Admin only). |
| PUT    | `/{id}`           | Update a user.                  |

## Configuration

Configuration for the service can be found in `src/main/resources/application.yml`.

## Collaboration

I am open to collaborations! If you are interested in contributing to this project, please follow these steps:

1.  Fork the repository.
2.  Create a new branch for your feature or bug fix: `git checkout -b feature-name`
3.  Make your changes and commit them with a descriptive message.
4.  Push your changes to your forked repository.
5.  Open a pull request to the `main` branch of the original repository.

I will review your pull request as soon as possible. Thank you for your interest in collaborating!
