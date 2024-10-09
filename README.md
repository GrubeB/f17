# 

## Table of Contents

* [General Info](#general-information)
* [Technologies Used](#technologies-used)
* [Documentation Used](#documentation)

## General Information

## Technologies Used

- Spring Boot 3.1
- PostgreSQL 13

## Documentation

## Run
### Prod

### Dev
1. Start dev env:

        docker-compose -p f17 -f .\.docker\docker-compose.yaml up -d

2. Build:
    - Without tests:

             .\gradlew build -x test
    - With test:

             .\gradlew build

3. Run applications:

         .\gradlew :service:game:bootRun