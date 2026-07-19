# Plan: YAML-Driven Database Plugin API (`schemacrawler-dbplugins`)

## 1. Goals

SchemaCrawler's database plugin system is powerful but requires writing Java for every new
connector. The goals of this initiative are:

1. Define a **YAML schema** covering the most common plugin configuration surface — JDBC URL
   template, default properties, CLI help strings, schema/catalog filtering, retrieval strategy
   overrides, and an allowlist of known driver URL parameters.
2. Provide a clean **Java model layer** (records/classes) that holds the parsed YAML data with no
   dependency on Jackson outside the parsing layer.
3. Provide a **`YamlDatabaseConnector`** that constructs a fully functional `DatabaseConnector`
   from the model layer.
4. Provide a **`YamlDatabaseConnectorBridge`** — the single `ServiceLoader` entry — that discovers
   and expands YAML-defined plugins without being a server itself.
5. Ship five **bundled YAML-configured plugins** for Snowflake, Microsoft Access, ClickHouse,
   Trino, and Apache Cassandra.
6. Enable **user-extensible filesystem plugins** — drop a YAML file somewhere on the configured
   path and a new server appears with no code.

---

## 2. Current Architecture Summary

Every database plugin today follows this pattern:

```
DatabaseConnector
  └─ constructor builds DatabaseConnectorOptions via DatabaseConnectorOptionsBuilder:
       ├─ DatabaseServerType        → server identifier + display name
       ├─ DatabaseConnectionSourceBuilder → JDBC URL template, default port, urlx defaults
       ├─ PluginCommand             → picocli option declarations for help display
       ├─ urlStartsWith / predicate → URL matching for auto-detection
       ├─ informationSchemaViews    → classpath SQL resource folder
       ├─ schemaRetrievalOptions    → per-strategy MetadataRetrievalStrategy overrides
       └─ limitOptionsBuilder       → includeSchemas / includeCatalogs rules
```

Registration: one class per plugin in
`META-INF/services/schemacrawler.tools.databaseconnector.DatabaseConnector`.

**PluginCommand and CLI options**: Each connector declares its own `PluginCommand` with
`.addOption()` calls. For server-type plugins, these options (e.g. `--server`, `--host`, `--port`,
`--database`) appear in the `--help servers` display and in the per-server help page (e.g.,
`-h postgresql`). At runtime, the base four options (`--host`, `--port`, `--database`, `--server`)
are parsed from the CLI by `ServerHostConnectionGroupOptions`. Any driver-specific extra properties
(beyond host/port/database) are currently passed via `--urlx=key=value`. The
`PluginCommand.addOption()` API is the plugin mechanism for declaring and documenting these named
options.

**Driver URL parameter protection**: Java connectors can call
`DatabaseConnectionSourceBuilder.withAdditionalDriverProperties(Set<String>)` to declare an
allowlist of known JDBC connection property names. When set, any unrecognised `--urlx=` parameter
is rejected at startup, preventing silent misconfigurations. See `DB2DatabaseConnector` for a
full example of this pattern.

---

## 3. New Module: `schemacrawler-dbplugins`

### 3.1 Module coordinates and placement

| Item | Value |
|---|---|
| Artifact ID | `schemacrawler-dbplugins` |
| Group ID | `us.fatehi` |
| Location | `schemacrawler-dbplugins/` at project root |
| Root `pom.xml` | Added under the "Database plugins" `<modules>` section |

### 3.2 Dependencies

```xml
<!-- Compile / runtime -->
<dependency>us.fatehi:schemacrawler</dependency>           <!-- core API, no Jackson -->

<!-- Jackson 3 — already used in schemacrawler-scripting; versions managed in parent -->
<dependency>tools.jackson.core:jackson-databind</dependency>
<dependency>tools.jackson.dataformat:jackson-dataformat-yaml</dependency>

<!-- Test -->
<dependency>us.fatehi:schemacrawler-testdb</dependency>
<dependency>us.fatehi:schemacrawler-test-utility</dependency>
<dependency>us.fatehi:schemacrawler-commandline</dependency>
```

