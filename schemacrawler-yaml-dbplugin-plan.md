# Plan: YAML-Driven Database Plugin API (`schemacrawler-dbplugins`)

## 1. Motivation and Goals

SchemaCrawler's database plugin system requires writing Java — a `DatabaseConnector` subclass, a `DatabaseConnectionSourceBuilder` configuration, a `PluginCommand` for picocli help, and a `META-INF/services` registration entry. This is correct and rich, but it is too much friction for "thin" connectors that only need a JDBC URL template, a few driver properties, and basic schema/catalog filtering.

The goals of this plan are:

1. Define a **YAML schema** that covers the most common plugin configuration surface area.
2. Provide a **`YamlDatabaseConnector` base class** that constructs a fully functional `DatabaseConnector` from a parsed YAML definition.
3. Ship a new module `schemacrawler-dbplugins` with **bundled YAML-configured plugins** for Snowflake, Microsoft Access (UCanAccess), ClickHouse, Trino, and Apache Cassandra.
4. Enable **user-extensible classpath and filesystem plugins** so that dropping a YAML file somewhere is sufficient to register a new server — no Java code required.

---

## 2. Current Architecture Summary

Every database plugin today follows this pattern (abbreviated from `PostgreSQLDatabaseConnector`):

```
DatabaseConnector
  └─ constructor calls super(DatabaseConnectorOptions)

DatabaseConnectorOptions is built via DatabaseConnectorOptionsBuilder:
  - DatabaseServerType        → server identifier + display name
  - DatabaseConnectionSourceBuilder → JDBC URL template (${host}/${port}/${database}),
                                      default port, default urlx properties
  - PluginCommand             → picocli option declarations for --server/--host/--port/--database
  - urlStartsWith / predicate → URL matching for auto-detection
  - informationSchemaViews    → classpath SQL resource folder
  - schemaRetrievalOptions    → per-strategy MetadataRetrievalStrategy overrides
  - limitOptionsBuilder       → includeSchemas / includeCatalogs (inclusion/exclusion rules)
```

Registration: one entry per plugin in
`META-INF/services/schemacrawler.tools.databaseconnector.DatabaseConnector`.

---

## 3. New Module: `schemacrawler-dbplugins`

### 3.1 Module coordinates and placement

| Item | Value |
|---|---|
| Artifact ID | `schemacrawler-dbplugins` |
| Group ID | `us.fatehi` (consistent with the project) |
| Location | `schemacrawler-dbplugins/` in the root of the SchemaCrawler project |
| Added to root `pom.xml` | Yes — under the "Database plugins" section of `<modules>` |

### 3.2 Dependencies

```xml
<!-- Runtime and compile -->
<dependency>us.fatehi:schemacrawler</dependency>          <!-- core API -->

<!-- Jackson 3 (tools.jackson) — already used in schemacrawler-scripting -->
<dependency>tools.jackson.core:jackson-databind</dependency>
<dependency>tools.jackson.dataformat:jackson-dataformat-yaml</dependency>

<!-- Test -->
<dependency>us.fatehi:schemacrawler-testdb</dependency>
<dependency>us.fatehi:schemacrawler-test-utility</dependency>
<dependency>us.fatehi:schemacrawler-commandline</dependency>
```

Jackson is already managed in the project (`tools.jackson` group ID, used by `schemacrawler-scripting`). No new version decisions are needed.

---

## 4. YAML Plugin Definition Schema

A YAML file contains one `plugin` document. Multiple YAML files may coexist in a bundle or user directory.

