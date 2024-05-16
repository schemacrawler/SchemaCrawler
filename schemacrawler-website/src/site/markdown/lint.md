# SchemaCrawler Lint

A lot of database schema designers do not follow good design practices, simply because these 
are not documented well. A lot of people know about normalization, and take care to either 
normalize their tables, or deliberately denormalize them for performance reasons. However, 
what about "design smells", such as column names that are SQL reserved words, such as a 
column called `COUNT`? Or, foreign key and primary key columns that have different data 
types?

SchemaCrawler can analyze and
[lint](https://en.wikipedia.org/wiki/Lint_(software)) your database to find
potential design flaws. SchemaCrawler Lint can be run using the 
`--command=lint` command-line option.

SchemaCrawler Lint is a separate jar, and contains both the framework for doing database 
schema lints, as well as some checks for common database schema design issues. You can 
extend this by creating your own jar that contains lint checks.

For more details, see the `lint` example in the 
[SchemaCrawler examples](https://www.schemacrawler.com/downloads.html#running-examples-locally/) 
download.

## SchemaCrawler Lint Reports

SchemaCrawler Lint can produce reports in 
[text](lint-report-examples/lint_report.text), 
[HTML5](lint-report-examples/lint_report.html),
[JavaScript object notation (JSON)](lint-report-examples/lint_report.json) or
[YAML](lint-report-examples/lint_report.yaml) format. 
(Click on the links for example reports.) 
A lint report will be produced in the format specified using the 
`--output-format` command-line option. For example,
`--output-format=json` will generate a lint report in JSON format.

SchemaCrawler linters can be configured (both severity, and thresholds) using
a [YAML configuration file.](config/schemacrawler-linter-configs.yaml) You can run SchemaCrawler
lint with an additional command-line option, for example, 
`--linter-configs=<path to linter configuration file>`, 
pointing to the path of the SchemaCrawler linter configuration file. You can
configure whether or not to run a linter, change a linter's severity, or exclude
certain tables and columns from the linter using the configuration file. You can 
also configure a threshold to fail a build if too many lints are found.

SchemaCrawler Lint has a number of lint checks built-in. These are prioritized
as critical, high, medium and low. The results are shown on the lint report. 
The checks are detailed below.


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

```xml
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

