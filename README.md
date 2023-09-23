# Explore with me

## Functionality

The application is a poster in which users can offer events and gather a company to participate in it, or find an event in which they themselves want to participate.

### The program has two microservices:

1. ewm-main - contains all the logic, including working with the database, handling exceptional cases, etc.
2. ewm-stat - stores the number of views and allows you to make various selections to analyze the operation of the application

_The main service contains three layers of application management:_

1. Public:

   - getting compilations of events with filtering capability or get compilation by ID
   - getting all event categories or get category by ID
   - getting events with filtering capability or get specific event by ID
   - getting a list of comments on a specific event with text filtering

2. Private:

   - creating, receiving and updating an event that you are the initiator of
   - confirmation or rejection of requests for participation in your event
   - sending an requests (with the possibility of subsequent cancellation) for participation in someone else's event
   - the ability to create, update, delete and get comments on your or someone else's event

3. Admin:

   - creating, updating, and deleting event categories
   - search and edit any events
   - creating, updating, and deleting compilations of events
   - adding, deleting and receiving information about any user
   - viewing and deleting any comments

_The stat service performs the following functionality:_

- contains a record of information that a request to the API endpoint was processed - timestamp, IP-address, url
- providing statistics for selected dates for the selected endpoint

_You can read more about the specification below:_

[main service](https://github.com/isthatkirill/ewm-spring-boot/blob/main/ewm-main-service-spec.json) /// [stat service](https://github.com/isthatkirill/ewm-spring-boot/blob/main/ewm-stats-service-spec.json)

## Testing

The application has been thoroughly tested using Postman for various components:

- [this](https://github.com/isthatkirill/ewm-spring-boot/blob/main/postman/ewm-main-service.json) for main service
- [this one](https://github.com/isthatkirill/ewm-spring-boot/blob/main/postman/ewm-stat-service.json) for stat service
- [and this one](https://github.com/isthatkirill/ewm-spring-boot/blob/main/postman/feature.json) for "comments" functionality

Also, more than 250 unit-tests were written with 86% code coverage

## Instructions for start the program

1. Clone this repository
   `git clone https://github.com/isthatkirill/ewm-spring-boot.git`
2. Go to the directory with the program
   `cd ewm-spring-boot`
3. Build the app
   `mvn clean install`
4. Run docker containers using docker-compose
   `docker-compose up`
5. Good job! The application is running. Detailed information about the launch is available in the logs in the console.

## Technologies and libraries used

- Java
- Spring Boot
- JUnit
- Mockito
- MockServer
- Lombok
- Mapstruct
- H2
- PostgreSQL
- Hibernate
- Docker
- Postman