**No Jackson is introduced into `schemacrawler` (Core).** Jackson lives exclusively in
`schemacrawler-dbplugins`.

---

## 4. Required Change to SchemaCrawler-Core

### 4.1 New `DatabaseConnectorBundle` interface

Add a new interface to `schemacrawler` (Core), in the same package as `DatabaseConnector`:

```java
// New interface — pure Java, zero Jackson dependency
public interface DatabaseConnectorBundle {
    Collection<DatabaseConnector> getDatabaseConnectors();
}
```

### 4.2 Updated `DatabaseConnectorRegistry` loading

`DatabaseConnectorRegistry.loadDatabaseConnectorRegistry()` is enhanced with one extra check after
loading each `ServiceLoader` entry:

```
if (databaseConnector instanceof DatabaseConnectorBundle bundle) {
    for (DatabaseConnector child : bundle.getDatabaseConnectors()) {
        register child by its getDatabaseServerType().getDatabaseSystemIdentifier()
    }
    // do NOT register the bridge itself as a server entry
} else {
    register databaseConnector normally
}
```

### 4.3 Collision policy

Two distinct collision scenarios require separate handling:

**YAML vs. YAML collision** (two YAML files declaring the same `server:` identifier):
- `YamlDatabasePluginLoader` throws a `SchemaCrawlerException` when it detects a duplicate
  `server:` key during loading.
- This is a packaging or authoring error. The exception provides a clear message identifying
  the conflicting server name and the two source files.

**Java plugin vs. YAML plugin** (same `server:` identifier):
- Java-coded `DatabaseConnector` subclasses are registered first (loaded directly via
  `ServiceLoader` as before).
- When the `YamlDatabaseConnectorBridge` bundle is expanded, any connector whose server
  identifier is **already registered is silently skipped** — the Java plugin is kept and the
  YAML definition discarded. A `DEBUG` log message records the skip.
- **Java plugins always win over YAML plugins.** This preserves backward compatibility and
  allows Java connectors to shadow any YAML file with the same name.

This is a **backward-compatible additive change** — all existing connectors are unaffected. The
bridge never appears in `getRegisteredPlugins()`, `getHelpCommands()`, or
`getDatabaseServerTypes()`.

---

## 5. YAML Plugin Definition Schema

A single YAML file describes one plugin. Files are discovered from:

- **Classpath root directory** `schemacrawler-dbplugins/` — consistent with the module name, not
  inside `META-INF/`.
- **Filesystem directory** configured via system property `schemacrawler.plugins.dir` or
  environment variable `SC_PLUGINS_DIR` (defaults to `~/.schemacrawler/plugins/`).

### 5.1 Full annotated schema

