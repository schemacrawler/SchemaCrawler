# SchemaCrawler Lint

A lot of database schema designers do not follow good design practices, simply because these 
are not documented well. A lot of people know about normalization, and take care to either 
normalize their tables, or deliberately denormalize them for performance reasons. However, 
what about "design smells", such as column names that are SQL reserved words, such as a 
column called `COUNT`? Or, foreign key and primary key columns that have different data types? 

SchemaCrawler can analyze and
[lint](http://en.wikipedia.org/wiki/Lint_\(software\)) your database to find
potential design flaws. SchemaCrawler Lint can be run using the 
`-command=lint`
command-line option. A lint report will be produced in any specified format -
either text, CSV, HTML5 or JSON.

SchemaCrawler Lint is a separate jar, and contains both the framework for
doing database schema lints, as well as some checks for common database schema
design issues. You can extend this by creating your own jar that contains lint
checks.

For more details, see lint example in the 
[SchemaCrawler examples](https://sourceforge.net/projects/schemacrawler/files/SchemaCrawler%20Examples/) 
download.

## SchemaCrawler Lint Reports

SchemaCrawler Lint can produce reports in [text](lint-report-
examples/lint_report.txt) , [HTML5](lint-report-examples/lint_report.html) or
[JSON](lint-report-examples/lint_report.json) format. (Click on the links for
example reports.)

SchemaCrawler linters can be configured (both severity, and thresholds) using
an [XML configuration file.](schemacrawler-linter-configs.xml) You can provide
a system property, `schemacrawer.linter_configs.file`, pointing to the path of
the SchemaCrawler linter XML configuration file. On the command-line, you can
use `-Dschemacrawer.linter_configs.file=[path]`.

## Lint Checks

SchemaCrawler Lint has a number of lint checks built-in. These are prioritized
as critical, high, medium and low. The results are shown on the lint report.
The checks are:

- Tables with incrementing column names, for example, a table with column names like CONTACT1, CONTACT2 
  and so on can indicate de-normalization.  
  Additionally, SchemaCrawler Lint will check that the data-types of all incrementing columns are the same, 
  and that no numbers are skipped.
- Tables with no columns at all, or just a single column could indicate a schema design smell.
- Tables with no indices.
- Tables where the default value is 'NULL' instead of NULL may indicate a error when creating a table.
- Tables that have nullable columns in a unique index.
- Tables that have spaces in table or column names, or names that are reserved words in the 
  ANSI SQL standard.
- Tables that have too many large objects (CLOBs or BLOBs), since these could result in 
  additional reads when returning query results.
- Tables foreign key and primary key have different data types.
- Columns in different tables, that have the same name but have different data types.
- Cyclical relationships between tables, which could cause issues with deletes and inserts.
- Tables with redundant indices.  
  A redundant index is one where the sequence of columns is 
  the same as the first few columns of another index. For example, the index `INDEX_B(COL1)` is 
  not needed when you have another index, `INDEX_A(COL1, COL2)`.
- Relationship tables with just foreign keys and no attributes, but still have a primary key.

## Lint Extensions

In addition, organizations may have their own design practices, for example that names of 
all tables relating to customers (that is, all tables with `CUSTOMER_ID` column) are prefixed 
with `CUST_`. SchemaCrawler allows you to write your own lint plugins to detect these. 
SchemaCrawler can be run in automated builds, and you can write tests that fail your build 
if a developer violates your rules.

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
