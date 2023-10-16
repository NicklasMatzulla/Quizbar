# QuizBar
QuizBar is a browser game where teams of two to four players can compete against each other to prove their knowledge in a total of 5 categories.

## Usage
1. Enter your database credentials in the [application.properties file](./src/main/resources/application.properties)
2. Build and start the application on **Java 17+**
* Optional configure the questions in the [questions.json file](./questions.json)

## Libraries and sources
* [Spring Boot](https://spring.io)
* [Vaadin](https://vaadin.com)
* [Line Awesome](https://github.com/paritie/line-awesome)
* [Gson](https://github.com/google/gson)
* [Lombok](https://projectlombok.org)
* [MariaDB Java Client](https://mariadb.org)
* [Spring Boot Test](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-testing)
* [Testcontainers](https://www.testcontainers.org)
* [DALL-E 2](https://openai.com/dall-e-2) (for generating the [smoothie image](./src/main/resources/META-INF/resources/images/smoothie.png))