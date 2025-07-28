# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot application called "plan_make" built with Java 17 and Gradle. It's a web application that includes:
- Spring Boot 3.5.3
- Spring Data JPA for database operations
- MySQL database connectivity
- Spring Web for REST API endpoints
- Lombok for reducing boilerplate code

## Development Commands

### Build and Run
```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun

# Clean and rebuild
./gradlew clean build
```

### Testing
```bash
# Run all tests
./gradlew test

# Run tests with output
./gradlew test --info

# Run a specific test class
./gradlew test --tests "com.ikhyeon.plan_make.PlanMakeApplicationTests"
```

### Other Gradle Tasks
```bash
# Check for dependency updates
./gradlew dependencyUpdates

# Generate project reports
./gradlew build --scan

# Run without tests
./gradlew build -x test
```

## Architecture

### Package Structure
- `com.ikhyeon.plan_make` - Root package containing the main Spring Boot application class
- Standard Maven/Gradle directory structure with `src/main/java` and `src/test/java`

### Key Technologies
- **Database**: MySQL with Spring Data JPA
- **Web Framework**: Spring MVC
- **Build Tool**: Gradle with wrapper
- **Testing**: JUnit 5 platform
- **Code Generation**: Lombok annotations

### Configuration
- Main configuration in `src/main/resources/application.properties`
- Database connection and JPA settings should be configured here
- Static resources served from `src/main/resources/static/`
- Thymeleaf templates in `src/main/resources/templates/`

## Database Setup

The application is configured for MySQL. Ensure you have:
1. MySQL server running
2. Database connection properties configured in `application.properties`
3. Required database and user created

## Development Notes

- Uses Gradle wrapper (`./gradlew`) for consistent build environment
- Java 17 toolchain configured in `build.gradle`
- Lombok reduces boilerplate - use annotations like `@Data`, `@Entity`, etc.
- JPA entities should be placed in appropriate packages under the main package
- REST controllers should follow Spring MVC patterns