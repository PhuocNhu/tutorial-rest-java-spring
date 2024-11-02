# Simple REST API and WebSocket Implementation

# tutorial-rest-java-spring

[![Build Status](https://travis-ci.org/codecentric/springboot-sample-app.svg?branch=master)](https://travis-ci.org/codecentric/springboot-sample-app)
[![Coverage Status](https://coveralls.io/repos/github/codecentric/springboot-sample-app/badge.svg?branch=master)](https://coveralls.io/github/codecentric/springboot-sample-app?branch=master)
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)


## Requirements

For building and running the application you need:

- Java Development Kit (JDK): [Version 22](https://www.oracle.com/java/technologies/downloads/)
- Maven: [Version 3](https://maven.apache.org)

## Running the application locally
### Things need to complete before run the application:
1. Download [Smtp4dev](https://github.com/rnwood/smtp4dev/releases/tag/3.6.1)
2. Create PostgresDB and then run the sql script in `src/resources` folder
3. Adding informations in 2 properties files with this format (replace ... with your custom configurations):
```shell
spring.application.name=tutorial-rest-java-spring


spring.datasource.url=...
spring.datasource.username=...
spring.datasource.password=...

spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

#Encryption
encryption.salt.rounds=10

#Token
jwt.algorithm.key=...
jwt.issuer=...
jwt.expiryInSeconds=...

#Dev Email
email.from=samplemail@gmail.com

#FrontEnd URL
app.frontend.url=http.//ecommerce.com


#SMTP
spring.mail.host=localhost
spring.mail.port=25
#spring.mail.username=
#spring.mail.password=
#spring.properties.mail.smtp.auth=true
#spring.properties.mail.smtp.starttls.enable=true

```
```shell
spring.jpa.defer-datasource-initialization=true
spring.datasource.url=jdbc:h2:mem:test
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect

spring.mail.port=3025
spring.mail.username=springboot
spring.mail.password=secret
```

### There are several ways to run a Spring Boot application on your local machine. Below is 2 suggestions:
1. Using IntelliJ IDEA
2. Using Command Line

```shell
mvn spring-boot:run
```
The application will start on the default port (e.g., `http://localhost:8080`).

## Testing the WebSocket
To test the WebSocket functionality, use the `index.html` file located in the `tools/websocket-tool` folder:
1. Start the Spring Boot application.
2. Open `tools/websocket-tool/index.html` in a web browser.
3. Use the tool to send and receive messages to verify the WebSocket connection.

## Copyright

Released under the Apache License 2.0. See the [LICENSE](https://github.com/codecentric/springboot-sample-app/blob/master/LICENSE) file.