```yaml
# schemacrawler-dbplugins/snowflake.yaml

plugin:

  # Required — lowercase identifier; must be unique across loaded plugins.
  # Duplicate server: identifiers cause a SchemaCrawlerException at startup.
  server: snowflake

  # Required — human-readable display name
  name: Snowflake

  # Required — JDBC URL template; ${host}, ${port}, ${database} are interpolated
  url-template: "jdbc:snowflake://${host}:${port}/"

  # Required — string prefix used for automatic detection when --url is passed directly
  url-prefix: "jdbc:snowflake:"

  # Optional — default TCP port
  default-port: 443

  # Optional — default JDBC URL extra properties (mapped to withDefaultUrlx calls).
  # Values are strings. Every key listed here must also appear in allowed-driver-properties
  # (when that field is present), or startup validation fails.
  default-url-properties:
    db: "${database}"                 # maps database placeholder into URL property
    CLIENT_SESSION_KEEP_ALIVE: "true"

  # Optional — allowlist of JDBC connection property names accepted by the driver.
  # Mirrors the Java pattern DatabaseConnectionSourceBuilder.withAdditionalDriverProperties()
  # used by DB2DatabaseConnector. When set, any --urlx=key=value whose key is NOT in this
  # list is rejected at startup with a clear error message. When absent, no filtering is
  # applied (open, permissive mode).
  # All keys listed in default-url-properties must also appear here.
  allowed-driver-properties:
    - db
    - schema
    - warehouse
    - role
    - CLIENT_SESSION_KEEP_ALIVE
    - user
    - password
    - privateKey
    - privateKeyFile
    - authenticator
    - loginTimeout
    - networkTimeout
    - queryTimeout
    - tracing
    - proxyHost
    - proxyPort

  # Optional — additional named CLI options beyond --server/--host/--port/--database.
  # These are declared via PluginCommand.addOption() and appear in the server's help page.
  # At runtime, users pass them as --urlx=key=value.
  # Each entry has: name, type (String|Integer|Boolean), help text lines, and
  # an optional urlx-key override (defaults to the option name).
  additional-options:
    - name: warehouse
      type: String
      urlx-key: warehouse
      help:
        - "Snowflake virtual warehouse name"
        - "Optional, defaults to the account default"
    - name: role
      type: String
      urlx-key: role
      help:
        - "Snowflake role name"
        - "Optional, defaults to the account default role"

  # Optional — help strings for the standard CLI options.
  # Each key is one of: server, host, port, database.
  # Values are lists of description lines (picocli multi-line convention).
  help:
    server:
      - "--server=snowflake"
      - "Loads SchemaCrawler plug-in for Snowflake"
    host:
      - "Snowflake account identifier"
      - "(e.g. myorg-myaccount.snowflakecomputing.com)"
    port:
      - "Port number"
      - "Optional, defaults to 443"
    database:
      - "Snowflake database name"

  # Optional — schema retrieval strategy overrides.
  # Keys must be valid SchemaInfoMetadataRetrievalStrategy field names.
  # Values must be valid MetadataRetrievalStrategy enum constant names.
  schema-retrieval:
    tableColumnsRetrievalStrategy: metadata_over_schemas

  # Optional — identifier quote character (e.g. `"` for standard SQL, `` ` `` for MySQL-style)
  identifier-quote-string: "\""

  # Optional — schema/catalog inclusion/exclusion (Java regex).
  # Exactly one of include-schemas or exclude-schemas may be set; same for catalogs.
  limit:
    exclude-schemas: "INFORMATION_SCHEMA"
    # include-schemas: ".*"
    # exclude-catalogs: "..."
    # include-catalogs: "..."
```

### 5.2 Schema design decisions

- **`url-template`** uses the identical placeholder syntax accepted by
  `DatabaseConnectionSourceBuilder.builder(...)`.
- **`url-prefix`** maps to `withUrlStartsWith(...)`.
- **`default-url-properties`** map to `withDefaultUrlx(key, value)` calls.
- **`allowed-driver-properties`** maps to `withAdditionalDriverProperties(Set<String>)`. When
  present, every key in `default-url-properties` must also be listed here; validation is performed
  at YAML parse time (not at connection time), giving immediate feedback on authoring errors.
- **`additional-options`** declares named driver-specific properties using the
  `PluginCommand.addOption()` API. These appear in the plugin's help page. Users pass them at
  runtime via `--urlx=key=value`.
- **`schema-retrieval`** uses exact `SchemaInfoMetadataRetrievalStrategy` Java field names as
  keys. A static lookup map in `YamlDatabaseConnector` resolves them — no reflection, no
  `setAccessible()`.
- **`identifier-quote-string`** covers the SQLite pattern.
- **`limit`** covers include/exclude for schemas and catalogs.
- **Information-schema SQL overrides are out of scope for v1** — connectors needing custom SQL
  continue to use Java.

---

## 6. Java Model and Implementation Design

The implementation is structured in four distinct layers with clear separation of concerns.
**YAML parsing code is completely isolated from the bridge.**

### 6.1 Model layer — records holding YAML data

Pure Java records with no framework annotations. These hold the data parsed from YAML and have no
knowledge of Jackson, SchemaCrawler options, or picocli.

```
schemacrawler.plugins.dbplugins.model
  ├─ DatabasePluginDefinition           // top-level record: server, name, urlTemplate, ...
  ├─ UrlPropertyDefinition              // name, value (for default-url-properties entries)
  ├─ AdditionalOptionDefinition         // name, type, urlxKey, help[]
  ├─ HelpDefinition                     // server[], host[], port[], database[]
  ├─ SchemaRetrievalDefinition          // Map<String,String> of strategy overrides
  └─ LimitDefinition                    // excludeSchemas, includeSchemas, excludeCatalogs, includeCatalogs
