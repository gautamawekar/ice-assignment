# Music Metadata Service

A Spring Boot application for managing artist profiles and track metadata.

## Overview

This service stores information about artists and their tracks, exposes REST endpoints for CRUD-style 
artist operations, and supports searching tracks by artist name with pagination. 
It uses Spring Data JPA with an embedded H2 database for local development and testing.

# Assumptions:

### Artist Management:
- There can be more than one Artist with same name.
- Hence, edit can happen only by using artist id.
- Workflow to update artist name: Search for the artist & then update artist by artist_id.

### Tracks Management:
- Tracks can be added against an artist using artist_id.
- Multiple Tracks can be added at any given time.


## Features

- Create artist profiles
- Update an artist name using the artist ID
- Fetch the current artist of the day.
- Resets Artist of the day when all artists have finished showing up.
- Add one or more tracks to an artist
- Search tracks by artist name, with paging support
- H2 database console enabled for local inspection

## Tech Stack

- Java 21
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- H2 in-memory database
- Maven
- JUnit 5 + Mockito for testing

## Project Structure

- `src/main/java/com/ice/controller` — REST controllers
- `src/main/java/com/ice/service` — business logic
- `src/main/java/com/ice/repository` — Spring Data repositories
- `src/main/java/com/ice/entity` — JPA entities
- `src/main/java/com/ice/model` — request/response records
- `src/main/resources/application.yaml` — application configuration
- `src/main/resources/ice_schema.sql` — main schema definition
- `src/test/java` — unit and integration tests

## Prerequisites

- Java 21+
- Maven 3.9+

## Running the Application

From the project root:

```bash
./mvnw spring-boot:run
```

The application starts on:

- localhost:8080

The H2 console is available at:

- http://localhost:8080/h2-console

Default database settings:

- JDBC URL: `jdbc:h2:mem:musicdb`
- Username: `sa`
- Password: empty

You can override these with environment variables:

```bash
DB_URL=jdbc:h2:mem:musicdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE DB_USER=sa DB_PWD= ./mvnw spring-boot:run
```

## API Endpoints

### Create artist

```bash
curl --request POST \
  --url http://localhost:8080/artists \
  --header 'Content-Type: application/json' \
  --data '{
    "artistName": "gautam1"
  }'
```

### Update artist name

```bash
curl --request PUT \
  --url http://localhost:8080/artists \
  --header 'Content-Type: application/json' \
  --data '{
    "id": 1,
    "updatedName": "gautam-updated"
  }'
```

### Get artist of the day

```bash
curl http://localhost:8080/artists/of-the-day
```

### Add tracks to an artist

```bash
curl --request POST \
  --url http://localhost:8080/tracks/artists/1 \
  --header 'Content-Type: application/json' \
  --data '{
    "tracks": [
      { "title": "Song A", "genre": "POP", "length": 180 },
      { "title": "Song B", "genre": "Rock", "length": 210 }
    ]
  }'
```

### Search tracks by artist name (Pagination support)

```bash
curl "http://localhost:8080/tracks/search?artistName=gautam&page=1&size=5"
```

### Error format

```json
{
  "errors": [
    {
      "code": "1001",
      "message": "Artist Name cannot be empty"
    }
  ]
}
```

Each error has a code which identifies which part of the application has failed
- 1000 series is for Artist profile
- 2000 series is for Track management
- 4000 series is for artist of the day

## Notes

- Entries are stored in the H2 database and initialized using `ice_schema.sql`.
- Artist names are validated before create/update.
- Search results for tracks are paginated.

## Testing

Run the test suite with:

```bash
./mvnw test
```

The project includes repository, service, and controller-focused tests to validate behavior around artist profiles and track lookup.

