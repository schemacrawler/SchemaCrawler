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
pointing to the path of the SchemaCrawler linter XML configuration file. You can
configure whether or not to run a linter, change a linter's severity, or exclude
certain tables and columns from the linter using the configuration file. You can 
also configure a threshold to fail a build if too many lints are found.

## Lint Checks

SchemaCrawler Lint has a number of lint checks built-in. These are prioritized
as critical, high, medium and low. The results are shown on the lint report.
The checks are:

**Linter:** *schemacrawler.tools.linter.LinterCatalogSql*    
Allows you to run SQL against the database. The SQL statement must
return exactly one column and one row of data in the results. If one row
is returned, it means that the lint has detected a problem. However, if
no rows of data are returned, it means that there are no issues. 
Example configuration:

```
<linter id="schemacrawler.tools.linter.LinterCatalogSql">
  <config>
    <property name="message">message for SQL catalog lint</property>
    <property name="sql"><![CDATA[SELECT TOP 1 1 FROM INFORMATION_SCHEMA.TABLES]]></property>
  </config>
</linter>
```

**Linter:** *schemacrawler.tools.linter.LinterColumnTypes*   
Looks for columns in different tables, that have the same name but have
different data types.

**Linter:** *schemacrawler.tools.linter.LinterForeignKeyMismatch*   
Checks tables where the foreign key column data type is different from
the referenced primary key column data type.

**Linter:** *schemacrawler.tools.linter.LinterForeignKeyWithNoIndexes*   
Checks for tables where foreign keys have no indexes. This may cause
inefficient lookups.

**Linter:** *schemacrawler.tools.linter.LinterNullColumnsInIndex*    
Checks for tables that have nullable columns in a unique index.

**Linter:** *schemacrawler.tools.linter.LinterNullIntendedColumns*    
Checks for tables where the default value is 'NULL' instead of NULL,
since this may indicate a error when creating a table.

**Linter:** *schemacrawler.tools.linter.LinterRedundantIndexes*   
Checks for tables with redundant indexes. A redundant index is one where
the sequence of columns is the same as the first few columns of another
index. For example, the index `INDEX_B(COL1)` is not needed when you have
another index, `INDEX_A(COL1, COL2)`.

**Linter:** *schemacrawler.tools.linter.LinterTableAllNullableColumns*   
Tables that have all columns besides the primary key that are nullable,
may contain no useful data, and could indicate a schema design smell.

**Linter:** *schemacrawler.tools.linter.LinterTableCycles*   
Checks for cyclical relationships between tables, which could cause
issues with deletes and inserts.

**Linter:** *schemacrawler.tools.linter.LinterTableEmpty*   
Checks for empty tables with no data.

**Linter:** *schemacrawler.tools.linter.LinterTableSql*   
Allows you to run SQL against the database. The SQL statement must
return exactly one column and one row of data in the results. If one row
is returned, it means that the lint has detected a problem. However, if
no rows of data are returned, it means that there are no issues.
Notice the use of `${table}` to indicate the name of the table the lint
is running against.   
Example configuration:

```
<linter id="schemacrawler.tools.linter.LinterTableSql">
  <table-exclusion-pattern><![CDATA[.*BOOKS]]></table-exclusion-pattern>
  <config>
    <property name="message">message for custom SQL lint</property>
    <property name="sql"><![CDATA[SELECT TOP 1 1 FROM ${table}]]></property>
  </config>
</linter>
```

**Linter:** *schemacrawler.tools.linter.LinterTableWithBadlyNamedColumns*   
Checks for columns that should not be named according to certain patterns.
For example, you may have a policy that no column can be named `ID`,
because you want columns with complete names, such as `ORDER_ID`.
If you want to detect columns named `ID`, you could use configuration as
shown in the example below.  
Example configuration:

```
<linter id="schemacrawler.tools.linter.LinterTableWithBadlyNamedColumns">
  <config>
    <property name="bad-column-names"><![CDATA[.*\.ID]]></property>
  </config>
</linter>
```

**Linter:** *schemacrawler.tools.linter.LinterTableWithIncrementingColumns*   
Checks for tables with incrementing column names, for example, a table
with column names like `CONTACT1`, `CONTACT2` and so on can indicate
de-normalization. Additionally, SchemaCrawler Lint will check that the
data-types of all incrementing columns are the same, and that no numbers
are skipped.

**Linter:** *schemacrawler.tools.linter.LinterTableWithNoIndexes*   
Checks for tables with no indexes.

**Linter:** *schemacrawler.tools.linter.LinterTableWithNoPrimaryKey*   
Checks for tables with no primary keys. Tables that purely model
relationships, without any attributes are ignored.

**Linter:** *schemacrawler.tools.linter.LinterTableWithNoRemarks*   
Checks for tables and columns with no remarks.

**Linter:** *schemacrawler.tools.linter.LinterTableWithNoSurrogatePrimaryKey*   
Checks for tables that have more than one column as a primary key, and recommends 
that a surrogate key column be used as a primary key instead.

**Linter:** *schemacrawler.tools.linter.LinterTableWithPrimaryKeyNotFirst*   
Checks for tables where the primary key columns are not first, since
this is the convention.

**Linter:** *schemacrawler.tools.linter.LinterTableWithQuotedNames*   
Checks for tables that have spaces in table or column names, or names
that are reserved words in the ANSI SQL standard.

**Linter:** *schemacrawler.tools.linter.LinterTableWithSingleColumn*   
Checks for tables with no columns at all, or just a single column, since
that could indicate a schema design smell.

**Linter:** *schemacrawler.tools.linter.LinterTooManyLobs*   
Checks for tables that have too many large objects (CLOBs or BLOBs),
since these could result in additional reads when returning query
results. By default, this is more than one such column.   
Example configuration:

```
<linter id="schemacrawler.tools.linter.LinterTooManyLobs">
  <config>
    <property name="max-large-objects">3</property>
  </config>
</linter>
```

**Linter:** *schemacrawler.tools.linter.LinterForeignKeySelfReference*   
Checks tables where the foreign key self-references the primary key. 
This means that a record in the table references itself, and
cannot be deleted.


## Enforcing Good Schema Design During Builds

SchemaCrawler Lint can be configured to fail a build using a configuration file. 
You can call SchemaCrawler using a regularly constructed command-line, 
from Apache Maven using the Exec Maven Plugin, or from ant using the 
java task, or from Gradle using JavaExec.

In the configuration file, you can set a dispatch threshold for linters. 
If the number of lints for that linter exceeds the 
threshold, the lint dispatch will take effect. To specify a dispatch method,
provide an additional command-line argument, such as 
`-lintdispatch=terminate_system`

Valid lint dispatch methods are 

- `terminate_system` - Return an error code of 1 to the system, by calling System.exit(1)
- `throw_exception` - Throw a runtime exception
- `write_err` - Write to stderr, and continue on
- `none` - No dispatch, and continue on normally

Here is an example linter configuration, with a threshold defined, making it eligible for dispatch:

```
<linter id="schemacrawler.tools.linter.LinterTableWithNoIndexes">
  <severity>critical</severity>
  <threshold>1</threshold>
</linter>
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
