# Audition API Overview

Audition API is a RESTFul Spring Boot application, having great measures to match the business
requirements, along with Spring features through the following coding standards, validations,
testing and reports.

## Application Configuration

Along with few default values in application.yml, we also have the API details which
we use for retrieving the posts, comments and more details.

Below are the mandatory values to set in application.yml file or else, it would through an error
when the application is about to start.

<pre>
audition:
  api:
    base-url: https://jsonplaceholder.typicode.com
    posts:
      path: /posts
    comments:
      path: /comments
</pre>

Ex: If I don't provide this block, it throws below error.
<pre>
  posts:
    path: /posts
</pre>

<pre>
***************************
APPLICATION FAILED TO START
***************************

Description:

Binding to target org.springframework.boot.context.properties.bind.BindException: Failed to bind properties under '
audition.api' to com.audition.configuration.AuditionApiProperties failed:

    Property: audition.api.posts
    Value: "null"
    Reason: Posts audition.api.posts configuration is required (missing 'posts' section or 'posts.path' property)

Action:

Update your application's configuration
</pre>

## API/Endpoint Details

### Controller

AuditionController class contains all the API Endpoints which are exposed in this application.
We mainly have four API endpoints, those are listed more in detail via Swagger Documentation.
[Go to Swagger section](#swagger-documentation)

All the validations like NotNull, Positive Integer, GET POST Methods, String to Long conversion
etc are placed in the same class. We have `PostIdParam` commonly used for three of our APIs (reusability)

`RestTemplate` in the `WebSecurityConfig` class has the Logging enabled particularly for API requests
and responses. Set to debug mode to view the Response Body logs.

Exceptions and loggers of the app are handled by ExceptionAdvice, SystemException generates
necessary ErrorResponse so that consumer of the API can easily know what's the exception is
either client error(validation, constraints, required values etc) or server error(API connection error,
application level errors).

### Swagger Documentation

All the Swagger Documentation for the APIs is mentioned through APIResponses, Schema, API Params,
Query Paths in AuditionController and its dependent classes.

You can access, test the APIs using the Swagger UI, please access it through below link after starting
the application.

http://localhost:8080/swagger-ui/index.html

### Testing

Project has 80%+ coverage for all the code. To run the test cases suite or test report, please
find the below commands.

`./gradlew test`
`./gradlew jacocoTestReport`

### Check Style and PMD Reports

Run `./gradlew pmdMain` to generate PMD Report for src folder files
Run `./gradlew pmdTest` to generate PMD Report for test folder files

Make sure it is clean.

### Overall Build

Run `./gradlew clean build` to clean the build folder and perform all the test, coverage,
PMD, checkstyle and generate a jar file ready to deploy.

<pre>
8:42:46 am: Executing 'build'…

> Task :prepareGitHooks NO-SOURCE
> Task :generateEffectiveLombokConfig UP-TO-DATE
> Task :compileJava UP-TO-DATE
> Task :processResources UP-TO-DATE
> Task :classes UP-TO-DATE
> Task :resolveMainClassName UP-TO-DATE
> Task :bootJar UP-TO-DATE
> Task :jar UP-TO-DATE
> Task :assemble UP-TO-DATE
> Task :checkstyleMain UP-TO-DATE
> Task :generateTestEffectiveLombokConfig UP-TO-DATE
> Task :compileTestJava
> Task :processTestResources UP-TO-DATE
> Task :testClasses
> Task :pmdMain UP-TO-DATE
> Task :checkstyleTest
> Task :pmdTest
> Task :spotbugsMain UP-TO-DATE
> Task :test UP-TO-DATE
> Task :jacocoTestReport UP-TO-DATE
> Task :spotbugsTest
> Task :check
> Task :build

Deprecated Gradle features were used in this build, making it incompatible with Gradle 8.0.

You can use '--warning-mode all' to show the individual deprecation warnings and determine if they come from your own scripts or plugins.

See https://docs.gradle.org/7.6/userguide/command_line_interface.html#sec:command_line_warnings

BUILD SUCCESSFUL in 3s
17 actionable tasks: 4 executed, 13 up-to-date
8:42:50 am: Execution finished 'build'.

</pre>

### Run the application & test

Run `./gradlew bootRun` to start the application.

### Backlog Items

- Add Docker to the application to deploy into containers
- Add Sonarqube implementation. SonarProject.properties file and find if any vulnerabilities exist
- Update Spring boot versions to eliminate any vulnerabilities through dependency injection or any other classes.
- Add Security with a JWT token and Identity Provider API