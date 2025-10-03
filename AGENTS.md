# Instructions for SchemaCrawler Project

## Project Structure and Build
- SchemaCrawler is set up as a multi-module **Apache Maven** project.
- Standard Apache Maven commands can be used to build and test the project.
- Integration tests use **Testcontainers**, and can be triggered with an additional `-Dheavydb` flag to the Apache Maven commands.

## General Coding Guidelines
- Prefer **immutability** and use the `final` keyword for fields, parameters, and local variables wherever possible.
- Follow **Java best practices**, including usage of `Optional`, `Streams`, and functional programming where applicable.
- Ensure **thread safety** by avoiding mutable shared state and using synchronized wrappers or concurrency utilities when necessary.
- Use **meaningful names** for classes, methods, and variables to improve code readability.
- Follow **SOLID principles** to enhance maintainability and scalability.
- Write meaningful **javadocs** for functions and classes.

## Project Structure
- Organize packages based on functionality (e.g., `service`, `repository`, `controller`).
- Use **Apache Maven** for build management and dependency resolution.
- Maintain a **consistent project structure**, following standard conventions.

## Dependencies and Versions
- Define **explicit versions** for dependencies to prevent compatibility issues.
- Prefer **dependency management** using `dependencyManagement` in `pom.xml` for centralized version control.
- Use **dependency exclusions** where necessary to avoid unwanted transitive dependencies.

## Testing and Quality
- Write **unit tests** for business logic using **JUnit 5** with **Hamcrest** matchers.
- Use **Mockito** for mocking dependencies in tests.
- Maintain **high test coverage** to ensure reliability.