```

`DatabasePluginDefinition` additionally carries `Set<String> allowedDriverProperties` (empty
meaning unrestricted).

### 6.2 YAML parsing layer — isolated Jackson usage

```
schemacrawler.plugins.dbplugins.yaml
  ├─ DatabasePluginYamlDeserializer      // Jackson ObjectMapper + YAMLFactory; reads
  │                                       // YAML into DatabasePluginDefinition records
  └─ (Jackson POJOs / @JsonProperty here, NOT in the model layer)
```

`DatabasePluginYamlDeserializer.parse(InputStream) → DatabasePluginDefinition` — this is the only
class that touches Jackson.

Post-parse validation (before returning the record):
1. If `allowedDriverProperties` is non-empty, verify that every key in `defaultUrlProperties`
   appears in `allowedDriverProperties`. Throw `SchemaCrawlerException` with the offending key
   name if not.

### 6.3 Connector builder — model → `DatabaseConnector`

```
schemacrawler.plugins.dbplugins
  └─ YamlDatabaseConnector extends DatabaseConnector
```

Takes a `DatabasePluginDefinition` in its constructor. Has no knowledge of Jackson or YAML.
Translates the model into the builder chain:

| Model field | Builder API |
|---|---|
| `urlTemplate` | `DatabaseConnectionSourceBuilder.builder(urlTemplate)` |
| `defaultPort` | `.withDefaultPort(defaultPort)` |
| `defaultUrlProperties` | `.withDefaultUrlx(key, value)` for each entry |
| `allowedDriverProperties` (non-empty) | `.withAdditionalDriverProperties(allowedDriverProperties)` |
| `server` + `name` | `new DatabaseServerType(server, name)` |
| `urlPrefix` | `withUrlStartsWith(urlPrefix)` |
| `help.server/host/port/database` | `PluginCommand.addOption(name, type, helpLines...)` |
| `additionalOptions` | `PluginCommand.addOption(name, type, helpLines...)` for each entry |
| `schemaRetrieval` entries | `schemaRetrievalOptionsBuilder.with(strategy, value)` via static lookup map |
| `identifierQuoteString` | `schemaRetrievalOptionsBuilder.withIdentifierQuoteString(...)` |
| `limit.excludeSchemas` | `limitOptionsBuilder.includeSchemas(new RegularExpressionExclusionRule(...))` |
| `limit.includeSchemas` | `limitOptionsBuilder.includeSchemas(new RegularExpressionInclusionRule(...))` |
| `limit.excludeCatalogs` | `limitOptionsBuilder.includeCatalogs(new RegularExpressionExclusionRule(...))` |
| `limit.includeCatalogs` | `limitOptionsBuilder.includeCatalogs(new RegularExpressionInclusionRule(...))` |

The `SchemaInfoMetadataRetrievalStrategy` resolution uses a **static
`Map<String, SchemaInfoMetadataRetrievalStrategy>`** keyed by field name — no reflection,
compatible with ArchUnit.

### 6.4 Discovery layer

```
schemacrawler.plugins.dbplugins
  └─ YamlDatabasePluginLoader