```yaml
# schemacrawler-plugin.yaml schema (v1)
plugin:

  # Required — must be a lowercase identifier, must be unique across all plugins
  server: snowflake

  # Required — human-readable display name
  name: Snowflake

  # Required — JDBC URL template; supports ${host}, ${port}, ${database} placeholders
  url-template: "jdbc:snowflake://${host}.snowflakecomputing.com:${port}/${database}"

  # Required — prefix used to auto-detect this plugin from a raw --url argument
  url-prefix: "jdbc:snowflake:"

  # Optional — default JDBC port
  default-port: 443

  # Optional — default URL extra properties (passed as JDBC URL parameters or
  # Properties entries). Keys and values are strings.
  default-url-properties:
    warehouse: ""
    role: ""
    authenticator: "snowflake"

  # Optional — picocli help strings for each CLI option
  # Each entry is a list of description lines (same multi-line convention as picocli).
  help:
    server:
      - "--server=snowflake"
      - "Loads SchemaCrawler plug-in for Snowflake"
    host:
      - "Snowflake account identifier (e.g. myorg-myaccount)"
    port:
      - "Port number"
      - "Optional, defaults to 443"
    database:
      - "Snowflake database name"

  # Optional — schema retrieval strategy overrides.
  # Keys are SchemaInfoMetadataRetrievalStrategy field names (camelCase or snake_case).
  # Values are MetadataRetrievalStrategy enum constants.
  schema-retrieval:
    tableColumnsRetrievalStrategy: metadata_over_schemas
    primaryKeysRetrievalStrategy: metadata_over_schemas

  # Optional — identifier quoting character (e.g. `"` or `` ` ``)
  identifier-quote-string: "\""

  # Optional — schema filtering (applied to limitOptionsBuilder).
  # Supports Java regex. Exactly one of include/exclude-schemas may be set.
  limit:
    exclude-schemas: "INFORMATION_SCHEMA"
    # include-schemas: ".*"       # alternative
    # exclude-catalogs: "..."     # optional catalog filter
    # include-catalogs: "..."     # alternative
