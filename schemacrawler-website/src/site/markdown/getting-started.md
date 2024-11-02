# Getting Started with SchemaCrawler

## SchemaCrawler Installation

### Pre-requisites for Installation

- Install the latest version of [Java](https://www.oracle.com/java/technologies/)
- Optionally, install [Graphviz] if you want to create schema diagrams
- Optionally, install [Apache ant] and [Apache Maven] if you want to try out the examples

### Cross-platform Installation

Installing SchemaCrawler is as simple as unzipping a file. First, download the [SchemaCrawler
distribution with examples] zip file, and unzip it into a directory. You will have the SchemaCrawler
distribution in a directory called `_schemacrawler`. There are detailed instructions with the download.

You can extend the SchemaCrawler distribution by downloading additional libraries that SchemaCrawler
integrates with, such as the pure Java Graphviz library, [Spring Framework](https://spring.io/projects/spring-framework), [Groovy], [Ruby], [Python] and so
on. You can also download additional JDBC drivers, and have SchemaCrawler pick them up automatically.
Once you are happy with the customization of your installation, you can copy the `_schemacrawler`
folder to any location on your system, and use it from there.



## Getting Started

### FAQs

Before downloading SchemaCrawler, be sure to read the [FAQs](faq.html) and take a look at the [resources](resources.html).

Watch the video on [How to explore a new database](https://dev.to/sualeh/how-do-you-explore-a-new-database-1pge) to understand the power of SchemaCrawler, and to give you some ideas of how to use it. Also read [Explore Your Database Schema with SchemaCrawler](https://dev.to/sualeh/explore-your-database-schema-with-schemacrawler-5341), [How to Get Database Metadata as Java POJOs](https://dev.to/sualeh/how-to-get-database-metadata-as-java-pojos-24li) and [Lint Your Database Schema With GitHub Actions Workflows](https://dev.to/sualeh/lint-your-database-schema-with-github-actions-workflows-57cg).

SchemaCrawler can [generate diagrams of your database schema](diagramming.html), and export them to other tools. Take a look at [How to Generate dbdiagram.io Diagrams for Your Database](https://dev.to/sualeh/how-to-generate-dbdiagram-io-diagrams-for-your-database-431l)
and [How to Generate Mermaid Diagrams for Your Database](https://dev.to/sualeh/how-to-generate-mermaid-diagrams-for-your-database-33bn) to see how you can continue to evolve your database design. If you want continuous ingtegration, see how you can [Generate Database Diagrams With GitHub Actions Workflows](https://dev.to/sualeh/generate-database-diagrams-with-github-actions-workflows-4l96).

And finally, here are some other quick getting started articles:

- [How to Visualize Your MySql Database with One Command (and Nothing to Install)](https://dev.to/sualeh/how-to-visualize-your-mysql-database-with-one-command-and-nothing-to-install-21cp)
- [How to Visualize Your PostgreSQL Database with One Command (and Nothing to Install)](https://dev.to/sualeh/how-to-visualize-your-postgresql-database-with-one-command-and-nothing-to-install-3e3j)
- [How to Visualize Your SQLite Database with One Command (and Nothing to Install)](https://dev.to/sualeh/how-to-visualize-your-sqlite-database-with-one-command-and-nothing-to-install-1f4m)
- [Automatically Document Your Database in Markdown](https://dev.to/sualeh/automatically-document-your-database-in-markdown-elf)


### Explore the Command-Line

Explore the SchemaCrawler command-line with a [live online tutorial](https://killercoda.com/schemacrawler). 
The tutorial works from within any browser with no software or plugins needed.

SchemaCrawler provides detailed command-line help. Simply run `schemacrawler.cmd` (or
`schemacrawler.sh` on Unix) with no command-line arguments for help.


### Examples

The first thing to try is the [SchemaCrawler examples].

Several examples on how to use SchemaCrawler on the command-line, with [Apache ant] or with
[Apache Maven], as an API, how to script your database using JavaScript, [Groovy], [Ruby] or [Python], how to
create database diagrams with [Graphviz] and how to use the [Apache Velocity](https://velocity.apache.org/) templating integration
are provided with the [SchemaCrawler distribution with the examples] download.


### Connecting To Your Database

Read information about [database support](database-support.html) carefully to understand how to connect to your database.


### How-tos

Once you start getting comfortable with SchemaCrawler, and need to know more about how to do
things, read the [how-tos](how-to.html) section.



## Advanced Topics

### Advanced Installation Steps

After you have worked through the examples, you can make a copy of this `_schemacrawler` directory to
any location on your hard-disk, and rename the directory to something appropriate. Then, put this
directory on your `PATH`. Once you open a command shell, you can run SchemaCrawler using
`schemacrawler.cmd` (or `schemacrawler.sh` on Unix).

### Configuration

SchemaCrawler offers rich configuration options. Read about them on the [SchemaCrawler Configuration](config.html) page.


### SchemaCrawler Docker Image

You can use the official [SchemaCrawler Docker image](https://hub.docker.com/r/schemacrawler/schemacrawler/) from Docker Hub to reduce some of your
installation steps. It comes with [Graphviz] pre-installed, so you can generate schema diagrams.
For more information, see [information on the Docker image](docker-image.html).


### Tweaking Your Installation
If you install SchemaCrawler to some other location, you can use
[David Guillot's shell script](https://gist.github.com/David-Guillot/dd53227141fd62ff5db6ef23c929f7b1)
to launch SchemaCrawler


### Building From Source Code

To use SchemaCrawler in your development projects, or to build SchemaCrawler from the source code, read
about [building](building.html).


### Additional Installation Options

Additional [download and installation options](downloads.html) are available.



[SchemaCrawler examples]: https://www.schemacrawler.com/downloads.html#running-examples-locally/
[Groovy]: https://www.groovy-lang.org/
[Ruby]: https://www.ruby-lang.org/en/
[Python]: https://www.python.org/
[Graphviz]: https://www.graphviz.org/
[Apache Maven]: https://maven.apache.org/
[Apache ant]: https://ant.apache.org/
