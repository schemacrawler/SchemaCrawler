# Junie Guidelines for SchemaCrawler

This document provides guidelines for the Junie AI assistant when working with the SchemaCrawler project.

## General Coding Guidelines
- Prefer **immutability** and use the `final` keyword for fields, parameters, and local variables when appropriate.
- Follow the **Effective Java** principles, particularly "Item 17 - Minimize Mutability" by using package-private constructors.
- Follow **Java best practices**, including usage of `Optional`, `Streams`, and functional programming where applicable.
- Ensure **thread safety** by avoiding mutable shared state and using synchronized wrappers or concurrency utilities when necessary.
- Use **meaningful names** for classes, methods, and variables to improve code readability.
- Follow **SOLID principles** to enhance maintainability and scalability.
- Write meaningful **javadocs** for functions and classes.
- Use **builder pattern** for complex object construction.
- Implement clear **class hierarchies** with abstract base classes and specific implementations.
- Use **interfaces** extensively with well-defined implementations.
- Prefer **composition over inheritance** where appropriate.

## Code Formatting
- Use **UTF-8** character encoding for all files.
- Use **CRLF** line endings (LF for shell scripts).
- Include a **final newline** at the end of all files.
- Limit lines to a **maximum of 80 characters**.
- Trim **trailing whitespace** (except in Markdown files).
- Use **space indentation** with 2 spaces (4 spaces for Python files).

## Project Structure
- Organize packages based on functionality (e.g., `service`, `repository`, `controller`).
- Use **Apache Maven** for build management and dependency resolution.
- Maintain a **consistent project structure**, following standard conventions.
- The project follows a **modular architecture** with clear separation of concerns:
  - Core modules (utility, api, tools)
  - Additional functionality modules (commandline, scripting, lint)
  - Database-specific plugins (db2, hsqldb, mysql, etc.)
  - Documentation and distribution modules

## Dependencies and Versions
- Define **explicit versions** for dependencies to prevent compatibility issues.
- Prefer **dependency management** using `dependencyManagement` in `pom.xml` for centralized version control.
- Use **dependency exclusions** where necessary to avoid unwanted transitive dependencies.

## Testing and Quality
- Write **unit tests** for business logic using JUnit 5 with Hamcrest matchers.
- Use **Mockito** for mocking dependencies in tests.
- Maintain **high test coverage** to ensure reliability.
- Use **descriptive test method names** with @DisplayName annotations to clearly indicate test purpose.
- Test **edge cases** thoroughly (empty results, invalid inputs, malformed data).
- Use **custom test annotations** (@WithTestDatabase, @ResolveTestContext) for test setup.
- Implement **test utilities** for database setup and content verification.
- Use **test resources** for expected output comparison.
- Follow **AAA pattern** (Arrange, Act, Assert) in test methods.
