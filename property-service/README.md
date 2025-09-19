# property-service

This service is part of the Property Management System.

## Description

This service is responsible for managing properties, including creating, retrieving, updating, and deleting property information. It also manages floors and units within a property.

## Getting Started

To get the project on your local machine, you can clone the repository using the following command:

```bash
git clone https://github.com/your-username/property-management.git
```

*Note: Replace `https://github.com/your-username/property-management.git` with the actual URL of your repository.*

## Building

To build the service, run the following command from the service's root directory (`property-service`):

```bash
mvn clean install
```

## Running the Service

After a successful build, you can run the service using:

```bash
java -jar target/property-service-0.0.1-SNAPSHOT.jar
```

The service will start on the port configured in `src/main/resources/application.yml` (default is usually 8080).

## API Endpoints

The following are the API endpoints exposed by the `property-service`.

### Property Management Endpoints

Base Path: `/api/properties`

| Method | Endpoint          | Description                                       |
|--------|-------------------|---------------------------------------------------|
| POST   | `/`               | Create a new property.                            |
| GET    | `/{id}`           | Get a property by ID.                             |
| GET    | `/`               | Get all properties with optional filtering and pagination. |
| PUT    | `/{id}`           | Update an existing property.                      |
| DELETE | `/{id}`           | Delete a property by ID.                          |
| GET    | `/search`         | Search properties by name or address.             |
| GET    | `/{id}/stats`     | Get statistics for a property.                    |
| GET    | `/count`          | Get total number of properties.                   |

### Floor Management Endpoints

Base Path: `/api/floors`

| Method | Endpoint                | Description                               |
|--------|-------------------------|-------------------------------------------|
| POST   | `/`                     | Create a new floor.                       |
| GET    | `/{id}`                 | Get a floor by ID.                        |
| GET    | `/`                     | Get floors by property ID with optional pagination. |
| PUT    | `/{id}`                 | Update an existing floor.                 |
| DELETE | `/{id}`                 | Delete a floor by ID.                     |
| GET    | `/{id}/occupancy-stats` | Get occupancy statistics for a floor.     |
| POST   | `/{id}/refresh-occupancy` | Refresh occupancy statistics for a floor. |

### Unit Management Endpoints

Base Path: `/api/units`

| Method  | Endpoint                      | Description                                       |
|---------|-------------------------------|---------------------------------------------------|
| POST    | `/`                           | Create a new unit.                                |
| GET     | `/{id}`                       | Get a unit by ID.                                 |
| GET     | `/name/{name}`                | Get a unit by name and property ID.               |
| GET     | `/`                           | Get units with filtering and pagination.          |
| PUT     | `/{id}`                       | Update an existing unit.                          |
| DELETE  | `/{id}`                       | Delete a unit by ID.                              |
| PATCH   | `/{id}/occupancy`             | Update unit occupancy status.                     |
| GET     | `/search`                     | Search units by name or tenant.                   |
| GET     | `/property/{propertyId}/income` | Calculate potential rental income for a property. |
| GET     | `/property/{propertyId}/count`  | Count units in a property.                        |

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
