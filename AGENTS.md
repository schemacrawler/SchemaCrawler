# AGENTS.md — SchemaCrawler

SchemaCrawler is a multi-module Maven project providing the command-line tool, text and diagram output formats, linting, scripting, and bundled database connectors. It builds on [SchemaCrawler-Core](https://github.com/schemacrawler/SchemaCrawler-Core), which must be built first or resolved from Maven Central.

## Build and Test

```bash
# Standard build and unit tests
mvn clean verify

# With distribution artifacts (shaded JARs, launch scripts, examples)
mvn clean verify -Ddistrib

# With architectural verification tests
mvn clean verify -Dverify

# With Testcontainers database integration tests (requires Docker)
mvn clean verify -Dheavydb

# Run a single test class or method
mvn test -Dtest=ClassName
mvn test -Dtest=ClassName#methodName
```

Diagram-related tests require **Graphviz** (`dot` on `PATH`). Diagram generation is skipped silently when Graphviz is absent; override with system property `SC_GRAPHVIZ_PROC_DISABLE=true` to skip explicitly.

## Module Layout

| Module | Purpose |
|--------|---------|
| `schemacrawler-text` | Text, Markdown, and HTML output formatting |
| `schemacrawler-diagram` | ER diagram generation via Graphviz `dot` |
| `schemacrawler-operations` | Data operations (count, quickdump, etc.) |
| `schemacrawler-scripting` | JSR-223 scripting engine integration (Velocity, Thymeleaf, FreeMarker, Mustache, Mermaid, DBML, PlantUML templates) |
| `schemacrawler-commandline` | CLI entry point (picocli), command discovery, interactive shell (jline3) |
| `schemacrawler-lint` | Schema lint rules engine with 23 built-in linters |
| `schemacrawler-offline` | Offline/snapshot mode for serialized catalogs |
| `schemacrawler-docs` | Documentation generation |
| `schemacrawler-db2` / `-hsqldb` / `-mysql` / `-oracle` / `-postgresql` / `-sqlite` / `-sqlserver` | Bundled JDBC driver plugins |
| `schemacrawler-dbtest` | Shared test database utilities (HSQLDB-backed) |
| `schemacrawler-library-bom` | Bill of Materials for downstream consumers |
| `schemacrawler-distrib` | Assembles the ZIP distribution — activated by `-Ddistrib` |
| `schemacrawler-verify` | ArchUnit architectural verification + Docker image tests — activated by `-Dverify` |

## Extension Points (ServiceLoader SPI)

All major extension points use Java's `ServiceLoader`. Register new implementations by adding the fully qualified class name to the appropriate file under `src/main/resources/META-INF/services/`.

| Interface | Service File | Registers |
|-----------|-------------|-----------|
| `schemacrawler.tools.executable.SchemaCrawlerCommandProvider` | `META-INF/services/schemacrawler.tools.executable.SchemaCrawlerCommandProvider` | CLI commands |
| `schemacrawler.tools.lint.LinterProvider` | `META-INF/services/schemacrawler.tools.lint.LinterProvider` | Lint rules |
| `schemacrawler.tools.databaseconnector.DatabaseConnector` | `META-INF/services/schemacrawler.tools.databaseconnector.DatabaseConnector` | Database plugins |

## Command-Line Framework

The CLI uses **picocli** (v4.7.7) with **jline3** for the interactive shell.

- Entry point: `schemacrawler.Main`
- Command discovery: `CommandRegistry` loads all registered `SchemaCrawlerCommandProvider` instances via ServiceLoader at startup
- Execution flow: `Main` → `LogCommand` → connection-test / interactive-shell detection → `SchemaCrawlerCommandLine`
- Configuration files: Typesafe Config reads `schemacrawler.config.properties`

## Database Plugin Pattern

Each bundled database connector (e.g., `schemacrawler-postgresql`) follows this structure:

- **`{Db}DatabaseConnector extends DatabaseConnector`** — defines the JDBC URL template, default port, CLI option declarations, and optional metadata retrieval overrides.
- **Service file** — one-line registration in `META-INF/services/`.
- **Optional `{db}.information_schema/` resource folder** — `.sql` files that override how SchemaCrawler queries the database for views, routines, triggers, sequences, etc.

Use `withSchemaRetrievalOptionsBuilder()` on `DatabaseConnectorOptionsBuilder` to suppress or replace specific JDBC metadata retrieval strategies (e.g., disabling foreign key loading for databases with incomplete JDBC support).

## Lint Rule Pattern

Each lint rule consists of a pair:

- **`BaseLinterProvider` subclass** — registered via ServiceLoader, provides rule metadata and instantiates the linter.
- **`BaseLinter` subclass** — performs schema analysis and emits `LintViolation` instances.

Linter configuration is read from an optional XML file passed via `--lint-options-file`.

## ArchUnit Verification (`schemacrawler-verify`)

Run with `-Dverify`. `ArchitectureTest` enforces the same structural rules as `schemacrawler-core-verify`:

- `lookup*()` methods must return `Optional`.
- No `System.out` / `System.err` in production code.
- No generic exception throws (`RuntimeException`, `Exception`, `Throwable`).
- `@ModelImplementation` and `@Retriever` classes must be package-private.
- No package cycles across `schemacrawler.(**).*` slices.
- `Class.forName` restricted to `BasePluginCommandRegistry`.
- No `setAccessible()` calls.

## Coding Guidelines

- Prefer **immutability**: use `final` on fields, parameters, and local variables.
- Use `Optional`, streams, and functional programming idioms.
- Ensure **thread safety**: avoid mutable shared state.
- Write meaningful **Javadoc** for all public API.
- Tests use **JUnit 5** with **Hamcrest** matchers; mock with **Mockito**.
- All dependency versions are managed in `schemacrawler-parent/pom.xml`; do not declare versions in sub-module POMs.
