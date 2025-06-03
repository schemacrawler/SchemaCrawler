# SchemaCrawler Change History

SchemaCrawler release notes.

<a name="v16.26.1"></a>
## Release 16.26.1 - 2025-06-02

- Released additional tools for SchemaCrawler MCP Server
- Removed deprecated methods


<a name="v16.26.1"></a>
## Release 16.26.1 - 2025-05-24

- Released SchemaCrawler MCP Server


<a name="v16.25.3"></a>
## Release 16.25.3 - 2025-03-24

- Fix download scripts - fixes [#1882](https://github.com/schemacrawler/SchemaCrawler/issues/1882)


<a name="v16.25.2"></a>
## Release 16.25.2 - 2025-01-12

- Add convenience methods to Table for checking if there are indexes and triggers - fixes [#1831](https://github.com/sualeh/schemacrawler-issue1831)
- Externalize a `LinterInitializer` so that `Linters` can be used with a custom registry


<a name="v16.25.1"></a>
## Release 16.25.1 - 2024-12-27

- Fix #1815 - show column default value in text output


<a name="v16.24.3"></a>
## Release 16.24.3 - 2024-12-27

- Fix #1811 - pom for lint (see [sualeh/schemacrawler-issue1811](https://github.com/sualeh/schemacrawler-issue1811))
- Tweaks to output for connection test


<a name="v16.24.2"></a>
## Release 16.24.2 - 2024-12-21

- Allow table retrieval to proceed even if there are no permissions for a given schema
- Add command-line switch (--connection-test) to do a database connection test
- Print JVM CPU architecture in the logs


<a name="v16.24.1"></a>
## Release 16.24.1 - 2024-12-15

- Move ChatGPT integration into a separate top-level project
- Fix issue #1768 by standardizing how all reports are generated
- Build Docker image based on ARM 64 architecture


<a name="v16.23.2"></a>
## Release 16.23.2 - 2024-11-26

- Add back `getCrawlInfo` into the `BaseLinter` to allow linters to act based on a particular database system (see #1750)
- Create a lint report build to allow for custom lint reports to created programmatically (see #1750)


<a name="v16.23.1"></a>
## Release 16.23.1 - 2024-11-24

- Remove deprecated `--portable-names` option
- Add constraint column usage for MySQL and PostgreSQL
- Fix #1750 - avoid storing lints in catalog
- Run linters run in parallel
- Enhance lint report to allow streaming lints


<a name="v16.22.3"></a>
## Release 16.22.3 - 2024-11-06

- Deprecate `--portable-names` option in favor of `--portable`,
  which allows a better diff across different systems


<a name="v16.22.2"></a>
## Release 16.22.2 - 2024-08-04

- Fix issue #1560 - do not distribute the Cassandra JDBC driver
- Update dependencies


<a name="v16.22.1"></a>
## Release 16.22.1 - 2024-07-31

- Standardize plugin registry code
- Remove lint dispatch that allows system exit
- Allow building both on Java 8 and Java 21
- Move schemacrawler-diff proof of concept code to another project
- Split linters into linter providers in the registry, and actual linters that are instantiated when needed


<a name="v16.21.4"></a>
## Release 16.21.4 - 2024-06-18

- Update dependencies and database drivers
- Reduce dependency on Spring framework


<a name="v16.21.3"></a>
## Release 16.21.3 - 2024-06-09

- Update dependencies and database drivers


<a name="v16.21.2"></a>
## Release 16.21.2 - 2024-02-16

- Remove Microsoft Access JDBC driver distribution, since the driver is not being maintained
- Allow SchemaCrawler ChatGPT plugin to do Retrieval Augmented Generation (RAG) for optimal help with SQL queries


<a name="v16.21.1"></a>
## Release 16.21.1 - 2024-01-15

- Add a timeout option for ChatGPT
- Distribute MariaDB JDBC driver
- Change Trigger model to allow for multiple event manipulation types (fixes #1418)
- Change text and HTML output for triggers
- Fix #1419 - primary keys created using index not registered as primary keys for Oracle


<a name="v16.20.8"></a>
## Release 16.21.8 - 2023-12-30

- Streamline slf4j dependencies, and control everything using JDK logging


<a name="v16.20.7"></a>
## Release 16.20.7 - 2023-12-22

- Fix issue #1371 - add option to specify table and routine types to limit in a config file
- Add "compact_json" as an additional serialization format, to use with ChatGPT


<a name="v16.20.6"></a>
## Release 16.20.6 - 2023-11-17

- Fix issue #1333 - add joins to Oracle foreign key query


<a name="v16.20.5"></a>
## Release 16.20.5 - 2023-11-07

- Fix issue #1294 - allow search through multi-line definitions with grep


<a name="v16.20.4"></a>
## Release 16.20.4 - 2023-07-26

- Support "system functions" for the ChatGPT plugin which allows metadata to be sent to OpenAI for analysis
- Add a command-line argument `--use-metadata` for the ChatGPT plugin to get consent from the user to share metadata with OpenAIA


<a name="v16.20.3"></a>
## Release 16.20.3 - 2023-07-20

- Use SchemaCrawler commands for output from ChatGPT plugin
- Support lint in ChatGPT interactions
- Allow hiding of specific portions of table output, such as primary keys, foreign keys, indexes, and so on


<a name="v16.20.2"></a>
## Release 16.20.2 - 2023-07-07

- Add chat context for ChatGPT


<a name="v16.20.1"></a>
## Release 16.20.1 - 2023-07-03

- Add plugin for ChatGPT
- Fix issue #1179 for Mermaid diagram generation


<a name="v16.26.1"></a>
## Release 16.26.1 - 2023-06-03

- Fix issue #1146 for version of Apache Ivy 2.5.1
- Fix issue #1139 with allowing overrides of connection initializers in data sources


<a name="v16.19.10"></a>
## Release 16.19.10 - 2023-05-21

- Enhance Mermaid diagram generation with remarks and primary and foreign key designations
- Distribute Python script to generate Markdown output
- Fix issue #1130 with filtering quoted names


<a name="v16.19.9"></a>
## Release 16.19.9 - 2023-03-21

- Update drivers


<a name="v16.19.8"></a>
## Release 16.19.8 - 2023-03-18

- Quote mixed-case database object names for databases that support them - fixes #1033
- Support DuckDB JDBC driver


<a name="v16.19.8"></a>
## Release 16.19.8 - 2023-01-20

- Clean up interdependencies in distributed libraries


<a name="v16.19.6"></a>
## Release 16.19.6 - 2023-01-12

- Add support for Cassandra


<a name="v16.19.5"></a>
## Release 16.19.5 - 2022-12-16

- Fix issues with running in multiple threads
- Fix issue #961 - deserialization missing "java.net.*" as an allowed type
- Update copyright notices


<a name="v16.19.4"></a>
## Release 16.19.4 - 2022-11-30

- Fix issues with generation of PlantUML diagram


<a name="v16.19.3"></a>
## Release 16.19.3 - 2022-11-27

- Add a slug method to NamedObjectKey for readable unique ids
- Release PlantUML diagram generator
- Allow scripts and templates to use the title of the diagram or output


<a name="v16.19.2"></a>
## Release 16.19.2 - 2022-11-24

- Fix #933 - NullPointerException when using custom schema info level
- Fix #931 - Hive does not support DatabaseMetaData.getUserName()


<a name="v16.19.1"></a>
## Release 16.19.1 - 2022-11-20

- SchemaCrawler runs multi-threaded by defualt, but you can force single-threading with `SC_SINGLE_THREADED`=`true`
  as a environmental variable or Java system property
- Fix for Hive does not support DatabaseMetaData.getURL() #910


<a name="v16.18.2"></a>
## Release 16.18.2 - 2022-09-12

- In experimental mode, use multi-threading for obtaining table columns


<a name="v16.18.1"></a>
## Release 16.18.1 - 2022-08-23

- Breaking change to APIs by using database connection sources instead of database connections
  to allow for true multi-threading with multiple connections
- Allow multi-threading to be turned on with `SC_EXPERIMENTAL=true` either as an
  environmental variable or a Java system property
- Fix #835 - Oracle SQLException when multi-threading turned on


<a name="v16.17.4"></a>
## Release 16.17.4 - 2022-08-14

- Proxy database connections, and close them by closing the datasource
- Fix issue #826 - add attributes for tables when retrieving them


<a name="v16.17.3"></a>
## Release 16.17.3 - 2022-08-05

- Allow more variations of output in tutorials


<a name="v16.17.2"></a>
## Release 16.17.2 - 2022-07-30

- When determining weak associations, by default only match patterns similar to `table2.table1_id` -> `table1.id`
- Introduce a `--infer-extension-tables` command-line switch to look for tables that share a primary key name


<a name="v16.17.1"></a>
## Release 16.17.1 - 2022-07-22

- Experimental feature to run catalog loader with multiple threads
- Fix weak associations algorithm to find common use cases - fixes #793
- Extract IBM DB2 database name into the `catalog.getDatabaseInfo().getServerInfo()` - fixes #789
- Change references to online tutorials to Killercoda - fixes #781
- Assign names to foreign keys where the database has not assigned a name
- Do not have inconsistent column references in a table reference (such as a foreign key or weak association)


<a name="v16.16.18"></a>
## Release 16.16.18 - 2022-06-20

- Add support for updating remarks for foreign keys in diagrams and other output
- Ensure that column privileges are being retrieved for databases that support them
- Add website page on security considerations


<a name="v16.16.17"></a>
## Release 16.16.17 - 2022-06-12

- Fix issue #749 - Diagram shows hanging references when columns are not displayed by the "brief" command


<a name="v16.16.16"></a>
## Release 16.16.16 - 2022-06-07

- Add new diagram option to show or hide tables that are filtered out, which
  can be set in the configuration properties file with
  `schemacrawler.graph.show.foreignkey.filtered_tables=false`


<a name="v16.16.15"></a>
## Release 16.16.15 - 2022-05-30

- Exclude entity management framework metadata tables (from Liquibase, Flyway, django, EF Core, and so on)
  by default from SQLLite schemas, when using `EmbeddedSQLiteWrapper`


<a name="v16.16.14"></a>
## Release 16.16.14 - 2022-03-27

- Distribute Mermaid and DBML diagram creation scripts in Docker image
- Drop support for Oracle 11g and rework Oracle plugin
- Fix #660 - Load plugins with the caller's class loader


<a name="v16.16.12"></a>
## Release 16.16.12 - 2022-03-20

- Add database plugin to support Teiid
- Add support for hidden or invisible columns in MySQL and MariaDB
- Throw runtime exception if SchemaCrawler database plugin is not found on the CLASSPATH


<a name="v16.16.11"></a>
## Release 16.16.11 - 2022-02-05

- Update dependencies
- Release to Chocolatey and SDKMan from CI pipelines using JReleaser


<a name="v16.16.10"></a>
## Release 16.16.10 - 2022-01-25

- Change foreign key "key" to be consistent with a constraint "key"
- Create binary distribution, and move shell scripts into a bin/ folder


<a name="v16.16.9"></a>
## Release 16.16.9 - 2022-01-17

- Fix issue #556, #559 - Could not commit with auto-commit set on error on Oracle
- Update database drivers for MySQL and H2


<a name="v16.16.8"></a>
## Release 16.16.8 - 2022-01-14

- Fix issue #556 - NoClassDefFoundError when crawling an Oracle schema


<a name="v16.16.7"></a>
## Release 16.16.7 - 2022-01-07

- Show better error message with no command-line arguments
- Update vulnerable version of H2
- Add convenience methods to get parent and children tables
- Allow for null or blank privilege names in output
- Add tests for Informix, and distribute Informix JDBC driver
- Reorganize how remarks are handled, and show Microsoft SQL Server remarks for tables and columns


<a name="v16.16.6"></a>
## Release 16.16.6 - 2021-12-29

- Show routine definitions for Microsoft SQL Server
- Update instructions to install SchemaCrawler from the Scoop Main bucket
- Add a check for no command-line arguments, and exit early
- Add additional logging for table types and data type mappings reported by the JDBC driver


<a name="v16.16.5"></a>
## Release 16.16.5 - 2021-12-26

- Update dependencies, especially the H2 Database version


<a name="v16.16.4"></a>
## Release 16.16.4 - 2021-12-16

- Read SC_GRAPHVIZ_PROC_DISABLE boolean environmental variable or system property to disable creating native process for Graphviz
- Update copyright notices
- Update dependencies
- Put in preventive measures for [CVE-2021-44228](https://nvd.nist.gov/vuln/detail/CVE-2021-44228)
- Add link to SchemaCrawler API on RapidAPI
- Add note warning to remove scripting jar if not needed


<a name="v16.16.3"></a>
## Release 16.16.3 - 2021-11-16

- Use Semantic Versioning for releases
- GZIP serialized database schema offline snapshots


<a name="v16.16.02"></a>
## Release 16.16.02 - 2021-11-16

- Fix stopwatch logging


<a name="v16.16.01"></a>
## Release 16.16.01 - 2021-11-15

- Prefer runtime exceptions to checked exceptions in the API, and create more specific exception classes for SchemaCrawler exceptions
- Fix #535 - issue when logging offline connections
- Remove tests for MonetDB


<a name="v16.15.11"></a>
## Release 16.15.11 - 2021-11-01

- Show index and constraint remarks in output
- Create a new metadata extension view, EXT_TABLE_CONSTRAINTS for remarks and definitions of constraints
  and implement it for databases that support it


<a name="v16.15.10"></a>
## Release 16.15.10 - 2021-10-22

- Fix #522 - Crawling Oracle schema with non DBA user cause exception


<a name="v16.15.9"></a>
## Release 16.15.9 - 2021-10-19

- Make sure integration tests run on Java 8 and 17, on Windows and Linux, with and without Graphviz installed
- Use Graal JS as the preferred JavaScript engine for scripting and diagrams on all Java versions
- Fix printing of dumps when the data has array data types
- Show JSON-like config in logs


<a name="v16.15.8"></a>
## Release 16.15.8 - 2021-10-04

- Check for a valid command before loading catalog
- Fix issue #517 - TABLE_COLUMNS.sql causes SQL0604N against UTF-8 IBM DB2 database


<a name="v16.15.7"></a>
## Release 16.15.7 - 2021-09-15

- Fix issue #509 - distribute Docker image with JDK 17


<a name="v16.15.6"></a>
## Release 16.15.6 - 2021-09-12

- Fix issue #504 - fix logging level


<a name="v16.15.5"></a>
## Release 16.15.5 - 2021-09-11

- Fix issue #504 - do not load JDBC driver information based on SchemaInfoRetrieval.retrieveAdditionalJdbcDriverInfo
- Add supported JDBC version in JDBC driver information
- Reorganize output for JDBC driver information
- Add tests for Oracle 18


<a name="v16.15.4"></a>
## Release 16.15.4 - 2021-08-15

- Fix issue #496 - honor table includes in lint cyclic dependency checks
- Clarify information on licensing
- Create downloader for JavaScript engine for Java 15 and above


<a name="v16.15.2"></a>
## Release 16.15.2 - 2021-07-07

- Fix issue #482 - serialization for catalogs that have synonyms for objects that do not exist


<a name="v16.15.1"></a>
## Release 16.15.1 - 2021-06-12

- Remove `--only-matching` command-line option, and associated functionality


<a name="v16.14.6"></a>
## Release 16.14.6 - 2021-05-07

- Fix issue #465 with embedded HTML renderer
- Fix issue #466 with loading tables with a large number of exported foreign keys in SQL Server
- Fix issue #469 to ensure that foreign key names are scoped within a schema
- Fix issue #470 to remove ability to retrieve SQL Server stored procedure definitions


<a name="v16.14.5"></a>
## Release 16.14.5 - 2021-04-25

- Fix issue #461 with index cardinality


<a name="v16.14.4"></a>
## Release 16.14.4 - 2021-04-03

- Fix issue with exceptions thrown due to partial columns


<a name="v16.14.3"></a>
## Release 16.14.3 - 2021-03-30

- Add more documentation on output formats
- Change Java serialization output format to `ser` from `java`
- Allow better help for plugin commands, database plugins, and catalog loader plugins
- Send a Twitter tweet on a new release


<a name="v16.14.2"></a>
## Release 16.14.2 - 2021-03-26

- Add more documentation on website and command-line help for how to use SchemaCrawler
- Support specification of alternate keys in attributes file, and show them on output and diagrams
- Use only one enum for table constraints, even for user-defined metadata
- Remove option to show or hide weak associations,
  `schemacrawler.format.show_weak_associations`
- Distribute Snowflake JDBC driver with SchemaCrawler
- Show index information in diagrams with the maximum option
- Fix issue with SchemaCrawler shell accepting the `\` character
- Add new grep options, `--grep-tables`


<a name="v16.14.1"></a>
## Release 16.14.1 - 2021-03-10

- Move Docker Compose for SchemaCrawler to it's own project, with Maven moving `_testdb`
  files for use in the Docker compose scripts
- Make small code optimizations for performance
- Allow weak associations to be loaded from catalog attributes file
- Make WeakAssociation an interface, and consistent with other model classes
- Model foreign keys as table constraints for the referencing (foreign key) table
- Include all table constraints, including foreign keys in the table constraints list
- Fix documentation of linter configuration using YAML


<a name="v16.12.3"></a>
## Release 16.12.3 - 2021-02-23

- Add support for Apache Hive
- Add new NamedObjectKey class to encapsulate identifying key for schema objects

<a name="v16.12.2"></a>
## Release 16.12.2 - 2021-02-19

- Add examples for generating [mermaid](https://mermaid-js.github.io/mermaid/#/entityRelationshipDiagram)
and [dbdiagram.io](https://dbdiagram.io/home) diagrams


<a name="v16.12.1"></a>
## Release 16.12.1 - 2021-02-15

- Allow catalog loaders to be chained, and plugged in
- Rewrite weak associations analysis as a catalog loader
- Show table row counts by default, if they are loaded
- Replace `schemacrawler.format.show_row_counts` configuration option with `schemacrawler.format.hide_table_row_counts` with the opposite meaning
- Allow table and column remarks to be read in from a YAML file, and incorporated into the SchemaCrawler model
- Drop support for XML linter config file, and introduce a YAML file format
- Add instructions on how to generate [Mermaid Entity Relationship Diagrams](https://mermaid-js.github.io/mermaid/#/entityRelationshipDiagram)


<a name="v16.11.7"></a>
## Release 16.11.7 - 2020-12-26

- Add `--config-file` command back in, with more documentation on configuration on the website


<a name="v16.11.6"></a>
## Release 16.11.6 - 2020-11-28

- Fix broken examples
- Download dependencies once, to create an expanded distribution for Docker and jpackage


<a name="v16.11.5"></a>
## Release 16.11.5 - 2020-11-03

- Better logging for traceability, and better management of database connections


<a name="v16.11.4"></a>
## Release 16.11.4 - 2020-10-31

- Additional ways to provide the database username, for example by an environmental variable
- Publish [SchemaCrawler Action for GitHub Actions](https://github.com/schemacrawler/SchemaCrawler-Action)
- Exit with exit code 1 on a command-line error
- Fix issue with running plugins in interactive mode


<a name="v16.11.3"></a>
## Release 16.11.3 - 2020-10-23

- Add additional views to SAP HANA database plugin


<a name="v16.11.2"></a>
## Release 16.11.2 - 2020-10-22

- Release SAP HANA database plugin in <https://github.com/schemacrawler/SchemaCrawler-Database-Plugins>


<a name="v16.11.1"></a>
## Release 16.11.1 - 2020-10-18

- Make schema text commands a plugin into SchemaCrawler
- `show` and `sort` commands are folded into schema text command options
- Load configuration using [lightbend/config](https://github.com/lightbend/config)


<a name="v16.10.1"></a>
## Release 16.10.1 - 2020-09-20

- Set defaults programatically from database plugins
- Remove support for daisy chained commands (comma-separated)
- Reduce need for SchemaCrawler options builder


<a name="v16.9.5"></a>
## Release 16.9.5 - 2020-09-10

- Move loading options from configuration files to command-line
- Reorganize example configuration file to match command-line options


<a name="v16.9.4"></a>
## Release 16.9.4 - 2020-08-19

- Print complete help on registered linters from the command-line


<a name="v16.9.3"></a>
## Release 16.9.3 - 2020-07-19

- Change option to get help on SchemaCrawler commands, by using `--help command:lint` or
  `--help server:db2` for example
- Add ability to disable a database plugin by setting environment variable or
  system property `SC_WITHOUT_DATABASE_PLUGIN` to the name of the server -
  `SC_WITHOUT_DATABASE_PLUGIN=oracle` for example
- Do not read binary (BLOB and LONGVARBINARY) data
- Address issues with failure of JSON serialization


<a name="v16.9.2"></a>
## Release 16.9.2 - 2020-06-25

- Fix distribution image to download the correct dependencies, and remove duplicated jars
- Fix issue with the limit options builder


<a name="v16.9.1"></a>
## Release 16.9.1 - 2020-06-10

- Remove deprecated methods
- Allow for a JDBC driver class name to be specified for SchemaCrawler database plugins
- Add ability to retrieve database users using an additional data view
- Add ability to retrieve which tables are used by views, and show on output
- Rename OVERRIDE_TYPE_INFO to TYPE_INFO in data dictionary extensions
- Rename EXT_TABLE_CONSTRAINTS to CHECK_CONSTRAINTS in data dictionary extensions
- Add start value for sequences
- Remove support for EXT_PRIMARY_KEYS, EXT_FOREIGN_KEYS, and EXT_INDEX_COLUMNS data dictionary views


<a name="v16.8.1"></a>
## Release 16.8.1 - 2020-05-30

- Remove metadata_all as a metadata retrieval strategy
- Split SchemaCrawlerOptions into LimitOptions, FilterOptions, GrepOptions and LoadOptions to match the command-line


<a name="v16.7.2"></a>
## Release 16.7.2 - 2020-05-06

- Create privilege grants even if the grantor or grantee is null


<a name="v16.7.1"></a>
## Release 16.7.1 - 2020-04-30

- Model primary keys as table constraints instead of indexes
- Introduce architecture constraints, and reorganize classes in packages to avoid cyclic dependencies


<a name="v16.6.1"></a>
## Release 16.6.1 - 2020-04-10

- Make analysis of weak associations part of the maximum info-level retrieval
- Add new command-line option, --load-row-counts, to load table row counts


<a name="v16.5.3"></a>
## Release 16.5.3 - 2020-03-24

- Redistribute Oracle and IBM DB2 JDBC drivers with SchemaCrawler
- Provide docker-compose scripts for testing with databases
- Better command-line to create test schemas for various databases
- Better error messages when diagrams cannot be generated
- Redistribute Microsoft SQL Server integrated security companion DLL to allow for Windows authentication


<a name="v16.5.2"></a>
## Release 16.5.2 - 2020-02-20

- Throw exception if the SchemaCrawler database plugin is not on the CLASSPATH for major database systems
- Rename the integrations module to diagram


<a name="v16.4.1"></a>
## Release 16.4.1 - 2020-02-06

- Support enum values for MySQL and PostgreSQL as first-class schema metadata
- Add MariaDB test cases


<a name="v16.3.2"></a>
## Release 16.3.2 - 2020-01-15

- Re-issue of release


<a name="v16.3.1"></a>
## Release 16.3.1 - 2020-01-12

- Fix bug with generating titles in graphs (issue #281)


<a name="v16.3.0"></a>
## Release 16.3.0 - 2020-01-02

- Remove ability to run SQL commands directly from command-line - only named
  queries are supported


<a name="v16.2.7"></a>
## Release 16.2.7 - 2019-12-03

- Update documentation on website
- Change copyright notices


<a name="v16.2.6"></a>
## Release 16.2.6 - 2019-11-22

- Use `--output-format`for specifying serialization output formats


<a name="v16.2.5"></a>
## Release 16.2.5 - 2019-11-17

- Support JSON and YAML output formats for lint reports


<a name="v16.2.4"></a>
## Release 16.2.4 - 2019-11-05

- Add support for IBM DB2 materialized query tables (MQT)


<a name="v16.2.4"></a>
## Release 16.2.4 - 2019-10-26

- Really fix issue with passing parameters into shell commands


<a name="v16.2.3"></a>
## Release 16.2.3 - 2019-10-21

- Fix issue with passing parameters into shell commands


<a name="v16.2.2"></a>
## Release 16.2.2 - 2019-10-20

- Remove ability to generate JSON as an output format, since it has been replaced with
  the ability to generate JSON as a serialization format (using the `serialize` command,
  with a lot more schema information
- Workaround MySQL JDBC driver issue with retrieving table comments
  (<https://bugs.mysql.com/bug.php?id=80473>) by allowing table comments to be obtained
  from `table.getAttribute("TABLE_COMMENT")`

<a name="v16.2.1"></a>
## Release 16.2.1 - 2019-10-14

- Perform tests for major databases during the CI build using Testcontainers
- Add support for JSON and YAML output using Jackson
  *NOTE*: JSON format for output is deprecated, and will be removed in the next release


<a name="v16.1.2"></a>
## Release 16.1.2 - 2019-08-11

- Better help text, including shell command names in the help text
- Distributed with PostgreSQL driver 42.2.6 which reports function columns
- Reporting of additional table information for PostgreSQL


<a name="v16.1.1"></a>
## Release 16.1.1 - 2019-07-25

- Use semantic versioning
- Use POSIX compliant command-line arguments
- Consolidate code for command-line and shell using picocli 4.0.1
- Update the help system for plugins using picocli
- Rename routine columns to routine parameters consistently across the API and command-line
- Move Maven archetypes into GitHub example projects
- Move Docker build back into the main project


<a name="v15.06.01"></a>
## Release 15.06.01 - 2019-03-10

- Separate SchemaCrawler scripting and templating jar from the main jar


<a name="v15.05.01"></a>
## Release 15.05.01 - 2019-03-09

- Add option to run only configured linters, `-runalllinters=false`
- Separate SchemaCrawler commandline jar from the main jar in preparation for enhancements


<a name="v15.04.01"></a>
## Release 15.04.01 - 2019-02-02

- Add server information for MySQL
- Convert tests to JUnit 5
- Remove input resource from output options, and let each command fend for itself
- Added integrations with Mustache for templated output
- Use JUnit 5 in archetype generated code
- Use plain Java serialization for offline database


<a name="v15.03.04"></a>
## Release 15.03.04 - 2018-12-29

- Fix documentation on how to connect to Oracle
- Fix query for Oracle system information


<a name="v15.03.03"></a>
## Release 15.03.03 - 2018-12-28

- Remove Spring Framework command-line support
- Allow fine tuning of retrieval using configuration properties values for schema info level


<a name="v15.03.02"></a>
## Release 15.03.02 - 2018-12-12

- Fix issue with serialization of offline snapshots


<a name="v15.03.01"></a>
## Release 15.03.01 - 2018-12-10

- Change copyright messages
- Add support for PostgreSQL and Oracle materialized views
- Add support for Microsoft SQL Server indexed views


<a name="v15.02.02"></a>
## Release 15.02.02 - 2018-11-25

- Add example to demonstrate how to load PostgreSQL dumps


<a name="v15.02.01"></a>
## Release 15.02.01 - 2018-11-23

- Add support for server information in schemacrawler.schema.DatabaseInfo.getServerInfo()


<a name="v15.01.06"></a>
## Release 15.01.06 - 2018-11-10

- Fix issue with SchemaCrawler Shell connecting to databases not on localhost
- Support PGHOST, PGHOSTADDR, PGPORT, PGDATABASE environmental variables for PostgreSQL connections


<a name="v15.07.01"></a>
## Release 15.07.01 - 2018-10-14

- Add SchemaCrawler Shell example to the main distribution


<a name="v15.01.04"></a>
## Release 15.01.04 - 2018-10-14

- Create a unique run id for each SchemaCrawler run
- Rename -hideemptytables to -noemptytables
- Allow for additional data plugins to be downloaded with a script
- Reorganize command-line help to be consistent with SchemaCrawler Shell


<a name="v15.01.03"></a>
## Release 15.01.03 - 2018-09-21

- Drop support for OSGi bundles
- Drop support for CSV and TSV output
- Use [FST](https://github.com/RuedigerMoeller/fast-serialization) for catalog serialization and deserialization instead of XStream


<a name="v15.01.02"></a>
## Release 15.01.02 - 2018-09-17

- Fix Maven archetypes


<a name="v15.01.01"></a>
## Release 15.01.01 - 2018-07-10

- Output to files by default, except for text output format
- Make executable work in two phases, to load schema, and then to run a command
- Allow SchemaCrawler to work with Oracle users that have just the SELECT_CATALOG_ROLE


<a name="v14.21.03"></a>
## Release 14.21.03 - 2018-06-10

- Add tests for embedded MonetDB


<a name="v14.21.02"></a>
## Release 14.21.02 - 2018-05-21

- Add support for java.sql.Types REF_CURSOR, TIME_WITH_TIMEZONE, and TIMESTAMP_WITH_TIMEZONE
- Fixed issue with lint reporting "unique index with nullable columns" on computed columns

<a name="v14.21.01"></a>
## Release 14.21.01 - 2018-05-14

- Fixed issue #184 with with support for older Oracle versions in the SchemaCrawler Oracle plugin
- Changed API for SchemaCrawler plugins

<a name="v14.20.06"></a>
## Release 14.20.06 - 2018-05-02

- Fixed issue #181 with more control (and configuration) over graph generation

<a name="v14.20.05"></a>
## Release 14.20.05 - 2018-04-19

- Fixed issue #178 with JSON data generation

<a name="v14.20.04"></a>
## Release 14.20.04 - 2018-04-04

- Docker image updated with non-root user, and documentation updated for latest Docker version

<a name="v14.20.03"></a>
## Release 14.20.03 - 2018-03-10

- Website updates

<a name="v14.20.02"></a>
## Release 14.20.02 - 2018-03-07

* Upgrade to support Apache Velocity 2.0.
* Added remarks for tables and columns to Microsoft SQL Server test schema.

<a name="v14.20.01"></a>
## Release 14.20.01 - 2018-03-05

* Make -routines return no routines by default, to make it easier for first-time users.
* Support a pure Java implementation of Graphviz (nidi3/graphviz-java), for cases where Graphviz cannot be installed.
* Changed CommandProvider API

<a name="v14.19.01"></a>
## Release 14.19.01 - 2018-02-04

* Fixing bug when retrieving Oracle procedure metadata
* Adding test support for routines
* Adding test code for retrieving result set metadata

<a name="v14.18.01"></a>
## Release 14.18.01 - 2018-01-10

* Ignore MariaDb driver for MySQL if both are on the classpath.
* Verify support for unnamed primary keys and foreign keys.
* Support unnamed foreign keys.
* System to build reference schema for most common databases.
* Tests for unusual data types, user-defined data types, domains and temporary tables.
* Support for generating SQLite database from scripts.
* Added tests for ascending and descending columns in indexes.
* Getting MySQL plugin to support MariaDB as well
* Consistent treatment for hidden and generated table columns, with special marking in details output
* Support for Oracle 12c identity or auto-incremented columns

<a name="v14.17.05"></a>
## Release 14.17.05 - 2017-12-28

* Website updates, including link to the SchemaCrawler Web Application.

<a name="v14.17.04"></a>
## Release 14.17.04 - 2017-12-10

* Removing index unquoting work-around for old versions of the SQLite JDBC driver, and distributing with the latest driver.
* Making SQL type map a database specific property, which can be overridden by database plugins.
* Moving website to GitHub pages.

## Release 14.17.03 - 2017-11-21
<a name="v14.17.03"></a>

* Fixing a regression with SQLite databases created with double-quoted identifiers.

## Release 14.17.02 - 2017-11-20
<a name="v14.17.02"></a>

* Fix for broken API call in Maven archetype.
* Update to Apache Velocity example to quote database object names.

## Release 14.17.01 - 2017-11-19
<a name="v14.17.01"></a>

* Database objects getName() returns unquoted names, and getFullName() returns a name quoted using default quoting rules. Text output is controlled by the schemacrawler.format.identifier_quoting_strategy configuration property.
* Support for SQLite named foreign keys, auto-incremented columns, and more, with the latest JDBC driver for SQLite.
* Exclude database system schemas by default.
* Make -infolevel an optional command-line argument, and default to standard.
* Make -routines return no routines by default, to make it easier for first-time users.
* Show just SchemaCrawler information by default in the output, and not database and JDBC driver information. Add fine-tuned options for each.
* Adding some security lock-downs to XStream serialization.

## Release 14.16.04 - 2017-09-25
<a name="v14.16.04"></a>

* Updating dependencies.
* Bug-fix for Oracle routines.

## Release 14.16.03 - 2017-07-08
<a name="v14.16.03"></a>

* Updating dependencies.

## Release 14.16.02 - 2017-06-20
<a name="v14.16.02"></a>

* Bug fix for an issue with retrieving SQLite primary keys if the DDL used quoted names.
* Moved Sybase IQ plugin to new GitHub project.

## Release 14.16.01 - 2017-04-10
<a name="v14.16.01"></a>

* Better protection of user credentials in memory.
* Support for index remarks.
* Organize metadata views for extensibility. Please refer to <https://www.schemacrawler.com/data-dictionary-extensions.html>
* Remove reliance on Java 8 features where they add an overhaead.

## Release 14.15.04 - 2017-04-02
<a name="v14.15.04"></a>

* Added foreign key retrieval strategies, and exposed overrides in configuration properties.

## Release 14.15.03 - 2017-03-28
<a name="v14.15.03"></a>

* Added table retrieval strategies, and exposed overrides in configuration properties.

## Release 14.15.02 - 2017-03-23
<a name="v14.15.02"></a>

* Clarified licensing terms.

## Release 14.15.01 - 2017-03-21
<a name="v14.15.01"></a>

* Fixed licenses published in jar files and on Maven Central.
* Cleaner description of column data types.

## Release 14.14.04 - 2017-03-11
<a name="v14.14.04"></a>

* Better and more efficient logging and error messages.
* Added short forms, -fmt for -outputformat command-line option, and -i for -infolevel.
* Made the output format command-line option optional. The output format is derived from the output file extension if it is not explicitly provided.

## Release 14.14.03 - 2017-02-23
<a name="v14.14.03"></a>

* Java utility class to parse enum values from column.getAttribute("COLUMN_TYPE"), for MySQL.
* Better error message for bad commands.

## Release 14.14.02 - 2017-02-08
<a name="v14.14.02"></a>

* To get the enum values from column.getAttribute("COLUMN_TYPE"), run SchemaCrawler for MySQL with -infolevel=maximum.
* New SchemaCrawler for SQLite utility, schemacrawler.tools.sqlite.SchemaCrawlerSQLiteUtility.

## Release 14.14.01 - 2017-01-11
<a name="v14.14.01"></a>

* The graph command is removed. Graphs are generated based on the output format.

## Release 14.12.01 - 2016.15.34
<a name="v14.12.01"></a>

* New command-line option to show weak associations, irrespective of the infolevel.
* Changed the boolean property, schemacrawler.format.hide_weak_associations to schemacrawler.format.show_weak_associations, since weak associations are hidden by default in SchemaCrawler output. Also added a new command-line switch, -weakassociations to show weak associations. Weak associations now can be shown with any infolevel setting.

## Release 14.11.02 - 2016.15.30
<a name="v14.11.02"></a>

* Support for the Teradata JDBC driver. The Teradata JDBC driver does not follow JDBC specifications. It should throw a SQLFeatureNotSupportedException for getFunctions, or at the least a SQLException state HYC00 ("Optional feature not implemented"). Instead, it throws with a state of HY000 ("General error").

## Release 14.11.01 - 2016.15.36
<a name="v14.11.01"></a>

* Show a better error message when a script file cannot be read.
* Handle SQLException when retrieving JDBC driver information.
* Extended copyright messages through 2017.

## Release 14.10.06 - 2016-10-29
<a name="v14.10.06"></a>

* Made OfflineSnapshotExecutable constructor public, so that offline snapshots can be loaded programatically.

## Release 14.10.05 - 2016-10-26
<a name="v14.10.05"></a>

* Fixed issue with generating SQLite diagrams.
* Distribution now contains an example SQLite database.
* Fail if the SQLite database does not exist.

## Release 14.10.04 - 2016-10-23
<a name="v14.10.04"></a>

* Fixed issue with loading of JDBC drivers.

## Release 14.10.03 - 2016-10-01
<a name="v14.10.03"></a>

* Fixed formatting of multi-line column remarks in HTML output.

## Release 14.10.02 - 2016-09-17
<a name="v14.10.02"></a>

* New lint to recommend that a surrogate key column be used as a primary key.
* Fixed lint that checks foreign key and primary key data types.
* Additional documentation on how to connect to Microsoft SQL Server with Windows authentication.

## Release 14.10.01 - 2016-08-31
<a name="v14.10.01"></a>

* Support for newlines in table and column comments.
* Ability to read default configuration settings from the classpath, from a file called schemacrawler.config.properties. This file is distributed in the lib/ folder.
* Removed command-line option -p to specify an additional configuration file.
* Added documentation on how to do programmatic diff of schemas on the website. Added code examples to the website.

## Release 14.09.03 - 2016-08-01
<a name="v14.09.03"></a>

* A new configuration options `schemacrawler.format.no_schema_colors` if you do not want to show catalog and schema colors.

## Release 14.09.02 - 2016-07-14
<a name="v14.09.02"></a>

* Allow primary keys to be retrieved in bulk, to speed up Oracle execution.

## Release 14.09.01 - 2016-06-29
<a name="v14.09.01"></a>

* The MariaDB driver interferes with the use of the MySQL driver, so it is no longer distributed.
* Fixing null pointer exception for table types, by introducing an unknown table type.

## Release 14.08.02 - 2016-05-16
<a name="v14.08.02"></a>

* Adding hidden columns in a separate list, accessed with a new method, Table.getHiddenColuns().
* Allow table constraint definitions to be obtained separately from table constraint information.

## Release 14.08.01 - 2016-05-04
<a name="v14.08.01"></a>

* Improved performance with Oracle, by substituting schemas.

## Release 14.07.08 - 2016-04-14
<a name="v14.07.08"></a>

* Removing linter for surrogate keys, since it is of dubious value.

## Release 14.07.07 - 2016-04-08
<a name="v14.07.07"></a>

* Added a new boolean property, schemacrawler.format.hide_weak_associations, to hide weak associations in SchemaCrawler output.

## Release 14.07.06 - 2016-03-26
<a name="v14.07.06"></a>

* Making SchemaCrawler available under the Eclipse Public License, in addition to the GPL and LGPL.
* Fixing ability to run SQL from the command-line.

## Release 14.07.05 - 2016-03-19
<a name="v14.07.05"></a>

* More detailed examples of how to use the SchemaCrawler API from script.
* Fixing a bug with the default linter threshold.

## Release 14.07.04 - 2016-03-16
<a name="v14.07.04"></a>

* Lint enhancements to dispatch.

## Release 14.07.03 - 2016-03-10
<a name="v14.07.03"></a>

* Add details of lints that were dispatched, before the dispatch takes place.

## Release 14.07.02 - 2016-02-27
<a name="v14.07.02"></a>

* Support for Compact Profile 2.
* Adding dispatch strategies for lint. This allows the SchemaCrawler command-line to fail with an exit code from the command-line when there are too many lints.

## Release 14.06.05 - 2016-02-14
<a name="v14.06.05"></a>

* Bug fix for bug while retrieving metadata.

## Release 14.06.04 - 2016-02-14
<a name="v14.06.04"></a>

* Complete support for Oracle hidden columns. Hidden columns are indicated with Table.isHidden(). Similarly, generated columns (including Oracle function-based columns) are indicated with Table.isGenerated(). You can get a complete list of all table columns, including hidden columns, with Table.getColumns(), if you have used the "maximum" schema info-level.

## Release 14.06.03 - 2016-02-10
<a name="v14.06.03"></a>

* Support for Oracle function-based indexes.

## Release 14.06.02 - 2016-02-10
<a name="v14.06.02"></a>

* Distributing correct versions of JDBC driver jars.
* Performance boost by using Oracle data dictionary query for table indexes. Idea courtesy of Patric Rufflar.

## Release 14.06.01 - 2016-02-08
<a name="v14.06.01"></a>

* Added table row counts in output and diagrams. Use schemacrawler.format.show_row_counts=true in the configuration file.
* Significant performance improvements in Java code, especially related to logging. Idea courtesy of Patric Rufflar.
* Performance boost by using Oracle data dictionary query for table columns. Idea courtesy of Patric Rufflar.

## Release 14.10.06 - 2016-02-03
<a name="v14.10.06"></a>

* Fixed logging for features that the JDBC driver does not support.
* Added utility to filter tables based on "raw" name.
* Updating JDBC drivers and dependencies.

## Release 14.05.04 - 2015-12-03
<a name="v14.05.04"></a>

* Fixed weak references, and logging for offline databases.

## Release 14.05.03 - 2015-12-02
<a name="v14.05.03"></a>

* Added new quickdump command, which works with -infolevel=minimum, though row order is not guaranteed.

## Release 14.05.02 - 2015-11-26
<a name="v14.05.02"></a>

* Externalized logic to quote and unquote database object identifiers.

## Release 14.05.01 - 2015-11-14
<a name="v14.05.01"></a>

* Fix default log level.
* Fix who uses SchemaCrawler list.
* Fix database plugin archetype.
* Sealing internal packages.

## Release 14.04.04 - 2015-10-31
<a name="v14.04.04"></a>

* Fix for retrieving PostgreSQL triggers.
* More efficient table column retrieval in IBM DB2.
* Added tests for unique constraints, and user-defined data types.
* Fixed output for base data-types.

## Release 14.04.03 - 2015-10-19
<a name="v14.04.03"></a>

* Overrides to foreign key retrieval, for more efficient IBM DB2 crawling.

## Release 14.04.02 - 2015-10-11
<a name="v14.04.02"></a>

* Detailed timing information in logs.

## Release 14.04.01 - 2015-10-11
<a name="v14.04.01"></a>

* Fixing broken -urlx switch.
* Reworked command-line processing, and database plugins.
* Removed Apache Derby database plugin. Apache Derby is now supported using a database URL only.
* Added MariaDB database plugin.

## Release 14.03.03 - 2015-09-20
<a name="v14.03.03"></a>

* An attempt at faster retrieval of foreign keys in Oracle.

## Release 14.03.02 - 2015-09-17
<a name="v14.03.02"></a>

* Better excludes for system schemas in Oracle.
* Added ability to report on tables causing a cycle in a lint, using Tarjan's algorithm.

## Release 14.03.01 - 2015-08-17
<a name="v14.03.01"></a>

* Added detailed lint help, which is available from the command-line, using -help -command=lint.
* Added configuration for LinterTooManyLobs.
* Added new lint for specifying a list of reserved words for column names.
* Added new lints for empty tables, tables where all columns are nullable, and tables with no primary keys.
* New SchemaCrawlerUtility methods to get database system specific functionality.
* Renaming CrawlHeaderInfo to CrawlInfo, and making it available in linters.
* Fixed issues with sorting tables and routines alphabetically. Sorting alphabetically also sorts by object type.

## Release 14.02.02 - 2015-08-14
<a name="v14.02.02"></a>

* Added new lints for empty tables, tables where all columns are nullable, and tables with no primary keys.

## Release 14.02.01 - 2015-08-10
<a name="v14.02.01"></a>

* Added a custom SQL Lint, to allow for data lints.
* New lint command-line option, -linterconfigs=<path> to specify the location of the linter XML configuration file.
* Ability to specify table inclusion rules for lints.

## Release 14.01.02 - 2015-07-28
<a name="v14.01.02"></a>

* Bug fix for connecting to a database using a URL.

## Release 14.01.01 - 2015-07-16
<a name="v14.01.01"></a>

* SchemaCrawler is now supported on Java SE 8 or better only.
* The SchemaCrawler project has been moved to GitHub.
* Database specific overrides are not not in SchemaCrawlerOptions, and can be passed in along with the database connection if needed.
* SchemaCrawler has a new project to demonstrate how to programatically diff schemas.
* New -title command-line argument to show a custom title on output and diagrams.
* Methods to get database objects by name are renamed to lookup, and return Optional. This allows calling code to handle nulls.
* Sequences are color-coded by schema like the rest of the database objects.
* Rename indices to indexes consistently in method names.
* All references to parent objects are soft references, to allow for more efficient memory management and serialization.
* All references to parent objects are soft references, to allow for more efficient memory management and serialization.
* Shell scripts sc.sh and sc.cmd renamed to schemacrawler.sh and schemacrawler.cmd, since they clash with system utilities.

## Release 12.06.03 - 2015-03-27
<a name="v12.06.03"></a>

* Showing cardinality for tables that are filtered out.

## Release 12.06.02 - 2015-03-26
<a name="v12.06.02"></a>

* Fixing links on website.
* Fixing diagram to show hanging arrows for referenced tables.

## Release 12.06.01 - 2015-03-24
<a name="v12.06.01"></a>

* Hyperlinks in HTML output from foreign keys to tables.
* Lighter colors for tables, consistent in both HTML and graph output. Colors are assigned per catalog and schema.
* Foreign key cardinality in HTML and text output.
* New command-line option to remove empty tables (with no rows of data) from output -hideemptytables
* Not showing database password in command-line logs.
* Showing database server and JDBC driver in logs.

## Release 12.05.02 - 2015-03-17
<a name="v12.05.02"></a>

* Better identification of weak associations.
* Showing routine parameter names in JSON.

## Release 12.05.01 - 2015-03-14
<a name="v12.05.01"></a>

* Better identification of weak associations.

## Release 12.04.02 - 2015-02-10
<a name="v12.04.02"></a>

* Fix bug with connecting to Oracle.

## Release 12.04.01 - 2015-01-18
<a name="v12.04.01"></a>

* Reverting back to SQLite JDBC driver version 3.7.8, since that is the most stable, even if not readily available.
* Refactored API for reading input files, such as scripts.
* Made compressed file format the default for offline snapshots.

## Release 12.03.02 - 2015-01-18
<a name="v12.03.02"></a>

* Removed the -driver command-line option, since most JDBC drivers will register themselves automatically.
* Made sure that a JDBC driver for a database server is available, before the -server command-line option is valid for that server.

## Release 12.03.01 - 2015-01-02
<a name="v12.03.01"></a>

* Added -noremarks command-line option to hide table and column remarks (or comments).
* Using a stable version of the Xerial SQLite JDBC driver that is readily available on Maven Central. This does not support retrieving primary key names.
* Removed built-in support for ant tasks, since there is not way to omit an option if one is not specified.
* Changed copyright to 2015.
* New parser for command-line options.

## Release 12.02.03 - 2014-12-25
<a name="v12.02.03"></a>

* Integration with Thymeleaf templating.
* Flushing of output.

## Release 12.02.02 - 2014-12-24
<a name="v12.02.02"></a>

* Better logging of execution.
* Quicker execution when routines are not output.

## Release 12.02.01 - 2014-12-21
<a name="v12.02.01"></a>

* New output format, htmlx, which embeds database diagrams into an HTML file. The diagrams are in SVG format.
* Converted all HTML output to HTML5.
* Null-checks now throw NullPointerException, instead of IllegalArgumentException.
* Tested support of international character sets in table names and column names.
* Converting filter classes to Predicate, to prepare for Java 8 support.
* Converted all file operations to use Java Path, for better error handling.

## Release 12.01.01 - 2014-11-27
<a name="v12.01.01"></a>

* Collapsed distribution into a single distribution download, with Apache Ant Ivy scripts for downloading additional dependencies. A new -server command-line option will connect to a database with built-in support.
* Added Maven archetypes for database server plugins.
* Building Debian package as part of the main distributable.
* Added support for the H2 Database Engine.
* Changes direction of arrow for foreign keys in text output, to always point to the primary key.
* Made consistent CSS stylesheets for examples, website, and HTML text output.
* Fixed issues with retrieving functions from H2 Database Engine.
* Standardized CSS styles between the website, example readmes, and SchemaCrawler HTML output.

## Release 11.02.01 - 2014-11-04
<a name="v11.02.01"></a>

* Added options to hide cardinality in graphs.
* Added Maven archetypes for plugins.

## Release 11.01.01 - 2014-09-21
<a name="v11.01.01"></a>

* Changed Maven groupId to us.fatehi.
* Renamed Database class to Catalog.
* Changed all output formats to include table and column remarks whenever available.
* Added command-line option options to show significant columns, such as primary and foreign key columns, and columns that are part of unique indexes. Use the `-infolevel=standard -command=brief` command-line options.
* Indicate auto-incremented columns in graph and other output.

## Release 10.10.05 - 2014-08-04
<a name="v10.10.05"></a>

* Fixed bug with weak associations in diagrams being drawn twice.
* Added more variations of diagrams to website.

## Release 10.10.04 - 2014-07-27
<a name="v10.10.04"></a>

* Fixed bug with cardinality in diagrams, when a foreign key is only a part of the primary key of a table.
* Fixed -sequences command-line argument, and got it to work correctly with the properties file.

## Release 10.10.03 - 2014-07-06
<a name="v10.10.03"></a>

* Added support for include and exclude expressions for synonyms and sequences in config properties files.

## Release 10.10.02 - 2014-07-02
<a name="v10.10.02"></a>

* Added support for auto-incremented columns, and generated columns.

## Release 10.10.01 - 2014-06-22
<a name="v10.10.01"></a>

* Added support for sequences.

## Release 10.09.01 - 2014-05-14
<a name="v10.09.01"></a>

* Fixed table type to be based on a string, not an enum.
* Changed the object inheritance of a privilege, so that it is not a database object.

## Release 10.08.05 - 2014-04-25
<a name="v10.08.05"></a>

* Fixed lint "foreign key with no index" to look at primary keys as well.
* Fixed bug with loading XML databases.
* Fixed MySQL connection issue, by removing URL parameter "useInformationSchema=true" so that MySQL's buggy metadata provider is not used. This provider does not honor mixed-case tables names.

## Release 10.08.04 - 2014-04-03
<a name="v10.08.04"></a>

* Column sort options sort columns in dumps also.
* Added generation date to database diagram.

## Release 10.08.03 - 2014-03-24
<a name="v10.08.03"></a>

* Made extra columns provided in all information schema views available as attributes of SchemaCrawler Java objects.
* Added table definition metadata for Oracle.
* Allow for code to be executed before and after the crawl. This is in the form of pre- and post- executables.
* Added foreign key definition metadata for Oracle.
* Fixed DDL metadata for Oracle, so that it is portable.
* Converted code to Git.

## Release 10.08.02 - 2013-12-28
<a name="v10.08.02"></a>

* Updated copyright notices.
* Fixed javadocs, using standard Java 7 stylesheet.

## Release 10.08.01 - 2013-11-17
<a name="v10.08.01"></a>

* Trim all table names, to allow Informix to match correctly.

## Release 10.8 - 2013-11-10
<a name="v10.8"></a>

* Providing richer information about table constraints, including details of constrained columns.

## Release 10.7 - 2013-11-05
<a name="v10.7"></a>

* Providing richer information about table constraints. Renaming check constraints to table constraints.
* Worked abound ArrayIndexOutOfBoundsException in MySQL connector with connector 5.1.26 and server version 5.0.95.

## Release 10.6 - 2013-10-30
<a name="v10.6"></a>

* Changed InclusionRule to an interface, to allow custom implementations.
* Fixed lack of support for type maps by the Sybase IQ database driver.

## Release 10.5 - 2013-09-24
<a name="v10.5"></a>

* Fixed bug with table restrictions.
* Support all command-line options in ant task.

## Release 10.4 - 2013-07-24
<a name="v10.4"></a>

* Fixed view definitions for PostgreSQL.
* Fixed retrieving index metadata for tables, working around PostgreSQL bugs #3480 and #6253.
* Added retrieving index definitions for PostgreSQL and Oracle.
* Added retrieving view definitions for Apache Derby.

## Release 10.3 - 2013-05-24
<a name="v10.3"></a>

* Rework of the column data type classes, as well as a workaround for an Oracle JDBC driver bug with reporting data type information.

## Release 10.2 - 2013-05-21
<a name="v10.2"></a>

* New option to set output encoding format. The option is "schemacrawler.encoding.output" in the SchemaCrawler properties file. Default input and output encoding has been changed to UTF-8.
* Support for showing trigger information in Sqlite.

## Release 10.1 - 2013-04-21
<a name="v10.1"></a>

* SchemaCrawler support for J2SE 7 only, since previous Java versions are deprecated. Converted the codebase over to use J2SE 7 constructs.
* New command-line option, -only-matching, to show only matching tables, and does not show foreign keys that reference other non-matching tables. Modeled after the analogous grep option.
* New option to set input encoding format for script files and templates. The option is "schemacrawler.encoding.input" in the SchemaCrawler properties file.
* Renamed command-line option -table_types to -tabletypes.

## Release 9.6 - 2013-03-08
<a name="v9.6"></a>

* Fixed diagram generation on Linux when no GraphViz options are supplied.
* Added trigger names to JSON output by default. Added new configuration variable, schemacrawler.format.hide_trigger_names to suppress trigger names in all output formats.
* Added new -portablenames command-line option, to allow for easy comparison between databases, by hiding foreign key names, constraint names, trigger names, index and primary key names, and not showing the fully-qualified table name.

## Release 9.5 - 2013-02-24
<a name="v9.5"></a>

* Updated HTML generation to have captions consistently for all tables. Numeric data is right-aligned in HTML, and correctly reported in JSON as well. These changes affect other output formats as well.
* Updated Oracle database connection URL to the new syntax.

## Release 9.4 - 2013-02-02
<a name="v9.4"></a>

* Fix for honoring text formatting options in GraphViz dot output.

## Release 9.3.2 - 2012-11-17
<a name="v9.3.2"></a>

* Fix for honoring sort options on JSON output.

## Release 9.3.1 - 2012-11-3
<a name="v9.3.1"></a>

* Fix for generating diagrams on Unix.

## Release 9.3 - 2012-10-31
<a name="v9.3"></a>

* Weak associations are returned sorted.
* Added ability to specify additional command-line options for GraphViz, using either Java system properties, or environmental variables.

## Release 9.2 - 2012-10-21
<a name="v9.2"></a>

* Fixed Windows script, schemacrawler.cmd.
* Better support for sqlite, including the latest database driver, reporting of foreign keys, and view definitions.
* Updated the jTDS JDBC driver to 1.2.6, for Microsoft SQL Server.
* Deprecated the getType() method on database objects.

## Release 9.1 - 2012-09-30
<a name="v9.1"></a>

* Added examples for Groovy, Ruby and Python scripting.
* SchemaCrawler is now bundled as an OSGi jar.

## Release 9.0 - 2012-09-08
<a name="v9.0"></a>

* The API has changed from 8.x versions. All API calls return collections instead of arrays. Schemas are not containers, but tags for database objects. Tables and routines (stored procedures and functions) are available directly on the database, and there are new finders to search by schema.
* Support for database functions has been added. All references to stored procedures in the code and documentation now refer to routines. Stored procedures and functions are treated in the same way, as routines.
* SchemaCrawler now allows queries to be specified on the command-line. If a command is not recognized, or is not a named query, it is executed as if it is a query.
* The output format includes trigger definitions as part of the schema, instead of as the detailed schema output.
* Database connections have to be specified on the command-line, by providing the driver class name and URL. Database connections can no longer be defined in properties files.
* There are new command-line options. -V, --version will print the SchemaCrawler version and exit. There are shorter and more standard (Unix-like) synonyms -u (for -user), -c (for -command), and -o (for -output). The -procedures switch has been renamed -routines.

## Release 8.17 - 2012-07-12
<a name="v8.17"></a>

* The HTML output format outputs valid HTML5 and CSS3, as valid XML.
* JSON output supports all of the formatting options that the other text formats support.
* A new method, getShortName(), get the unqualified name for columns and other dependent objects.
* A new text format option, "schemacrawler.format.show_unqualified_names", shows the unqualified name in text output, for easier comparisons across different catalogs and schemas.

## Release 8.16 - 2012-06-20
<a name="v8.16"></a>

* Added support for synonyms. Synonyms are shown in SchemaCrawler output for IBM DB2, Oracle, and Microsoft SQL Server, as well as available for extension in other databases.

## Release 8.15 - 2012-05-05
<a name="v8.15"></a>

* Added distribution for Sybase IQ.

## Release 8.14 - 2012-04-08
<a name="v8.14"></a>

* Added new command-line switch to show parent and child tables, in addition to those selected by grep.

## Release 8.12 - 2012-02-25
<a name="v8.12"></a>

* Added ability to stream SchemaCrawler output.

## Release 8.11 - 2012-02-18
<a name="v8.11"></a>

* Added ability to configure SchemaCrawler linters.
* Added a new lint for useless surrogate keys.
* Added ability to tag tables and columns with additional metadata attributes from SQL queries.

## Release 8.10 - 2011-12-08
<a name="v8.10"></a>

* Added SchemaCrawler Lint as a separate report, with ability to add custom linters.
* Added additional SchemaCrawler linters.
* Added ability to extend SchemaCrawler with custom command.
* Fixed issue with constraints with the same name in different schemas.

## Release 8.9 - 2011-12-08
<a name="v8.9"></a>

* Added SchemaCrawler Lint as a separate report, with ability to add custom linters.
* Added additional SchemaCrawler linters.
* Added ability to extend SchemaCrawler with custom command.
* Fixed issue with constraints with the same name in different schemas.

## Release 8.8 - 2011-11-05
<a name="v8.8"></a>

* Added JSON and TSV output formats.
* Moved SchemaCrawler Maven Plugin into a new project.

## Release 8.7 - 2011-09-05
<a name="v8.7"></a>

* New INFORMATION_SCHEMA views to provide Oracle metadata details.
* Update database driver versions.
* Fixed bug #3392557 - NullPointer exception thrown when columns excluded in Oracle.
* Fixed bug #3392976 - Exception retrieving tables: Could not retrieve indexes for Oracle schemas with $ in the name.
* Fixed bug #3401752 - Attempt to locate database 'dbo' by name failed.

## Release 8.6 - 2011-05-08
<a name="v8.6"></a>

* New feature to chain, or run multiple SchemaCrawler commands using JavaScript.

## Release 8.5.1 - 2011-01-28
<a name="v8.5.1"></a>

* Re-organized command-line code, including grep command-line.

## Release 8.5 - 2011-01-28
<a name="v8.5"></a>

* Changed copyright to 2011.
* Fixed grep.
* Removed option to provide index info SQL.
* Better unit test coverage. Restructuring of Eclipse projects.

## Release 8.4 - 2010-12-23
<a name="v8.4"></a>

* Simplified commands - list_objects is now simply list. Removed the -show_stored_procedures command-line option - simply use -proedures=
* Fixed issue with obtaining PostgreSQL tables.
* Fixed issue with obtaining Derby foreign keys.
* Made -infolevel a required command-line argument.
* Use Maven 3.0 in the build.

## Release 8.3.3 - 2010-08-01
<a name="v8.3.3"></a>

* Added color to headings in HTML output.
* Fixed bug when remarks header was not shown if the table did not have remarks, but columns did.
* DB2 INFORMATION_SCHCEMA SQL fixed.
* Foreign keys show full column names in graphs, even when the table is excluded.

## Release 8.3.2 - 2010-08-01
<a name="v8.3.2"></a>

* Column remarks are printed along with table remarks.
* Foreign keys show full column names in graphs, even when the table is excluded.

## Release 8.3.1 - 2010-07-18
<a name="v8.3.1"></a>

* Added new command-line option, -urlx, to pass JDBC URL properties to bundled distributions.
* Added details on the "main" command-line in the how-to section of the website.

## Release 8.3 - 2010-07-15
<a name="v8.3"></a>

* Added new bundled distribution for IBM DB2.
* Added new bundled distribution for HyperSQL 2.0.0.
* Added support for spaces in schema and catalog names.
* Changed the test database to HyperSQL 2.0.0\. Changed the test data, and added more robust unit tests.

## Release 8.2 - 2010-05-25
<a name="v8.2"></a>

* Added support for spaces or reserved words in table, procedure, and column names.
* Added new database lint to find column and table names with spaces or reserved words in them.
* Added SchemaCrawler to the Sonatype Maven public repository. Added new Maven projects examples.
* Table and procedure remarks are output with verbose output.
* The Maven plug-in generates a database report that uses the same stylesheet as the rest of the Maven generated website.
* Generated database diagrams are less "colorful".

## Release 8.1 - 2010-02-28
<a name="v8.1"></a>

* Added SchemaCrawler lint, to highlight potential normalization issues, such as incrementing column names, and other issues. A new -infolevel value, lint, was added.
* Weak associations are now available only with SchemaCrawler lint.

## Release 8.0 - 2010-02-03
<a name="v8.0"></a>

* Added the -infolevel command-line switch to control the amount of database metadata retrieved by SchemaCrawler.
* Create a command registry, which allows pluggable commands. Any command can be run from the main class.
* Dropped the -catalogs command-line switch filtering catalogs. Dropped the concept of a catalog from the API. Databases now directly contain schemas.

## Release 7.6 - 2009-12-03
<a name="v7.6"></a>

* Fixed specification of Oracle SID using the -database command-line switch, as well as specification of port number.
* Fixed shell script classpath.

## Release 7.5.1 - 2009-11-28
<a name="v7.5.1"></a>

* Added methods to get imported and exported foreign keys and weak associations.

## Release 7.5 - 2009-11-25
<a name="v7.5"></a>

* Added a SchemaCrawlerInfo object to the database metadata, to provide the SchemaCrawler version number, and Java system properties.
* Updates to all bundled database drivers.
* Bug-fix for obtaining MySQL foreign keys.
* Added ability to script the database, using JavaScript. A live connection is provided to the JavaScript context for this purpose.
* Removed command-line switch, -schemapattern, and the schemapattern option. Use the -schemas command-line switch instead.
* Added sections to SchemaCrawler output, to make it more readable.
* Tables sort is done using graph algorithms, and includes cycle checks.

## Release 7.4 - 2009-09-24
<a name="v7.4"></a>

* Added new pre-packaged SchemaCrawler for Oracle.
* Fixed issue with index sort order of primary key columns.
* Updated website with new front page.

## Release 7.3.1 - 2009-09-24
<a name="v7.3.1"></a>

* Enhancement to the JavaSqlType class, to contain the SQL type group. New lookup by data-type name added to JavaSqlTypesUtility.
* Removed method, getTypeClass(), from ColumnDataType.
* Removed deprecated methods for grouping SQL data types (such as isReal()) from the ColumnDataType class.

## Release 7.3 - 2009-09-19
<a name="v7.3"></a>

* Added support for mapping of SQL data type to Java classes. This includes the new JavaSqlType class, and the JavaSqlTypesUtility. A new method, getTypeClass(), has been added to ColumnDataType.
* Added SchemaCrawler for Apache Derby, that is packaged for Apache Derby.

## Release 7.2 - 2009-08-21
<a name="v7.2"></a>

* Better modeling of privileges and grants.
* Bug fix for obtaining indexes and foreign keys with the Oracle driver.
* Added natural sort order for tables, based on the foreign keys, and corresponding new configuration option, and command-line switch, -sorttables. Also added new API methods to get child and parent tables, based on the foreign keys.
* Handle null schemas for databases that do not support schemas, such as MySQL.
* Allow data dumps when tables contain binary objects, by fixing the SELECT sort order.

## Release 7.1 - 2009-08-12
<a name="v7.1"></a>

* Better log messages, including for database connections, and inclusion of table and columns in the output.
* More efficient use of database connections, including a change to the Executable.execute method to take a database connection, rather than a data-source.
* Ensure that the pooled connection is closed, rather than the original database connection. This allows SchemaCrawler to play well with pooled connections.

## Release 7.0 - 2009-07-10
<a name="v7.0"></a>

* Major restructuring of the API, with the introduction of a Database object that contains all of the database metadata, and containing catalog and schema objects.
* To allow for more control on table selection, the -tables command-line parameter matches to the fully qualified table name, CATALOG.SCHEMA.TABLENAME. Similarly for the -routines command-line parameter.

## Release 6.4 - 2009-05-10
<a name="v6.4"></a>

* New pre-packaged distribution, SchemaCrawler for SQLite.
* Enhanced algorithm for finding weak associations.

## Release 6.3 - 2009-05-08
<a name="v6.3"></a>

* If no commands are specified, defaults to standard_schema, which provides the most commonly needed details of the schema.
* SchemaCrawler Grep functionality is built into the SchemaCrawler options, and the SchemaCrawler command-line. This means that all of the SchemaCrawler commands, including graphing, will use filtering and grep options.
* SchemaCrawler command-line option help has been re-written. New command-line options are available.
* SchemaCrawler can produce Graphviz DOT files to generate schema diagrams.

## Release 6.2 - 2009-04-08
<a name="v6.2"></a>

* A new feature to infer weak associations between tables, even if there is no foreign key. Ruby on Rails table schemes are supported, as well as other simple relationships. Table name prefixes are automatically detected.
* Database diagram support using Graphviz. Primary keys are indicated in a darker color than other columns. Foreign keys are indicated with arrow connectors. Weak associations between tables are shown in dashed lines. Multiple output formats are supported. Support for Jung is dropped.

## Release 6.1 - 2009-03-29
<a name="v6.1"></a>

* Better reporting of ascending and descending index columns. This is an API change.
* Better grep options, to allow searching through both tables and stored procedures.
* Grep options default to excluding stored procedures.
* Minor bug fixes for Sybase, including generating warnings when obtaining table column privileges, instead of failing.
* New Windows script file for locating Java, and launching SchemaCrawler.
* Reorganization of distribution, so that only a single SchemaCrawler jar file is distributed in all distributions. License distribution is also reorganized.

## Release 6.0.2 - 2008-11-30
<a name="v6.0.2"></a>

* Minor bug fixes for MySQL, where the schema name was being reported as null, causing an exception.

## Release 6.0.1 - 2008-10-30
<a name="v6.0.1"></a>

* Minor bug fixes for Oracle, where the data-type was a short instead of an integer.

## Release 6.0 - 2008-10-16
<a name="v6.0"></a>

* Changed the API so that there is a separation of concerns - the SchemaInfoLevel should be an option, not a CrawlHandler property.
* Changed the API so the top level object is a catalog, which contains schemas. This means that there is full support for multiple schemas.
* Added support for JavaScript scripting.

## Release 5.5 - 2008-02-28
<a name="v5.5"></a>

* Fixed bug that always reported an index as unique.
* Fixed bug with primary key columns.
* SchemaCrawler for Microsoft SQL Server to show procedures and triggers.
* SchemaCrawler distribution for any database.

## Release 5.4 - 2007-12-15
<a name="v5.4"></a>

* Added new command-line switch, -schemapattern, for specifying schema patterns when a connection is specified from the command-line.
* Added object attributes automatically, from unused columns in the metadata calls. Nothing goes to waste.
* Added JDBC driver metadata, including driver properties, and separated those from the database system metadata.

## Release 5.3 - 2007-11-15
<a name="v5.3"></a>

* Made sure that all relevant license files are getting delivered.
* Fixed bug in accessing foreign key update and delete rules on Oracle.
* Fixed equals comparison on all schema objects, and added a unit test for equals.
* Added an easier way to access foreign keys from the foreign key column itself Use isPartOfForeignKey(), and getReferencedColumn() on the Column class.
* Externalized the wrapper for java.sql.Types into a new public class, schemacrawler.schema.SqlDataType.
* Added the ability to get result-set metadata.
* Changed schema info level from an enum into a class that can be used to specify details on what aspects of the schema need to be retrieved.

## Release 5.2 - 2007-10-30
<a name="v5.2"></a>

* A significant re-write of the internal command processing code.
* Spring framework support for all of SchemaCrawler functionality, including integrations and grep.
* New example for grep.
* New example for using the Spring framework with SchemaCrawler.
* Pre-packaged releases do not load config file, even if available.
* Fixed Unix shell script for PostgreSQL.

## Release 5.1 - 2007-08-05
<a name="v5.1"></a>

* Simplified examples.
* Executable for SchemaCrawler for Microsoft SQL Server and MySQL.
* Commonly available SchemaCrawler command-line argument for setting the log level.

## Release 5.0 - 2007-06-25
<a name="v5.0"></a>

* API changed to use Java 5 constructs. Tested with Java 6\. API is no longer Java 1.4 compatible.
* Started SchemaCrawler Grep for MS SQL Server.
* Fixed java.lang.AbstractMethodError with Java 6.

## Release 4.2 - 2007-01-07
<a name="v4.2"></a>

* Fixed null pointer exception with DB2.
* Fixed error message on a bad command-line. Added a general -help option.
* Added new schema grep functionality, that can search for tables and columns usng regular expressions.
* Added new example for setting options programatically.
* Added documentation for providing database connection options directly on the command-line.
* Updated ant task to optionally take database connection options as arguments, instead of from the configuration file.

## Release 4.1 - 2006-12-07
<a name="v4.1"></a>

* Fixed bug [ 1610140 ] Sorting doesn't work.
* Fixed output of standard data types. Also changed the configuration option name.
* Fixed column output for tables and procedures that contain an underscore in their name. JDBC matches the underscore to a wildcard character.

## Release 4.0 - 2006-12-03
<a name="v4.0"></a>

* Added support for SQL standard INFORMATION_SCHEMA views. If equivalent SQL for the standard INFORMATION_SCHEMA view is specified, it is used to provide additional schema information such as check constraints, triggers, and view and procedure definitions.
* Added support for get index information views. This SQL will be used in preference to the getIndexInfo JDBC method to allow for databases and drivers that do not support getIndexInfo due to permissioning issues.
* Better output format for HTML and text.
* Better handling of output file resources.
* Better handling of database connection resources.
* More information on the website, and better website usability.

## Release 3.8 - 2006-09-26
<a name="v3.8"></a>

* Added Maven 2.0 plugin for generation of schema reports
* Added command-line option to provide database connection information
* Worked around a bug in the Oracle driver as described in <https://issues.apache.org/jira/browse/DDLUTILS-29?page=all>

## Release 3.7 - 2006-06-12
<a name="v3.7"></a>

* Added retrieval of database properties
* Added retrieval of column data types, both system datatypes and UDTs
* Added unit tests to ensure that the XHTML output is valid XML
* Fixed bug with merge rows option
* Fixed bug with appending output when multiple runs of SchemaCrawler append to the same output file
* Fixed SQLException with Oracle Database 10g Express Edition driver on outputting views
* Added XStream integration for XML serialization and deserialization
* Added new command, verbose_schema, that can give all possible details of the schema
* Obtain database system properties when the maximum schema command is used
* Added integrations with FreeMarker for templated output
* Removed Betwixt integration, since the required functionality is provided by XStream

## Release 3.6 - 2006-03-28
<a name="v3.6"></a>

* Added integrations with Velocity for templated output, and with JUNG for JPEG graph generation
* Added new examples to demonstrate Velocity templating, and JUNG graphs
* New command-line option to specify the output file name

## Release 3.5 - 2006-03-06
<a name="v3.5"></a>

* Added ability to retrieve column and table privileges
* Added Betwixt mappings for XML serialization and de-serialization
* Added Unix shell scripts for examples
* Fixed bug with the ordering of primary key and index columns
* Changed API - ColumnInfoLevel is called SchemaInfoLevel
* Moved source repository to Subversion

## Release 3.4 - 2006-01-16
<a name="v3.4"></a>

* Correction of spelling of the "sort_alphabetically" option names
* Refactoring of inheritance hierarchy of database objects, resulting in fewer equals, hashCode and compareTo implementations
* Better HTML formatting for counts
* Dirty reads are performed for counts and other operations
* Sets the default value for table columns
* Javadoc clean-up
* Findbugs, checkstyle, and PMD code smells clean-up

## Release 3.3 - 2005-11-03
<a name="v3.3"></a>

* The ability to include as well as exclude tables using regular expressions.
* The ability to include as well as exclude columns using regular expressions.
* A new substitution variable, ${columns}, for use in database queries that iterate over tables.
* New examples for executing database-specific and user-defined queries have been added.
* Better output formatting for HTML dumps, and counts in text format.
* New website.
* Worked around an issue with the Sybase driver, that provides primary keys in a different order than most other drivers.
* Improved logging, and made the log level in the configuration really work.

