# api-gateway

This service is part of the Property Management System.

## Description

The API Gateway is the single entry point for all clients. It handles routing, rate limiting, security, and other cross-cutting concerns. It uses Spring Cloud Gateway to route requests to the appropriate microservices.

## Getting Started

To get the project on your local machine, you can clone the repository using the following command:

```bash
git clone https://github.com/your-username/property-management.git
```

*Note: Replace `https://github.com/your-username/property-management.git` with the actual URL of your repository.*

## Building

To build the service, run the following command from the service's root directory (`api-gateway`):

```bash
mvn clean install
```

## Running the Service

After a successful build, you can run the service using:

```bash
java -jar target/api-gateway-0.0.1-SNAPSHOT.jar
```

The service will start on port 8080 as configured in `src/main/resources/application.yml`.

## Routing

The API Gateway routes requests to the downstream services based on the request path.

| Path Pattern                                       | Service Routed To  |
|----------------------------------------------------|--------------------|
| `/api/users/**`, `/api/auth/**`                    | `user-service`     |
| `/api/properties/**`, `/api/floors/**`, `/api/units/**` | `property-service` |

The gateway also exposes the OpenAPI documentation for the services. You can access the Swagger UI at [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) to view and interact with the APIs of all the services.

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
