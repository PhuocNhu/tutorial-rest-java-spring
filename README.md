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
