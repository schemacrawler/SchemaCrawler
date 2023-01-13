# SchemaCrawler

SchemaCrawler is a free database schema discovery and comprehension tool.
SchemaCrawler has a good mix of useful features for data governance. You can
[search for database schema objects](schemacrawler-grep.html) using regular
expressions, and output the schema and data in a readable text format. The
output serves for database documentation, and is designed to be
[diff-ed](https://en.wikipedia.org/wiki/Diff) against other database schemas.
SchemaCrawler also generates [schema diagrams.](diagramming.html) You can
[execute scripts](scripting.html) in any standard scripting language against
your database. You can find potential schema design issues with
[lint.](lint.html)

SchemaCrawler supports almost any database that has a JDBC driver, but for
convenience is [bundled with drivers](database-support.html) for some commonly used
RDBMS systems. SchemaCrawler works with any operating system that supports
Java SE 8 or better.

[![GitHub Repo stars](https://img.shields.io/github/stars/schemacrawler/schemacrawler?style=social)](https://github.com/schemacrawler/SchemaCrawler)
[![Quick Build](https://github.com/schemacrawler/SchemaCrawler/workflows/Quick%20Build/badge.svg)](https://github.com/schemacrawler/SchemaCrawler/actions?query=workflow%3A%22Quick+Build%22)
[![Integration Tests](https://github.com/schemacrawler/SchemaCrawler/workflows/Integration%20Tests/badge.svg)](https://github.com/schemacrawler/SchemaCrawler/actions?query=workflow%3A%22Integration+Tests%22)
[![codecov](https://codecov.io/gh/schemacrawler/SchemaCrawler/branch/master/graph/badge.svg)](https://app.codecov.io/gh/schemacrawler/SchemaCrawler)

[![The Central Repository](https://img.shields.io/maven-central/v/us.fatehi/schemacrawler.svg)](https://search.maven.org/search?q=g:us.fatehi%20schemacrawler*)
[![Main distribution](https://img.shields.io/github/downloads/schemacrawler/schemacrawler/total)](https://github.com/schemacrawler/SchemaCrawler/releases/latest)
[![Docker Pulls](https://img.shields.io/docker/pulls/schemacrawler/schemacrawler.svg)](https://hub.docker.com/r/schemacrawler/schemacrawler/)
[![Chocolatey](https://img.shields.io/chocolatey/v/schemacrawler)](https://community.chocolatey.org/packages/schemacrawler)
[![Scoop](https://img.shields.io/scoop/v/schemacrawler.svg)](https://github.com/ScoopInstaller/Main/blob/master/bucket/schemacrawler.json)



## SchemaCrawler Command-line

SchemaCrawler comes with a set of command-line tools that allow database
metadata to be output as [plain text,](snapshot-examples/snapshot.txt)
[HTML5,](snapshot-examples/snapshot.html)
[HTML5 with embedded diagrams](snapshot-examples/snapshot.svg.html),
[JavaScript object notation (JSON)](snapshot-examples/snapshot.json) or
[YAML](snapshot-examples/snapshot.yaml).
The HTML5 output is a combination of valid XML (that
can be manipulated by XML tools or XSLT), and HTML that can be viewed in a
browser. All formats are designed to be easy to
[diff](https://en.wikipedia.org/wiki/Diff), or find differences with other
schemas that may have been output in the same format.

SchemaCrawler has [grep](schemacrawler-grep.html) functionality that allows
you to search for table and column names using regular expressions.
SchemaCrawler is capable of creating entity-relationship diagrams in
[DOT format,](https://www.graphviz.org/doc/info/lang.html ) which
[Graphviz](https://www.graphviz.org/) can convert into [schema diagrams.](diagramming.html)
SchemaCrawler has powerful scripting ability,
using JavaScript, Groovy, Ruby or Python. A live connection is provided to the
script context to allow you to select from or even modify your database.
Examples are provided for all of these with the
[download](https://github.com/schemacrawler/SchemaCrawler/releases/).

SchemaCrawler is integrated with templating engines, such
as [Apache Velocity](https://velocity.apache.org/), [Thymeleaf](https://www.thymeleaf.org/),
[Apache &lt;#FreeMarker&gt;](https://freemarker.apache.org/) and [Mustache](https://mustache.github.io/).
You can write templates to generate SQL scripts or any other text output.
However, you will need to download the templating engine separately, since these are
not part of the SchemaCrawler download.

Explore the SchemaCrawler command-line with a [live online tutorial](https://killercoda.com/schemacrawler). 
The tutorial works from within any browser with no software or plugins needed.




## SchemaCrawler API

SchemaCrawler is also a Java API that makes working with database metadata as
easy as working with plain old Java objects. Java programmers need to access
database metadata

- in order to dynamically generate SQL statements
- when programmatically determining the capabilities of a given RDBMS
- when finding the names and types of tables and columns in the database

Programmers can obtain database metadata using JDBC, but with the raw JDBC API
database metadata is returned as result sets, not Java objects. Also,
programmers are still responsible for managing resources, mapping into object
structures, and handling exceptions. This makes using the JDBC API very
cumbersome when it comes to metadata. Furthermore, the JDBC API is not very
consistent. For example, to find the type of a table, you would look at the
`TABLE_TYPE` , which has a string value, but for procedures, `PROCEDURE_TYPE`
is an integer. An another example, is the `getCatalogs()` call, which returns
a result set with exactly one column, in contrast to `getStringFunctions()`
which returns a string containing the list of function names, separated by
commas.

SchemaCrawler attempts to solve some of these problems by providing an API
that is consistent and usable. Database metadata is provided in the form of
plain old Java objects (POJOs). Some examples of the consistency and usability
of the SchemaCrawler API are that:

- `Table` is an object that has a collection of `Column` objects, without
  requiring you to make additional calls
- Booleans are Java booleans, not an integer, a string, or null versus not-null,
  and enumerated values are Java enums, not integers
- Lists are always returned as `java.util.List`
- You don't worry about database resources or exception handling
- You can use standard Java programming idioms - for example, you can access the
  `Table` object from a `Column` object using `getParent()`

SchemaCrawler goes beyond what is available using JDBC, and can provide
information on database triggers, sequences and synonyms as well.

SchemaCrawler is free and open-source API, available under a number of
[licenses](license.html). SchemaCrawler is written
in Java, making it operating system agnostic. Since it leverages JDBC, it is
also database independent. It deliberately doesn't have any RDBMS-specific
code. SchemaCrawler allows you to compare structures between two different
database servers, or even two different database systems, from different
vendors.

SchemaCrawler provides metadata for the following database objects:

* Column data types
* Tables and views
  * Columns
  * Primary keys
  * Indexes
  * Table constraints
  * Triggers
  * Foreign keys
* Routines, including functions and stored procedures
* Sequences
* Synonyms
* Privileges and grants

The sample code below demonstrates just how easy it is to use SchemaCrawler.
For more example code, take a look at the [examples project](https://github.com/schemacrawler/SchemaCrawler/tree/master/schemacrawler-examplecode).
For more details, please refer to the [javadocs](https://javadoc.io/doc/us.fatehi/schemacrawler/).

<script src="https://gist.github.com/schemacrawler/63e4b8cb0515c6e928e7a9a419f46411.js"></script>
More code examples are at [Code Examples Using the SchemaCrawler API](code-examples.html).




## Acknowledgements

SchemaCrawler is grateful to

- [Adrien Sales](https://www.linkedin.com/in/adrien-sales) for developing the ecosystem
- [Atlassian](https://www.atlassian.com/) for providing a license for [Clover](https://www.atlassian.com/software/clover)
