<!-- markdownlint-disable MD024 -->
# SchemaCrawler - Frequently Asked Questions

## General

1. [What is SchemaCrawler?](#whats-schemacrawler)
2. [What does SchemaCrawler output look like?](#output)
3. [What are the SchemaCrawler commands?](#commands)
4. [What does a SchemaCrawler configuration file look like?](#config)

## Supported Platforms

1. [What databases does SchemaCrawler work with?](#supported-databases)
2. [What Java version does SchemaCrawler work with?](#supported-java)
3. [What operating systems does SchemaCrawler work with?](#supported-os)
4. [Can SchemaCrawler be used with ant?](#ant)
5. [Can SchemaCrawler be used with Apache Maven?](#maven)

## SchemaCrawler Database Diagrams

1. [What is a SchemaCrawler database diagram?](#diagrams)
2. [What does a SchemaCrawler database diagram look like?](#diagram-output)

## SchemaCrawler Grep

1. [What is SchemaCrawler Grep?](#whats-schemacrawler-grep)
2. [What does SchemaCrawler Grep output look like?](#grep-output)
3. [What are the SchemaCrawler Grep commands?](#grepcommands)
4. [What does a SchemaCrawler configuration file look like?](#config)

## SchemaCrawler Features

1. [What is SchemaCrawler Lint?](#schema-lint)
2. [What are weak associations?](#weak-associations)
3. [Can SchemaCrawler be used with scripting languages?](#scripting)
4. [Can SchemaCrawler be used programmatically?](#api)
5. [Can SchemaCrawler be used integrated with other programs?](#integrations)

## Distribution

1. [Where is SchemaCrawler available?](#availability )
2. [Is technical support available?](#tech-support)
3. [Is SchemaCrawler free?](#cost)


## General

---------

### <a name="whats-schemacrawler">What is SchemaCrawler?</a>

SchemaCrawler is an open-source Java API that makes working with database metadata as easy as working with plain old Java objects.
SchemaCrawler is also a command-line tool to output your database schema and data in a readable form. The output is designed to be [diff-ed](https://en.wikipedia.org/wiki/Diff) with previous versions of your database schema.

---------

### <a name="output">What does SchemaCrawler output look like?</a>

SchemaCrawler can produce [plain text,](snapshot-examples/snapshot.txt)
[HTML5,](snapshot-examples/snapshot.html)
[HTML5 with embedded diagrams](snapshot-examples/snapshot.svg.html),
[JavaScript object notation (JSON)](snapshot-examples/snapshot.json) or
[YAML](snapshot-examples/snapshot.yaml) output. The HTML5 output is a combination of valid XML (that can be manipulated by XML tools or XSLT), and HTML that can be viewed in a browser. SchemaCrawler can produce output in the [DOT format,](https://www.graphviz.org/doc/info/lang.html ) which [Graphviz](https://www.graphviz.org/) can convert into [schema diagrams.](diagramming.html)

---------

### <a name="commands">What are the SchemaCrawler commands?</a>

Explore the SchemaCrawler command-line with a [live online tutorial](https://killercoda.com/schemacrawler). 
The tutorial works from within any browser with no software or plugins needed.

Then download and install SchemaCrawler, and run the provided scripts (`schemacrawler.sh` on Unix, and `schemacrawler.cmd` on Windows). Detailed command-line help will be displayed.

SchemaCrawler can produce listings, and standard, or verbose details of your schema. The brief schema option gives you just table, view, stored procedure and function names. The standard schema option gives you the column names and primary keys. The verbose schema option will give you details of data types, indexes, primary and foreign keys, view, stored procedure and function definitions. The SchemaCrawler command can be combined with an _info-level_, which determines the level of detail of schema metadata obtained. The maximum _info-level_ will give you privileges, triggers definitions, and check constraints. Each successive option takes more time to execute.

SchemaCrawler provides commands for producing schema diagrams and running templates, if the appropriate extensions are installed.

SchemaCrawler can also manipulate your data. You can get counts of the rows in tables and views. SchemaCrawler can output all the data in your tables and views, or run specific SQL statements against table and views. SchemaCrawler can be fine-tuned using configuration files.

---------

### <a name="config">What does a SchemaCrawler configuration file look like?</a>

An example of a [SchemaCrawler configuration file.](config/schemacrawler.config.properties)

---------


## Supported Platforms

---------

### <a name="supported-databases">What databases does SchemaCrawler work with?</a>

SchemaCrawler supports any database for which there is a JDBC database driver available. SchemaCrawler has been tested with various databases, and JDBC drivers. For more information, see [Database System Support.](database-support.html)

---------

### <a name="supported-java">What Java version does SchemaCrawler work with?</a>

SchemaCrawler works with Java SE 17 or better.

---------

### <a name="supported-os">What operating systems does SchemaCrawler work with?</a>

SchemaCrawler works with any operating system that supports Java SE 17 or better.

---------

### <a name="ant">Can SchemaCrawler be used with ant?</a>

Yes, the SchemaCrawler command-line can be called from ant, using the exec task.

---------

### <a name="maven">Can SchemaCrawler be used with Apache Maven?</a>

Yes SchemaCrawler can be used in a Apache Maven project as an API, or to generate Apache Maven reports in your projects. In order to use SchemaCrawler in your Apache Maven projects, simply add a dependency to SchemaCrawler in your pom.xml.
_For more details, see the [plugins](plugins.html) page._

Using the [SchemaCrawler Report Maven Plugin](https://github.com/schemacrawler/SchemaCrawler-Report-Maven-Plugin), 
you can generate a SchemaCrawler report along with other reports for your Apache Maven generated site.

---------


## SchemaCrawler Database Diagrams

---------

### <a name="diagrams">What is a SchemaCrawler database diagram?</a>

SchemaCrawler can generate entity-relationship database diagrams using [Graphviz](https://www.graphviz.org/) . You can filter out tables, columns, stored procedure and functions based on regular expressions, using the [grep](#whats-schemacrawler-grep) functionality.
_For more details, see the [diagramming](diagramming.html) page._

---------

### <a name="diagram-output">What does a SchemaCrawler database diagram look like?</a>

An example of a SchemaCrawler database diagram:
[![An example of a SchemaCrawler database diagram](diagram-examples/diagram.png)](diagram-examples/diagram.png "SchemaCrawler database diagram")

---------

## SchemaCrawler Grep

---------

### <a name="whats-schemacrawler-grep">What is SchemaCrawler Grep?</a>

SchemaCrawler Grep is a set of SchemaCrawler command-line options that allow you to search your database schema for tables and columns that match a regular expression, much like the standard [grep](https://en.wikipedia.org/wiki/Grep) tool.

---------

### <a name="grep-output">What does SchemaCrawler Grep output look like?</a>

SchemaCrawler Grep output is the same as the SchemaCrawler text output.

---------

### <a name="grepcommands">What are the SchemaCrawler Grep commands?</a>

See the [filtering and grep command-line options](#commands) above.

---------

### <a name="config">What does a SchemaCrawler configuration file look like?</a>

The SchemaCrawler Grep configuration file is the same as the [SchemaCrawler configuration file.](config/schemacrawler.config.properties)

---------


## SchemaCrawler Features

---------

### <a name="schema-lint">What is SchemaCrawler Lint?</a>

SchemaCrawler can analyze and [lint](https://en.wikipedia.org/wiki/Lint_(software)) your database schema design to find potential issues. SchemaCrawler Lint can be run using the `--command=lint` command-line option.
_For more details, look at the [SchemaCrawler Lint](lint.html) page._

---------

### <a name="weak-associations">What are weak associations?</a>

Weak associations are inferred associations between tables, similar to foreign keys, even if there is no foreign key defined in the database schema between the tables.
_For more details, look at the [Weak Associations](weak-associations.html) page._

---------

### <a name="scripting">Can SchemaCrawler be used with scripting languages?</a>

SchemaCrawler has built-in support to be used with Python or JavaScript scripts.

Also, look at the [scripting](scripting.html) page.

_For more details, see scripting examples in the [SchemaCrawler examples](https://www.schemacrawler.com/downloads.html#running-examples-locally/) download, in the `examples\javascript`, and `examples\python` directories._

---------

### <a name="api">Can SchemaCrawler be used programmatically?</a>

SchemaCrawler is an API that improves on the standard JDBC metadata facilities. SchemaCrawler provides an easy to use set of plain old Java objects (POJOs) that represent your database schema.

Read [How to Get Database Metadata as Java POJOs](https://dev.to/sualeh/how-to-get-database-metadata-as-java-pojos-24li), and browse the [javadocs](https://javadoc.io/doc/us.fatehi/schemacrawler/).

_For more details, see scripting example in the [SchemaCrawler examples](https://www.schemacrawler.com/downloads.html#running-examples-locally/) download, in the `examples\api` directory._

---------

### <a name="integrations">Can SchemaCrawler be used integrated with other programs?</a>

SchemaCrawler can be integrated with other programs, but this requires some Java programming. SchemaCrawler is designed to be used programmatically with the [Spring Framework](https://spring.io/) . 

SchemaCrawler comes pre-built with integration with [Apache Velocity.](https://velocity.apache.org/) This allows you to specify your own templates for formatting the schema. For more details, see scripting example in the [SchemaCrawler examples](https://www.schemacrawler.com/downloads.html#running-examples-locally/) download, in the `examples\velocity` directory.


</dl>


## Distribution

### <a name="availability ">Where is SchemaCrawler available?</a>

SchemaCrawler is available as a download from [GitHub](https://www.schemacrawler.com/downloads.html#running-examples-locally) .

---------

### <a name="tech-support">Is technical support available?</a>

Yes. Please see [Support and Consulting](consulting.html) for details.

---------

### <a name="cost">Is SchemaCrawler free?</a>

See [SchemaCrawler license](license.html) for details.