```

`YamlDatabasePluginLoader.loadAll() → List<YamlDatabaseConnector>`:

1. **Classpath**: Enumerate all resources matching `schemacrawler-dbplugins/*.yaml` via
   `ClassLoader.getResources("schemacrawler-dbplugins")`. This is a directory at the classpath
   root — consistent with the module name, not inside `META-INF/`.
2. **Filesystem**: Read all `*.yaml` files from the directory given by system property
   `schemacrawler.plugins.dir` or environment variable `SC_PLUGINS_DIR`, defaulting to
   `~/.schemacrawler/plugins/`.
3. For each YAML stream, call `DatabasePluginYamlDeserializer.parse(stream)` →
   `DatabasePluginDefinition` → `new YamlDatabaseConnector(definition)`.
4. **Duplicate detection**: After parsing all files, check the accumulated set of `server:`
   identifiers. If any identifier appears more than once, throw `SchemaCrawlerException` naming
   the conflicting identifier and its source files.

### 6.5 Bridge — the single ServiceLoader entry

```
schemacrawler.plugins.dbplugins
  └─ YamlDatabaseConnectorBridge
         implements DatabaseConnector, DatabaseConnectorBundle
```

**The bridge is NOT itself a server**:
- Its `getDatabaseServerType()` returns a sentinel/internal type that is NOT registered in the
  map — the `DatabaseConnectorRegistry` skips registering the bridge directly (because it
  implements `DatabaseConnectorBundle`).
- Its `getDatabaseConnectors()` calls `YamlDatabasePluginLoader.loadAll()` and returns the
  results.
- It delegates no other `DatabaseConnector` methods — those are never called on the bridge
  itself.

The single `META-INF/services/schemacrawler.tools.databaseconnector.DatabaseConnector` entry:
```
schemacrawler.plugins.dbplugins.YamlDatabaseConnectorBridge
```

Each `YamlDatabaseConnector` returned from the bridge is registered individually in the
`DatabaseConnectorRegistry` map, appears in `--server` tab-completion, and has its `PluginCommand`
published to `--help servers`.

### 6.6 Additional CLI options — the plugin API

The `PluginCommand.addOption()` mechanism is the existing plugin API for surfacing named options in
a server's help page. The YAML `additional-options` list feeds directly into this:

- **Help output**: `--warehouse <warehouse>   Snowflake virtual warehouse name` appears in
  `-h snowflake`.
- **Runtime**: Users pass `--urlx=warehouse=mywarehouse` on the command line, which is merged
  into the `DatabaseServerHostConnectionOptions.urlx` map and forwarded to
  `DatabaseConnectionSourceBuilder.withUrlx(urlx)`.
- **No changes** to `schemacrawler-commandline` are required for v1. The existing `--urlx`
  mechanism handles runtime values; the plugin API handles documentation.

---

## 7. Bundled YAML Files

Five files live at `src/main/resources/schemacrawler-dbplugins/` in the module. Each includes a
curated `allowed-driver-properties` list sourced from the respective driver's documentation,
following the DB2 connector pattern.

### 7.1 `snowflake.yaml`

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
  allowed-driver-properties:
    - db
    - schema
    - warehouse
    - role
    - CLIENT_SESSION_KEEP_ALIVE
    - user
    - password
    - privateKey
    - privateKeyFile
    - authenticator
    - loginTimeout
    - networkTimeout
    - queryTimeout
    - tracing
    - proxyHost
    - proxyPort
  additional-options:
    - name: warehouse
      type: String
      urlx-key: warehouse
      help: ["Snowflake virtual warehouse name", "Optional"]
    - name: role
      type: String
      urlx-key: role
      help: ["Snowflake role name", "Optional"]
  help:
    server: ["--server=snowflake", "Loads SchemaCrawler plug-in for Snowflake"]
    host: ["Snowflake account identifier (e.g. myorg-myaccount.snowflakecomputing.com)"]
    port: ["Port number", "Optional, defaults to 443"]
    database: ["Snowflake database name"]
  limit:
    exclude-schemas: "INFORMATION_SCHEMA"
```

> **Note**: Snowflake JDBC encodes the database as a URL property (`db=`) not a path segment.
> This needs verification against current Snowflake JDBC driver documentation before finalising.

### 7.2 `access.yaml`

```yaml
plugin:
  server: access
  name: Microsoft Access
  url-template: "jdbc:ucanaccess://${database}"
  url-prefix: "jdbc:ucanaccess:"
  default-url-properties:
    memory: "false"
    showSchema: "true"
  allowed-driver-properties:
    - memory
    - showSchema
    - singleConnection
    - columnOrder
    - ignoreCase
    - lobScale
    - maxMemoFieldLength
    - concatNulls
    - immediatelyReleaseResources
    - jackcessOpener
    - openMode
    - inactivityTimeout
    - password
    - newDatabaseVersion
  additional-options:
    - name: memory
      type: Boolean
      urlx-key: memory
      help: ["Load database into memory", "Optional, defaults to false"]
  help:
    server: ["--server=access", "Loads SchemaCrawler plug-in for Microsoft Access (UCanAccess)"]
    host: ["Should be omitted"]
    port: ["Should be omitted"]
    database: ["Path to the .accdb or .mdb file"]
  schema-retrieval:
    tableColumnsRetrievalStrategy: metadata_over_schemas
```

### 7.3 `clickhouse.yaml`

```yaml
plugin:
  server: clickhouse
  name: ClickHouse
  url-template: "jdbc:clickhouse://${host}:${port}/${database}"
  url-prefix: "jdbc:clickhouse:"
  default-port: 8123
  default-url-properties:
    socket_timeout: "300000"
  allowed-driver-properties:
    - socket_timeout
    - compress
    - decompress
    - http_connection_timeout
    - http_receive_timeout
    - http_send_timeout
    - max_rows_to_read
    - max_bytes_to_read
    - use_time_zone
    - use_server_time_zone
    - use_objects_in_arrays
    - user
    - password
    - ssl
    - sslmode
    - sslrootcert
  additional-options:
    - name: compress
      type: Boolean
      urlx-key: compress
      help: ["Enable data compression", "Optional, defaults to true"]
  help:
    server: ["--server=clickhouse", "Loads SchemaCrawler plug-in for ClickHouse"]
    host: ["Host name", "Optional, defaults to localhost"]
    port: ["Port number", "Optional, defaults to 8123"]
    database: ["Database name"]
  limit:
    exclude-schemas: "INFORMATION_SCHEMA|system"
```

### 7.4 `trino.yaml`

```yaml
plugin:
  server: trino
  name: Trino
  url-template: "jdbc:trino://${host}:${port}/${database}"
  url-prefix: "jdbc:trino:"
  default-port: 8080
  allowed-driver-properties:
    - user
    - password
    - sessionUser
    - SSL
    - SSLVerification
    - SSLTrustStorePath
    - SSLTrustStorePassword
    - SSLTrustStoreType
    - SSLKeyStorePath
    - SSLKeyStorePassword
    - SSLKeyStoreType
    - applicationNamePrefix
    - clientInfo
    - clientTags
    - traceToken
    - timezone
    - locale
    - queryInterceptors
    - httpProxy
    - socksProxy
    - assumeLiteralNamesInMetadataCallsForNonConformingClients
    - assumeLiteralUnderscoreInMetadataCallsForNonConformingClients
    - disableCompression
    - extraCredentials
    - roles
    - sessionProperties
    - protocols
    - accessToken
    - externalAuthentication
    - kerberosEnabled
    - kerberosServicePrincipalPattern
    - kerberosPrincipal
    - kerberosRemoteServiceName
    - kerberosUseCanonicalHostname
    - kerberosConfigPath
    - kerberosKeytabPath
    - kerberosCredentialCachePath
    - kerberosDelegation
    - httpLoggingLevel
  additional-options:
    - name: user
      type: String
      urlx-key: user
      help: ["Trino user name", "Optional"]
    - name: SSL
      type: Boolean
      urlx-key: SSL
      help: ["Enable SSL", "Optional, defaults to false"]
  help:
    server: ["--server=trino", "Loads SchemaCrawler plug-in for Trino"]
    host: ["Trino coordinator host name", "Optional, defaults to localhost"]
    port: ["Port number", "Optional, defaults to 8080"]
    database: ["Catalog name"]
  limit:
    exclude-schemas: "INFORMATION_SCHEMA"
```

### 7.5 `cassandra.yaml`

```yaml
plugin:
  server: cassandra
  name: Apache Cassandra
  url-template: "jdbc:cassandra://${host}:${port}/${database}"
  url-prefix: "jdbc:cassandra:"
  default-port: 9042
  default-url-properties:
    consistency: "LOCAL_ONE"
  allowed-driver-properties:
    - consistency
    - loadbalancing
    - version
    - contactPoints
    - port
    - localDataCenter
    - username
    - password
    - authProvider
    - sslOptions
    - fetchSize
    - connectTimeout
    - readTimeout
    - compression
    - maxSchemaAgreementWaitSeconds
    - reconnectionPolicy
    - retryPolicy
    - requestTracker
    - asyncQueryTimeout
    - keyspace
  additional-options:
    - name: consistency
      type: String
      urlx-key: consistency
      help: ["Consistency level", "Optional, defaults to LOCAL_ONE"]
    - name: loadbalancing
      type: String
      urlx-key: loadbalancing
      help: ["Load balancing policy", "Optional"]
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

## 8. Full Module Structure

```
schemacrawler-dbplugins/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/
    │   │   └── schemacrawler/plugins/dbplugins/
    │   │       ├── model/                          ← pure model records, no Jackson
    │   │       │   ├── DatabasePluginDefinition.java
    │   │       │   ├── UrlPropertyDefinition.java
    │   │       │   ├── AdditionalOptionDefinition.java
    │   │       │   ├── HelpDefinition.java
    │   │       │   ├── SchemaRetrievalDefinition.java
    │   │       │   └── LimitDefinition.java
    │   │       ├── yaml/                           ← Jackson parsing, isolated
    │   │       │   └── DatabasePluginYamlDeserializer.java
    │   │       ├── YamlDatabaseConnector.java      ← model → DatabaseConnector
    │   │       ├── YamlDatabasePluginLoader.java   ← classpath + filesystem scanner
    │   │       └── YamlDatabaseConnectorBridge.java ← ServiceLoader entry, not a server
    │   └── resources/
    │       ├── META-INF/services/
    │       │   └── schemacrawler.tools.databaseconnector.DatabaseConnector
    │       │       (one line: schemacrawler.plugins.dbplugins.YamlDatabaseConnectorBridge)
    │       └── schemacrawler-dbplugins/            ← classpath root, consistent with module name
    │           ├── snowflake.yaml
    │           ├── access.yaml
    │           ├── clickhouse.yaml
    │           ├── trino.yaml
    │           └── cassandra.yaml
    └── test/
        └── java/schemacrawler/plugins/dbplugins/
            ├── model/
            │   └── DatabasePluginDefinitionTest.java
            ├── yaml/
            │   └── DatabasePluginYamlDeserializerTest.java
            ├── YamlDatabaseConnectorTest.java
            ├── YamlDatabasePluginLoaderTest.java
            └── BundledPluginsIntegrationTest.java
```

---

## 9. ArchUnit Compliance

| Rule | How `schemacrawler-dbplugins` satisfies it |
|---|---|
| No `System.out` / `System.err` in production code | Use `java.util.logging` throughout |
| No generic exception throws | Throw specific exceptions; use `SchemaCrawlerException` where applicable |
| `@ModelImplementation` / `@Retriever` classes package-private | N/A — no model/retriever classes in the SchemaCrawler-Core sense |
| `lookup*()` methods return `Optional` | N/A — no lookup methods in this module |
| No `setAccessible()` | The `SchemaInfoMetadataRetrievalStrategy` resolution uses a static `EnumMap`, not reflection |
| No package cycles | `model/` ← used by `yaml/`, `YamlDatabaseConnector`, loader, bridge; no reverse dependency |

---

## 10. Implementation Sequence

1. **Core PR (SchemaCrawler-Core)**: Add `DatabaseConnectorBundle` interface (no Jackson, pure
   Java). Update `DatabaseConnectorRegistry.loadDatabaseConnectorRegistry()` to unwrap bundles,
   applying the Java-wins collision policy. Add unit tests for normal registration, bundle
   expansion, Java-wins-over-YAML shadowing, and duplicate-YAML-server-id exception.

2. **Module scaffold**: Create `schemacrawler-dbplugins/pom.xml`; register in root `pom.xml`.

3. **Model records** (`model/` package): All six records including `allowedDriverProperties:
   Set<String>` in `DatabasePluginDefinition`. Unit tests with in-memory construction.

4. **YAML parsing layer** (`yaml/` package): `DatabasePluginYamlDeserializer` using `YAMLMapper`.
   Includes post-parse validation that `default-url-properties` keys are covered by
   `allowed-driver-properties` when the latter is non-empty. Unit tests parsing each of the five
   bundled YAML files and asserting all fields.

5. **`YamlDatabaseConnector`**: Translation from `DatabasePluginDefinition` →
   `DatabaseConnectorOptions`. Includes the static `SchemaInfoMetadataRetrievalStrategy` lookup
   map. Calls `withAdditionalDriverProperties()` when the allowlist is present. Unit tests
   covering URL construction, plugin command option generation, limit rule generation, and
   allowlist wiring.

6. **`YamlDatabasePluginLoader`**: Classpath discovery, filesystem discovery, and duplicate
   `server:` detection. Unit tests with a temp directory of YAML files including a duplicate-id
   failure case.

7. **`YamlDatabaseConnectorBridge`**: ServiceLoader entry; implements `DatabaseConnectorBundle`;
   calls loader; not registered as a server. Integration test verifying all five bundled plugins
   appear individually in a `DatabaseConnectorRegistry` loaded with the bridge on the classpath.

8. **Bundled YAML files**: The five YAML definitions with curated `allowed-driver-properties`
   lists. Test that each connector produces correct `PluginCommand` help output.

9. **Documentation update**: Add module to `AGENTS.md` table; add a user guide section on
   authoring YAML plugins.

---

## 11. User-Extensible Filesystem Plugins

Once the module is on the classpath, adding a new server requires only:

1. Create `~/.schemacrawler/plugins/mydb.yaml` (or set `SC_PLUGINS_DIR` to point elsewhere).
2. Place the JDBC driver JAR on the classpath.
3. Run `schemacrawler --server=mydb ...`.

No Java, no recompilation, no JAR packaging.

> **Security note**: YAML files from the filesystem plugin directory are loaded at startup. They
> can declare arbitrary JDBC URL templates, meaning a tampered YAML file could redirect connections
> to a malicious host. The `allowed-driver-properties` allowlist reduces the attack surface by
> preventing injection of unexpected connection properties, but the filesystem directory itself
> should be protected by OS-level file permissions (owner-readable only, `chmod 700
> ~/.schemacrawler/plugins/`). Consider documenting this guidance.

---

## 12. Out of Scope for v1

- Information-schema SQL query overrides from YAML (complex, deferred to v2)
- Connection initializers (e.g. Oracle `ALTER SESSION`) — Java-only feature
- Enum data type helpers (e.g. PostgreSQL `pg_enum`) — Java-only feature
- Mixing server plugin options into the `connect` command as picocli-parsed options (users use
  `--urlx=key=value` at runtime; the `additional-options` YAML field handles documentation)
- Automatic JDBC driver download/resolution
- JSON Schema document for YAML validation

---

## 13. Resolved Design Decisions

| # | Question | Decision |
|---|---|---|
| 1 | **Snowflake URL shape**: path segment vs `db=` URL property? | Use `db=` via `default-url-properties`. Verify against current Snowflake JDBC driver documentation before finalising. |
| 2 | **`additional-options` at runtime** | v1: users pass `--urlx=key=value`. Future v2 enhancement: mix server plugin commands into `ConnectCommand` for named option parsing (e.g. `--warehouse=mywarehouse`). |
| 3 | **Classpath collision between two YAML files** | Throw `SchemaCrawlerException` — duplicate `server:` identifiers are a packaging/authoring error and must be fixed explicitly. |
| 4 | **Java connector vs YAML connector with same `server:` id** | Java always wins. Java connectors are registered first; YAML connector is silently skipped with a `DEBUG` log if its identifier is already registered. |
| 5 | **Protecting against unknown JDBC URL parameters** | Add `allowed-driver-properties` to the YAML schema, mirroring `DatabaseConnectionSourceBuilder.withAdditionalDriverProperties()` as used by `DB2DatabaseConnector`. Validation at YAML parse time. |
