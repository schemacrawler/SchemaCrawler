# SchemaCrawler Lint

A lot of database schema designers do not follow good design practices, simply because these 
are not documented well. A lot of people know about normalization, and take care to either 
normalize their tables, or deliberately denormalize them for performance reasons. However, 
what about "design smells", such as column names that are SQL reserved words, such as a 
column called `COUNT`? Or, foreign key and primary key columns that have different data 
types?

SchemaCrawler can analyze and
[lint](http://en.wikipedia.org/wiki/Lint_\(software\)) your database to find
potential design flaws. SchemaCrawler Lint can be run using the 
`-command=lint`
command-line option. A lint report will be produced in any specified format -
either text, CSV, HTML5 or JSON.

SchemaCrawler Lint is a separate jar, and contains both the framework for doing database 
schema lints, as well as some checks for common database schema design issues. You can 
extend this by creating your own jar that contains lint checks.

For more details, see the lint example in the 
[SchemaCrawler examples](http://github.com/sualeh/SchemaCrawler/releases/) 
download.

## SchemaCrawler Lint Reports

SchemaCrawler Lint can produce reports in [text](lint-report-
examples/lint_report.txt) , [HTML5](lint-report-examples/lint_report.html) or
[JSON](lint-report-examples/lint_report.json) format. (Click on the links for
example reports.)

SchemaCrawler linters can be configured (both severity, and thresholds) using
an [XML configuration file.](schemacrawler-linter-configs.xml) You can run SchemaCrawler
lint with an additional command-line option, for example, 
`-linterconfigs=[path to linter XML configuration file]`, 
pointing to the path of the SchemaCrawler linter XML configuration file.

## Lint Checks

SchemaCrawler Lint has a number of lint checks built-in. These are prioritized
as critical, high, medium and low. The results are shown on the lint report.
The checks are:

- Tables with incrementing column names, for example, a table with column names like CONTACT1, CONTACT2 
  and so on can indicate de-normalization.  
  Additionally, SchemaCrawler Lint will check that the data-types of all incrementing columns are the same, 
  and that no numbers are skipped.
  (Linter id "schemacrawler.tools.linter.LinterTableWithIncrementingColumns")
- Tables with no columns at all, or just a single column could indicate a schema design smell.
  (Linter id "schemacrawler.tools.linter.LinterTableWithSingleColumn")
- Tables with no indexes.
  (Linter id "schemacrawler.tools.linter.LinterForeignKeyWithNoIndexes")
- Tables where the default value is 'NULL' instead of NULL may indicate a error when creating a table.
  (Linter id "schemacrawler.tools.linter.LinterNullIntendedColumns")
- Tables that have nullable columns in a unique index.
  (Linter id "schemacrawler.tools.linter.LinterNullColumnsInIndex")
- Tables that have spaces in table or column names, or names that are reserved words in the 
  ANSI SQL standard.
  (Linter id "schemacrawler.tools.linter.LinterTableWithQuotedNames")
- Tables that have too many large objects (CLOBs or BLOBs), since these could result in 
  additional reads when returning query results.
  (Linter id "schemacrawler.tools.linter.LinterTooManyLobs")
- Tables where the foreign key column data type is different from the referenced primary key column data type.
  (Linter id "schemacrawler.tools.linter.LinterForeignKeyMismatch")
- Columns in different tables, that have the same name but have different data types.
  (Linter id "schemacrawler.tools.linter.LinterColumnTypes")
- Cyclical relationships between tables, which could cause issues with deletes and inserts.
  (Linter id "schemacrawler.tools.linter.LinterTableCycles")
- Tables with redundant indexes.  
  A redundant index is one where the sequence of columns is 
  the same as the first few columns of another index. For example, the index `INDEX_B(COL1)` is 
  not needed when you have another index, `INDEX_A(COL1, COL2)`.
  (Linter id "schemacrawler.tools.linter.LinterRedundantIndexes")
- Relationship tables with just foreign keys and no attributes, but still have a primary key.
  (Linter id "schemacrawler.tools.linter.LinterUselessSurrogateKey")
- Tables with no indexes.
  (Linter id "schemacrawler.tools.linter.LinterTableWithNoIndexes")
- Tables with no primary key. If a table is a relationship table with no attributes, it will not
  be flagged.
  (Linter id "schemacrawler.tools.linter.LinterTableWithNoPrimaryKey")  
- Empty tables with no data.
  (Linter id "schemacrawler.tools.linter.LinterTableEmpty")
    
## Lint Configuration

You can customize SchemaCrawler lints using an XML configuration file. You can specify the 
path and filename of the configuration file using the `-linterconfigs=<path>` 
command-line option.

Here is an example:

```
<schemacrawler-linter-configs>
  <linter id="schemacrawler.tools.linter.LinterTableWithNoIndexes">
    <run>true</run>
    <severity>medium</severity>
    <table-inclusion-pattern><![CDATA[.*]]></table-inclusion-pattern>
    <table-exclusion-pattern><![CDATA[.*BOOKS]]></table-exclusion-pattern>
  </linter>
</schemacrawler-linter-configs>
```

With this lint configuration, you can

- Use `<run>` to indicate whether or not to execute a certain lint. The
  default is to run every lint.
- Set the severity, which can be one of either low, medium, high, or critical.
  Most lints default to a medium severity.
- Set table inclusion and exclusion patterns. These are regular expressions that
  match the fully qualified table name. The lint will run against the table 
  only if it matches the inclusion rule. The default is to include every
  table, and exclude none.


## Custom SQL Lints

SchemaCrawler allows you to define your own lints using SQL statements. These can run either 
against a table, or against the entire catalog. Custom SQL lints allow you to lint your 
data. For example, you may want to flag any table that does not have any data.

The SQL statement must return exactly one column and one row of data in the results. If one 
row is returned, it means that the lint has detected a problem. However, if no rows of data 
are returned, it means that there are no issues. Custom SQL lints are configured in the XML 
configuration file.

This is an example configuration of a SQL lint, that runs against a table. Notice the use of 
`${table}` to indicate the name of the table the lint is running against.

```
<schemacrawler-linter-configs>
  <linter id="schemacrawler.tools.linter.LinterTableSql">
    <config>
      <property name="message">message for custom SQL lint</property>
      <property name="sql"><![CDATA[SELECT TOP 1 1 FROM ${table}]]></property>
    </config>
  </linter>
</schemacrawler-linter-configs>
```

This is an example configuration of a SQL lint, that runs against the database. Notice that 
there is no `${table}` in the SQL statement.

```
<schemacrawler-linter-configs>
  <linter id="schemacrawler.tools.linter.LinterCatalogSql">
    <config>
      <property name="message">message for SQL catalog lint</property>
      <property name="sql">SELECT TOP 1 1 FROM INFORMATION_SCHEMA.TABLES</property>
    </config>
  </linter>  
</schemacrawler-linter-configs>
```

## Lint Extensions

In addition, organizations may have their own design practices, for example that names of 
all tables relating to customers (that is, all tables with `CUSTOMER_ID` column) are 
prefixed with `CUST_`. SchemaCrawler allows you to write your own lint plugins to detect 
these. SchemaCrawler can be run in automated builds, and you can write tests that fail your 
build if a developer violates your rules.

SchemaCrawler Lint is [very easily extended](plugins.html) for custom database schema checks.
The main distribution has example code. In order to add your own lint checks,

- Create a class that extends `schemacrawler.tools.lint.Linter`. 
  It is easiest to extend `schemacrawler.tools.lint.BaseLinter` , since you get 
  convenient `addLint` methods 
- Package your code in a jar file, and make sure that the jar has a text file 
  called `META-INF\services\schemacrawler.tools.lint.Linter` , 
  which contains the classnames of your linter classes 
- Drop your jar file in the SchemaCrawler lib directory, and create a 
  SchemaCrawler Lint report