```

### 4.1 Design decisions in the schema

- **`url-template`** exactly mirrors what `DatabaseConnectionSourceBuilder.builder(...)` takes today (`${host}`, `${port}`, `${database}`).
- **`url-prefix`** maps to `withUrlStartsWith(...)`. If neither `url-prefix` nor a custom predicate is needed, it can be omitted (no auto-detection by URL).
- **`default-url-properties`** map to `withDefaultUrlx(key, value)` calls.
- **`schema-retrieval`** exposes only the `SchemaInfoMetadataRetrievalStrategy` / `MetadataRetrievalStrategy` override surface — the same builder API that all Java connectors use. The key names mirror the Java field names exactly to avoid a separate mapping table.
- **`identifier-quote-string`** covers the SQLite pattern of `withIdentifierQuoteString("\"")`.
- **`limit`** covers the very common include/exclude pattern for schemas and catalogs.
- **Information-schema SQL overrides are out of scope for v1.** Connectors needing custom SQL queries still require the full Java approach. A future v2 could allow referencing SQL files from a classpath folder or filesystem path.
- **`schemaRetrievalOptionsBuilder` lambdas needing a live Connection** (e.g. PostgreSQL enum helpers) are not expressible in YAML. This is a deliberate constraint — advanced connectors continue to use Java.

---

## 5. Java Implementation Design

### 5.1 `YamlDatabasePluginDefinition` (POJO)

A Jackson-annotated POJO matching the YAML schema. Fields map 1:1 to the YAML structure above. Uses `@JsonProperty` annotations for camelCase↔kebab-case mapping.

### 5.2 `YamlDatabaseConnector extends DatabaseConnector`

A final `DatabaseConnector` subclass that:

1. Accepts a `YamlDatabasePluginDefinition` in its constructor.
2. Calls the existing builder chain (`DatabaseConnectorOptionsBuilder`, `DatabaseConnectionSourceBuilder`, `PluginCommand`) from that definition.
3. Converts `schema-retrieval` entries by looking up `SchemaInfoMetadataRetrievalStrategy` fields and `MetadataRetrievalStrategy` enum values by name via reflection (or a fixed `Map<String, SchemaInfoMetadataRetrievalStrategy>`).
4. Converts `limit` entries into `RegularExpressionInclusionRule` / `RegularExpressionExclusionRule` as needed.

This class is **not** in `META-INF/services` directly; it is instantiated by the loader (§5.3).

### 5.3 `YamlDatabasePluginLoader` (static factory)

A utility class responsible for loading YAML plugin definitions from:

| Source | Discovery mechanism |
|---|---|
| Classpath bundle | All resources matching `META-INF/schemacrawler-plugins/*.yaml` enumerated via `ClassLoader.getResources(...)` |
| Filesystem directory | Path read from system property `schemacrawler.plugins.dir` or environment variable `SC_PLUGINS_DIR`; defaults to `~/.schemacrawler/plugins/` |

`YamlDatabasePluginLoader.loadAll()` returns `List<YamlDatabaseConnector>`.

### 5.4 `YamlDatabaseConnectorBridge implements DatabaseConnector`

A **single registered `DatabaseConnector`** in `META-INF/services` that acts as a routing bridge to all YAML-loaded connectors.

This solves the core problem: Java's `ServiceLoader` requires a known class per registration, but we want dynamic plugins from YAML. The bridge:

1. On construction, calls `YamlDatabasePluginLoader.loadAll()` to discover all YAML connectors.
2. Stores the resulting `Map<String /* serverIdentifier */, YamlDatabaseConnector>`.
3. Overrides the required `DatabaseConnector` lifecycle methods to **delegate to the correct sub-connector** after routing by server identifier or URL prefix.
4. Reports itself to `DatabaseConnectorRegistry` as a multi-valued plugin.

**Caveat**: This approach requires `DatabaseConnectorRegistry` in SchemaCrawler-Core to be able to "explode" a bridge connector into its constituent parts so that each YAML-defined server shows up individually in `--help servers`, `--server=<tab>` completion, and individual routing. This is a **small focused change to Core** (see §6).

---

## 6. Required Change to SchemaCrawler-Core

Add a new marker interface (or a default method) to `DatabaseConnector`:

```java
// New optional interface in schemacrawler-core
public interface DatabaseConnectorBundle {
    Collection<DatabaseConnector> getDatabaseConnectors();
}
```

`DatabaseConnectorRegistry.getDatabaseConnectorRegistry()` is enhanced to check whether each ServiceLoader-discovered `DatabaseConnector` also implements `DatabaseConnectorBundle`. If it does, each constituent connector is unboxed and registered individually. Otherwise behaviour is unchanged.

This is a **backward-compatible additive change** — existing connectors are unaffected. It would be part of a minor SchemaCrawler-Core PR.

---

## 7. Bundled Plugins

Five YAML files are included in `schemacrawler-dbplugins/src/main/resources/META-INF/schemacrawler-plugins/`:

### 7.1 Snowflake

```yaml
plugin:
  server: snowflake
  name: Snowflake
  url-template: "jdbc:snowflake://${host}:${port}/"
  url-prefix: "jdbc:snowflake:"
  default-port: 443
  default-url-properties:
    db: "${database}"
    CLIENT_SESSION_KEEP_ALIVE: "true"
  help:
    server: ["--server=snowflake", "Loads SchemaCrawler plug-in for Snowflake"]
    host: ["Snowflake account identifier (e.g. myorg-myaccount.snowflakecomputing.com)"]
    port: ["Port number", "Optional, defaults to 443"]
    database: ["Snowflake database name"]
  limit:
    exclude-schemas: "INFORMATION_SCHEMA"
```

*Note: Snowflake JDBC URLs encode the database as a URL property (`db=`) rather than a URL path segment. The template reflects this.*

### 7.2 Microsoft Access (UCanAccess)

```yaml
plugin:
  server: access
  name: Microsoft Access
  url-template: "jdbc:ucanaccess://${database}"
  url-prefix: "jdbc:ucanaccess:"
  default-url-properties:
    memory: "false"
    showSchema: "true"
  help:
    server: ["--server=access", "Loads SchemaCrawler plug-in for Microsoft Access (UCanAccess)"]
    host: ["Should be omitted"]
    port: ["Should be omitted"]
    database: ["Path to the .accdb or .mdb file"]
  schema-retrieval:
    tableColumnsRetrievalStrategy: metadata_over_schemas
