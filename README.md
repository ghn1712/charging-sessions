# Charging sessions
## Implementation
To meet the computational complexity requirement, I used two data structures in the Repository class. A HashMap using as key the uuid of the charging session and a TreeMap whose key is the startedAt timestamp of the charging session. This was necessary because if I used only a HashMap or only a TreeMap, or the stop a charging session feature would take more than log(n) or the summary feature would take more than log(n). Both data structures are synchronized so the application is thread-safe. The system architecture is inspired by [Clean Architecture]( https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

### Libs
- Dependency injection - [Guice](https://github.com/google/guice)
- Web framework - [Javalin](https://javalin.io/)
- Tests - [Spock](http://spockframework.org/spock/docs/1.3/all_in_one.html)

## Run
There are two options to run the application:
 - `./gradlew clean run` on project root folder
 - `./gradlew clean jar` and then `java -jar build/libs/charging-session-1.0-SNAPSHOT.jar`

## Build and tests
`./gradlew clean build` on project root folder
