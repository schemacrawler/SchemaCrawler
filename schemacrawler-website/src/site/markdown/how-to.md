<!-- markdownlint-disable MD024 -->
# SchemaCrawler - How-to

## SchemaCrawler Command-line

1. [How to run the SchemaCrawler command-line](#command-line)


## Include and Exclude Stuff from the Output

1. [How to see why certain tables or columns for excluded](#excluded-tables)
2. [How to include only significant columns - that is, columns that are part of a primary key or unique index, or columns that are foreign keys](#include-significant-columns)
3. [How to include or exclude certain tables or columns](#excluded-tables-or-columns)
4. [How to exclude database views from the output](#excluded-views)
5. [How to exclude routines, that is, stored procedures and functions from the output](#excluded-routines)
6. [How to exclude database functions from the output](#excluded-functions)
7. [How to exclude database table and column remarks or comments from the output](#excluded-remarks)
8. [How to do case-insenstive filtering](#case-insensitive)

## Create diff-able Output

1. [How to sort columns, foreign-keys and indexes alphabetically](#sorting)
2. [How to diff column data types across databases](#diff-data-types)
3. [How to allow diffs of tables that have columns added in between](#ordinal-numbers)
4. [How to hide display of object names that can change from server to server](#portable-names)
5. [How to show table row counts in output and diagrams](#table-row-counts)
6. [How to hide foreign key names, constraint names, trigger names, specific names for procedures, or index and primary key names](#index-names)
7. [How to hide catalog and schema names in text output](#hide-schema-names)
8. [How to hide catalog and schema colors in HTML output and schema diagrams](#hide-schema-colors)

## Integrations

1. [How to script with your database](#javascript)
2. [How to create your own output format](#velocity)
3. [How to create a database diagram of your schema](#diagrams)
4. [How to provide additional Graphviz command-line options](#graphviz_opts)
5. [How to use pure-Java Graphviz](#graphviz-java)

## How to Use SchemaCrawler in Projects

1. [How to use SchemaCrawler programmatically](#api)
2. [How to use SchemaCrawler in an Apache Maven Project](#maven-project)
3. [How to use SchemaCrawler as an ant Task](#ant)
4. [How to use SchemaCrawler To Produce an Apache Maven Report](#maven-report)

## Advanced SchemaCrawler Usage

1. [How to get trigger, view, stored procedure and function definitions](#definitions)
2. [How to obtain check constraints](#check_constraints)
3. [How to get tables in "create" or "drop" order](#create_order)
4. [How to extend SchemaCrawler by adding a new command, new linter, or new database system support](#plugins)
5. [How to configure SchemaCrawler linters](#configure_linter)
6. [How to extend SchemaCrawler by adding a new linter](#add_new_linter)
7. [How to fail a build with too many SchemaCrawler lints](#fail_build_linter)
8. [How to obtain PostgreSQL materialized views](#pgsql_materialized_views)

---------

## <a name="command-line">How to Use the SchemaCrawler Command-line</a>

Explore the SchemaCrawler command-line with a [live online tutorial](https://killercoda.com/schemacrawler). 
The tutorial works from within any browser with no software or plugins needed.

Use `schemacrawler.Main` to launch SchemaCrawler from the command-line. This launch offers a
number of connection options, including by JDBC driver and URL, and by a connection defined in
a properties configuration file. There are options to load configuration from properties files.
JDBC drivers and other external libraries must be available on the classpath for this
application to function.

For help, use the `-h` command-line switch.

---------

## Include and Exclude Stuff from the Output

---------

### <a name="excluded-tables">How to see why certain tables or columns for excluded</a>
Re-run SchemaCrawler with `--log-level=ALL` on the command-line.

---------

### <a name="include-significant-columns">How to include only significant columns - that is, columns that are part of a primary key or unique index, or columns that are foreign keys</a>
Re-run SchemaCrawler with the `--info-level=standard --command=brief` command-line options.

---------

### <a name="excluded-tables-or-columns">How to include or exclude certain tables or columns</a>
Change the configuration for the SchemaCrawler the table or column include and exclude patterns in the
`schemacrawler.config.properties` file. The include or exclude specification is
a [Java regular expression](https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html).
The include pattern is evaluated first, and the exclusions are made from the included tables or columns list.
Also see the [filtering and grep command-line options.](faq.html#commands)

---------

### <a name="excluded-views">How to exclude database views from the output</a>
Use the `--table-types` command-line option, without VIEW. For example, you can provide `--table-types=TABLE`.
Further, see the [details on the command-line options.](faq.html#commands)

---------

### <a name="excluded-routines">How to exclude routines, that is, stored procedures and functions from the output</a>

The option in the configuration can be overridden by the `--routines=` command-line option.
Further, see the [details on the command-line options.](faq.html#commands)

---------

### <a name="excluded-functions">How to exclude database functions from the output</a>
Use the `--routine-types=PROCEDURE` command-line option. Further, see the [details on the command-line options.](faq.html#commands)

---------

### <a name="excluded-remarks">How to exclude database table and column remarks or comments from the output</a>
Use the `--no-remarks` command-line option. Further, see the [details on the command-line options.](faq.html#commands)

---------

### <a name="case-insensitive">How to do case-insenstive filtering</a>
For any SchemaCrawler argument that uses regular expressions, you can use
[mode modifiers](https://www.regular-expressions.info/refmodifiers.html)
for case-insensitive matches. For example, to grep for column names, use a regular expression
like `--grep-columns='(?i).*data'`

---------


## Create diff-able Output

---------

### <a name="sorting">How to sort columns, foreign-keys and indexes alphabetically</a>
Change the configuration for the SchemaCrawler "sort alphabetically" properties in the
[`schemacrawler.config.properties`](config/schemacrawler.config.properties) file.
Also see the [sorting command-line options.](faq.html#commands)

---------

### <a name="diff-data-types">How to diff column data types across databases</a>
Change the configuration for the SchemaCrawler `schemacrawler.format.show_standard_column_type_names=true`
in the `schemacrawler.config.properties` file. This setting will show standard data types across different database systems.
On the other hand, if you want to see the real database specific data types, change the setting to a value of true.

---------

### <a name="ordinal-numbers">How to allow diffs of tables that have columns added in between</a>

When columns are added into a table, they can change the column ordinal number. This can mess
up the diffs. Change the configuration for the SchemaCrawler
`schemacrawler.format.show_ordinal_numbers=false` in the `schemacrawler.config.properties`
file. You can combine this setting with the setting to sort columns alphabetically to produce
diff friendly output.

---------

### <a name="portable-names">How to hide display of object names that can change from server to server</a>

Use the `--portable-names=true` command-line option to allow for easy comparison between
databases, by hiding foreign key names, constraint names, trigger names, specific names for
procedures, and index and primary key names, and not showing the fully-qualified table name.

---------

### <a name="table-row-counts">How to hide table row counts in output and diagrams</a>

Hide table row counts in output, and diagrams. Change the configuration for the SchemaCrawler
`schemacrawler.format.hide_table_row_counts=true` in the `schemacrawler.config.properties` file.
Then even if you run SchemaCrawler with the `--load-row-counts` command-line option, table row counts
will not be shown in the output.

---------

### <a name="hide-empty-tables">How to hide empty tables in output and diagrams</a>

Hide tables with no data in output, and diagrams. Run SchemaCrawler with the 
`--load-row-counts --no-empty-tables` command-line options, while table row counts
will be shown in the output, empty tables will be omitted.

---------

### <a name="index-names">How to hide foreign key names, constraint names, trigger names, specific names for procedures, or index and primary key names</a>

If foreign key names, constraint names, trigger names, specific names for procedures, or index
and primary key names are not explicitly provided while creating a schema, most database
systems assign default names. These names can show up as spurious diffs in SchemaCrawler
output. Change the configuration for the following properties in your
`schemacrawler.config.properties` file. All of these names can be hidden by using the
`--portable-names` command-line option.

```
schemacrawler.format.hide_primarykey_names=false
schemacrawler.format.hide_foreignkey_names=false
#schemacrawler.format.hide_alternatekeys_names=false
schemacrawler.format.hide_weakassociation_names=false
schemacrawler.format.hide_index_names=false
schemacrawler.format.hide_trigger_names=false
schemacrawler.format.hide_routine_specific_names=false
schemacrawler.format.hide_constraint_names=false
```
---------

### <a name="hide-schema-names">How to hide catalog and schema names in text output</a>

Change the configuration for the SchemaCrawler `schemacrawler.format.show_unqualified_names=true` in the `schemacrawler.config.properties` file. This setting will show unqualified names of database objects such as tables and procedures. That is, the catalog and schema names will not be displayed. Use with care, especially if you have foreign keys that reference tables in other schemas, or synonyms.

---------

### <a name="hide-schema-colors">How to hide catalog and schema colors in HTML output and schema diagrams</a>

Change the configuration for the SchemaCrawler `schemacrawler.format.no_schema_colors=true` in the `schemacrawler.config.properties` file. This setting will not show color-coded catalog and schema names in HTML and schema diagram output.

---------

## Integrations

### <a name="javascript">How to script with your database</a>

SchemaCrawler has built-in support to be used with JavaScript scripts. Write your JavaScript file, assuming that a "catalog" variable containing the database schema will be available. A "connection" variable will also be available, and you will be able to execute SQL against your database. Run SchemaCrawler with the command-line options - `--command script --script <your script file>` . See the example in the `examples\javascript` directory for more details.

---------

### <a name="velocity">How to create your own output format</a>

SchemaCrawler integrates with [Apache Velocity](https://velocity.apache.org/) to allow for templated output. Put Velocity on your classpath, and create your template, and run SchemaCrawler with the command-line options `--command velocity --template <your Velocity template>`. See the Velocity example in the [SchemaCrawler examples](https://www.schemacrawler.com/downloads.html#running-examples-locally/) download.

---------

### <a name="diagrams">How to create a database diagram of your schema</a>

SchemaCrawler integrates with [Graphviz](https://www.graphviz.org/) to produce graph images. See the diagram example in the [SchemaCrawler examples](https://www.schemacrawler.com/downloads.html#running-examples-locally/) download. For more details, see the [diagram section](diagramming.html).

---------

### <a name="graphviz_opts">How to provide additional Graphviz command-line options</a>

SchemaCrawler integrates with [Graphviz](https://www.graphviz.org/) to produce graph images. See the previous question for details.
You can provide additional Graphviz command-line options using the `SC_GRAPHVIZ_OPTS` environmental variable, or pass in the additional arguments using the `SC_GRAPHVIZ_OPTS` Java system property.

---------

### <a name="graphviz-java">How to use pure-Java Graphviz</a>

In certain situations, it may not be possible to install Graphviz. SchemaCrawler integrates with [nidi3/graphviz-java](https://github.com/nidi3/graphviz-java) to provide a pre-Java approach to generating graphs. Download the [SchemaCrawler distribution](https://www.schemacrawler.com/downloads.html#running-examples-locally), and run through the `diagram` example. This will download the required jar files automatically.

---------


## How to Use SchemaCrawler in Projects

---------

### <a name="api">How to use SchemaCrawler programmatically</a>

Read [How to Get Database Metadata as Java POJOs](https://dev.to/sualeh/how-to-get-database-metadata-as-java-pojos-24li), and browse the [javadocs](https://javadoc.io/doc/us.fatehi/schemacrawler/).

_See the api example in the [SchemaCrawler examples](https://www.schemacrawler.com/downloads.html#running-examples-locally/) download._

Or, if you are impatient, try code similar to the following:

<script src="https://gist.github.com/schemacrawler/63e4b8cb0515c6e928e7a9a419f46411.js"></script>
More code examples are at [Code Examples Using the SchemaCrawler API](code-examples.html).

---------

### <a name="maven-project">How to use SchemaCrawler in an Apache Maven Project</a>

In order to use SchemaCrawler in your Apache Maven projects, add a dependency to SchemaCrawler in your pom.xml.

```xml
<dependencies>
  ...
  <dependency>
    <groupId>us.fatehi</groupId>
    <artifactId>schemacrawler</artifactId>
    <version>16.21.3</version>
  </dependency>
</dependencies>
```

---------

### <a name="ant">How to use SchemaCrawler as an ant Task</a>

Call the SchemaCrawler command-line from ant, using the exec task.

---------

### <a name="maven-report">How to use SchemaCrawler To Produce an Apache Maven Report</a>

The [SchemaCrawler Report Maven Plugin](https://github.com/schemacrawler/SchemaCrawler-Report-Maven-Plugin)
can generate database documentation for an Apache Maven-generated website.

---------

## Advanced SchemaCrawler Usage

---------

### <a name="definitions">How to get trigger, view, stored procedure and function definitions</a>

_See the documentation in [Extensions Using the Data Dictionary](data-dictionary-extensions.html) ._

---------

### <a name="check_constraints">How to obtain check constraints</a>

_See the documentation in [Extensions Using the Data Dictionary](data-dictionary-extensions.html) ._

---------

### <a name="create_order">How to get tables in "create" or "drop" order</a>

Tables are sorted in alphabetical order by default. If you turn alphabetical sorting off, the
tables will be displayed in "create" order - that is, tables with no foreign-key dependencies
will be displayed first. The "drop" order is the reverse of the "create" order. Use the
following command-line arguments to obtain tables in "create" order: `--command=list
--sort-tables=false --routines=`

---------

### <a name="plugins">How to extend SchemaCrawler by adding a new command, new linter, or new database system support</a>

See the [SchemaCrawler Plugins](plugins.html) page.

---------

### <a name="configure_linter">How to configure SchemaCrawler linters</a>

See [SchemaCrawler Lint](lint.html) for details.

---------

### <a name="add_new_linter">How to extend SchemaCrawler by adding a new linter</a>

See [SchemaCrawler Lint](lint.html) for details.

---------

### <a name="fail_build_linter">How to fail a build with too many SchemaCrawler lints</a>

See [SchemaCrawler Lint](lint.html) for details.

---------

### <a name="pgsql_materialized_views">How to obtain Oracle and PostgreSQL materialized views</a>

PostgreSQL materialized views are a table type called "MATERIALIZED VIEW". Use an additional SchemaCrawler command-line option, `"--table-types=TABLE,VIEW,MATERIALIZED VIEW"`. Notice the double-quotes, since there is a space between
`MATERIALIZED` and `VIEW`.

---------