```

### 7.3 ClickHouse

```yaml
plugin:
  server: clickhouse
  name: ClickHouse
  url-template: "jdbc:clickhouse://${host}:${port}/${database}"
  url-prefix: "jdbc:clickhouse:"
  default-port: 8123
  default-url-properties:
    socket_timeout: "300000"
  help:
    server: ["--server=clickhouse", "Loads SchemaCrawler plug-in for ClickHouse"]
    host: ["Host name", "Optional, defaults to localhost"]
    port: ["Port number", "Optional, defaults to 8123"]
    database: ["Database name"]
  limit:
    exclude-schemas: "INFORMATION_SCHEMA|system"
```

### 7.4 Trino

```yaml
plugin:
  server: trino
  name: Trino
  url-template: "jdbc:trino://${host}:${port}/${database}"
  url-prefix: "jdbc:trino:"
  default-port: 8080
  help:
    server: ["--server=trino", "Loads SchemaCrawler plug-in for Trino"]
    host: ["Trino coordinator host name", "Optional, defaults to localhost"]
    port: ["Port number", "Optional, defaults to 8080"]
    database: ["Catalog name"]
  limit:
    exclude-schemas: "INFORMATION_SCHEMA"
```

### 7.5 Apache Cassandra (DataStax JDBC)

```yaml
plugin:
  server: cassandra
  name: Apache Cassandra
  url-template: "jdbc:cassandra://${host}:${port}/${database}"
  url-prefix: "jdbc:cassandra:"
  default-port: 9042
  default-url-properties:
    consistency: "LOCAL_ONE"
  help:
    server: ["--server=cassandra", "Loads SchemaCrawler plug-in for Apache Cassandra"]
    host: ["Host name", "Optional, defaults to localhost"]
    port: ["Port number", "Optional, defaults to 9042"]
    database: ["Keyspace name"]
  schema-retrieval:
    tableColumnsRetrievalStrategy: metadata_over_schemas
    primaryKeysRetrievalStrategy: metadata_over_schemas
    foreignKeysRetrievalStrategy: metadata_over_schemas
```

---

## 8. User-Extensible Filesystem Plugins

Once the module is on the classpath, a user can add any new database by:

1. Creating `~/.schemacrawler/plugins/myplugin.yaml` (or any `.yaml` file in the configured directory).
2. Adding the JDBC driver JAR to the classpath.
3. Running SchemaCrawler with `--server=myplugin ...`.

No Java code, no recompilation, no JAR packaging required.

The discovery path can be overridden via:
- System property: `-Dschemacrawler.plugins.dir=/path/to/dir`
- Environment variable: `SC_PLUGINS_DIR=/path/to/dir`

---

## 9. Module Structure

```
schemacrawler-dbplugins/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/schemacrawler/plugins/yaml/
    │   │   ├── YamlDatabasePluginDefinition.java     # Jackson POJO
    │   │   ├── YamlDatabasePluginDefinition.java     # nested POJOs: HelpDefinition, LimitDefinition, etc.
    │   │   ├── YamlDatabaseConnector.java            # extends DatabaseConnector
    │   │   ├── YamlDatabasePluginLoader.java         # classpath + filesystem scanner
    │   │   └── YamlDatabaseConnectorBridge.java      # implements DatabaseConnector + DatabaseConnectorBundle
    │   └── resources/
    │       ├── META-INF/services/
    │       │   └── schemacrawler.tools.databaseconnector.DatabaseConnector
    │       │       (contains: schemacrawler.plugins.yaml.YamlDatabaseConnectorBridge)
    │       └── META-INF/schemacrawler-plugins/
    │           ├── snowflake.yaml
    │           ├── access.yaml
    │           ├── clickhouse.yaml
    │           ├── trino.yaml
    │           └── cassandra.yaml
    └── test/
        └── java/schemacrawler/plugins/yaml/
            ├── YamlDatabasePluginLoaderTest.java
            ├── YamlDatabaseConnectorTest.java
            └── BundledPluginsTest.java
