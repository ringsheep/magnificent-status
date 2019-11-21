# Scheduled health state checker
A sample daemon app to check status of randomly falling external api

## Quick start
Docker way:
```
TBD, not done yet :)
```
Simple jar launch:
```
./gradlew clean build && java -jar build/libs/magnificent-status-0.0.1-SNAPSHOT.jar
```
to launch an external api which can change it's state:
```
pip install twisted
python server.py
```
http://localhost:12345/ is being watched by default every 3 seconds

## API
* Get current state
```
curl -X GET http://localhost:8080/state'
```
* All state changes are viewed in app logs

## Technologies & frameworks: 
* Java 11, Spring Boot, Junit, Mockito

## Requirements
* JDK 11
* Python 2/3 to launch external api

## TODO
* Dockerize app
* Swagger documentation