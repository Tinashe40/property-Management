# eureka-server

This service is part of the Property Management System.

## Description

The Eureka Server is used for service discovery. All microservices register themselves with the Eureka Server, which allows them to find and communicate with each other without hardcoding hostnames and ports.

## Getting Started

To get the project on your local machine, you can clone the repository using the following command:

```bash
git clone https://github.com/your-username/property-management.git
```

*Note: Replace `https://github.com/your-username/property-management.git` with the actual URL of your repository.*

## Building

To build the service, run the following command from the service's root directory (`eureka-server`):

```bash
mvn clean install
```

## Running the Service

After a successful build, you can run the service using:

```bash
java -jar target/eureka-server-0.0.1-SNAPSHOT.jar
```

The service will start on port 8761 as configured in `src/main/resources/application.yml`.

## Eureka Dashboard

Once the server is running, you can access the Eureka dashboard in your browser to see the registered services.

**URL:** [http://localhost:8761/](http://localhost:8761/)

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