```

---

## 10. ArchUnit Considerations

The `schemacrawler-verify` module enforces several architectural rules. The new module must comply:

| Rule | How `schemacrawler-dbplugins` satisfies it |
|---|---|
| No `System.out` / `System.err` in production code | Use `java.util.logging` |
| No generic exception throws | Use specific checked or `SchemaCrawlerException` |
| `@ModelImplementation` / `@Retriever` classes must be package-private | N/A (no model/retriever classes) |
| `lookup*()` methods return `Optional` | N/A (no lookup methods) |
| No `setAccessible()` | Reflection for `SchemaInfoMetadataRetrievalStrategy` lookup will use field access, not `setAccessible` — or use a static lookup map instead |

The reflection concern in §5.2 (looking up `SchemaInfoMetadataRetrievalStrategy` values) should use a **static `EnumMap`** rather than reflection to stay clean and avoid the `setAccessible()` restriction.

---

## 11. Implementation Sequence

1. **Core change (small PR to SchemaCrawler-Core)**: Add `DatabaseConnectorBundle` interface; update `DatabaseConnectorRegistry` to unwrap bundles. This unblocks step 2.

2. **New module scaffold**: Create `schemacrawler-dbplugins/pom.xml`, register in root `pom.xml`.

3. **YAML POJO layer**: `YamlDatabasePluginDefinition` + nested POJOs, Jackson annotations, unit tests with sample YAML.

4. **`YamlDatabaseConnector`**: The translation layer from POJO to `DatabaseConnectorOptions`. Covers URL template, default port, urlx properties, help command options, schema-retrieval overrides (static enum map), identifier quote string, and limit options.

5. **`YamlDatabasePluginLoader`**: Classpath resource scanning (`META-INF/schemacrawler-plugins/*.yaml`) and filesystem directory scanning. Unit tests covering both paths.

6. **`YamlDatabaseConnectorBridge`**: ServiceLoader entry, delegates to loader. Integration test confirming all 5 bundled plugins appear in `DatabaseConnectorRegistry`.

7. **Bundled YAML files**: The five YAML definitions above, with test assertions that the connectors they produce can construct valid `DatabaseConnectorOptions` and generate correct `PluginCommand` help output.

8. **Documentation**: Update `AGENTS.md` module table; add a user guide section explaining how to author a custom YAML plugin.

---

## 12. Out of Scope for v1

- Information-schema SQL overrides from YAML (complex, deferred to v2).
- Connection initializers (Java-only feature — e.g. Oracle's `SET` statements).
- Enum data type helpers (Java-only feature — e.g. PostgreSQL `pg_enum`).
- Non-standard URL patterns where database is not a path segment (Snowflake's `db=` property is handled via `default-url-properties` workaround, see §7.1).
- Automatic JDBC driver download or dependency resolution.
- A YAML schema registry or JSON Schema document (useful but not blocking).

---

## 13. Open Questions for Review

1. **`DatabaseConnectorBundle` interface in Core vs. an extension point in this project**: Is the Core team willing to take the small `DatabaseConnectorBundle` change, or should the new module use a different mechanism (e.g. a startup hook, a `@Configuration` pattern from the existing Typesafe Config infrastructure) to register dynamic connectors?

2. **Module name**: `schemacrawler-dbplugins` (plural, bundled plugins concept) vs. `schemacrawler-yaml-plugin` (emphasises the YAML mechanism) vs. `schemacrawler-plugin-registry` (emphasises the extensibility angle). Which name fits the project's naming conventions best?

3. **YAML file discovery path on the classpath**: `META-INF/schemacrawler-plugins/*.yaml` is proposed. Should this mirror the pattern used for Typesafe Config (`schemacrawler.config.properties`) more closely?

4. **Snowflake URL shape**: Snowflake JDBC can take the database either as a URL path segment or as a `db` URL property depending on driver version. The plan currently uses `db` as a default URL property with the database placeholder. This needs verification against the current Snowflake JDBC driver documentation before implementation.

5. **`DatabaseConnectorBridge` server type**: The bridge itself would be registered in `AvailableServers` unless suppressed. Should the bridge have a sentinel server type (e.g. `yaml-bundle`) that the registry explicitly hides from help output and completion candidates?
