# SchemaCrawler

> SchemaCrawler is free and open-source software. 
> [Donations are appreciated.](https://www.paypal.me/sualeh)  

SchemaCrawler is a free database schema discovery and comprehension tool.
SchemaCrawler has a good mix of useful features for data governance. You can
[search for database schema objects](schemacrawler_grep.html) using regular
expressions, and output the schema and data in a readable text format. The
output serves for database documentation, and is designed to be 
[diff-ed](http://en.wikipedia.org/wiki/Diff) against other database schemas.
SchemaCrawler also generates [schema diagrams.](diagramming.html) You can
[execute scripts](scripting.html) in any standard scripting language against
your database. You can find potential schema design issues with
[lint.](lint.html)

SchemaCrawler supports almost any database that has a JDBC driver, but for
convenience is [bundled with drivers](database-support.html) for some commonly used
RDBMS systems. SchemaCrawler works with any operating system that supports
Java SE 8, Compact Profile 2 or better.

[![Build Status](https://travis-ci.org/sualeh/SchemaCrawler.svg?branch=master)](https://travis-ci.org/sualeh/SchemaCrawler)
[![Coverage Status](https://coveralls.io/repos/sualeh/SchemaCrawler/badge.svg?branch=master&service=github)](https://coveralls.io/github/sualeh/SchemaCrawler?branch=master)
[![The Central Repository](https://img.shields.io/maven-central/v/us.fatehi/schemacrawler.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3Aus.fatehi%20schemacrawler)
[![Main distribution](https://img.shields.io/badge/zip-download-brightgreen.svg)](https://github.com/sualeh/SchemaCrawler/releases/latest)
[![Docker Pulls](https://img.shields.io/docker/pulls/sualeh/schemacrawler.svg)](https://hub.docker.com/r/sualeh/schemacrawler/)
[![Stack Overflow](https://img.shields.io/stackexchange/stackoverflow/t/schemacrawler.svg)](http://stackoverflow.com/search?tab=newest&q=schemacrawler)

-----

## SchemaCrawler Command-line

SchemaCrawler comes with a set of command-line tools that allow database
metadata to be output as [plain text,](snapshot-examples/snapshot.text) 
[comma-separated text (CSV),](snapshot-examples/snapshot.csv) 
[HTML5,](snapshot-examples/snapshot.html)
[HTML5 with embedded diagrams,](snapshot-examples/snapshot.svg.html) or 
[JavaScript object notation (JSON).](snapshot-examples/snapshot.json) 
The HTML5 output is a combination of valid XML (that
can be manipulated by XML tools or XSLT), and HTML that can be viewed in a
browser. All formats are designed to be easy to
[diff](http://en.wikipedia.org/wiki/Diff) , or find differences with other
schemas that may have been output in the same format.

SchemaCrawler has [grep](schemacrawler_grep.html) functionality that allows
you to search for table and column names using regular expressions.
SchemaCrawler is capable of creating entity-relationship diagrams in 
[DOT format,](http://www.graphviz.org/doc/info/lang.html ) which
[Graphviz](http://www.graphviz.org/) can convert into [schema diagrams.](diagramming.html) 
SchemaCrawler has powerful scripting ability,
using JavaScript, Groovy, Ruby or Python. A live connection is provided to the
script context to allow you to select from or even modify your database.
Examples are provided for all of these with the
[download](http://github.com/sualeh/SchemaCrawler/releases/).

SchemaCrawler is integrated with, and allows you to write templates to
generate SQL scripts or any other text output, using templating engines, such
as [Apache Velocity](http://velocity.apache.org/), [Thymeleaf](http://www.thymeleaf.org/) or
[&lt;FreeMarker&gt;](http://freemarker.org/) . However, you will need to download
Apache Velocity or &lt;FreeMarker&gt; separately, since these are not part of the
SchemaCrawler download.

Complete SchemaCrawler command-line help is available with the -h or -help command-line
options.

-----

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
- Lists are always Java lists - `java.util.List`
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

The sample code below demonstrates just how easy it is to use SchemaCrawler:

<script src="https://gist.github.com/sualeh/63e4b8cb0515c6e928e7a9a419f46411.js"></script>
        
For more details, please refer to the [javadocs](apidocs/index.html).
