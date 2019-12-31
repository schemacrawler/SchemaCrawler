# SchemaCrawler Installation

## Pre-requisites for Installation

- Install the latest version of [Java]
- Optionally, install [Graphviz] if you want to create database diagrams
- Optionally, install [Apache ant] and [Apache Maven] if you want to try out the examples

## Cross-platform Installation

Installing SchemaCrawler is as simple as unzipping a file. First, download the [SchemaCrawler 
distribution with examples] zip file, and unzip it into a directory. You will have the SchemaCrawler 
distribution in a directory called `_schemacrawler`. There are detailed instructions with the download.

You can extend the SchemaCrawler distribution by downloading additional libraries that SchemaCrawler 
integrates with, such as the pure Java Graphviz library, [Spring Framework], [Groovy], [Ruby], [Python] and so 
on. You can also download additional JDBC drivers, and have SchemaCrawler pick them up automatically. 
Once you are happy with the customization of your installation, you can copy the `_schemacrawler` 
folder to any location on your system, and use it from there.


# Getting Started

## FAQs

Before downloading SchemaCrawler, be sure to read the [FAQs] and take a look at the [resources].

Also read the [Scribe Tools](http://scribetools.readthedocs.org/en/latest/schemacrawler/index.html) 
_Read The Docs_ guide.


## Explore the Command-Line

Explore the SchemaCrawler command-line on [Katacoda](https://www.katacoda.com/schemacrawler).

SchemaCrawler provides detailed command-line help. Simply run `schemacrawler.cmd` (or
`schemacrawler.sh` on Unix) with no command-line arguments for help.

## Examples

The first thing to try is the [SchemaCrawler examples].

Several examples on how to use SchemaCrawler on the command-line, with [Apache ant] or with [Apache 
Maven], as an API, how to script your database using JavaScript, [Groovy], [Ruby] or [Python], how to 
create database diagrams with [Graphviz] and how to use the [Apache Velocity] templating integration 
are provided with the [SchemaCrawler distribution with the examples] download.


## Connecting To Your Database

Read information about [database support] carefully to understand how to connect to your database.

## How-tos

Once you start getting comfortable with SchemaCrawler, and need to know more about how to do 
things, read the [how-tos] section.


# Advanced Topics

## Advanced Installation Steps

After you have worked through the examples, you can make a copy of this `_schemacrawler` directory to 
any location on your hard-disk, and rename the directory to something appropriate. Then, put this 
directory on your `PATH`. Once you open a command shell, you can run SchemaCrawler using 
`schemacrawler.cmd` (or `schemacrawler.sh` on Unix).


## SchemaCrawler Docker Image

You can use the official [SchemaCrawler Docker image] from Docker Hub to reduce some of your 
installation steps. It comes with [Graphviz] pre-installed, so you can generate diagrams. 
For more information, see [information on the Docker image](docker-image.html).

## Tweaking Your Installation
If you install SchemaCrawler to some other location, you can use 
[David Guillot's shell script](https://gist.github.com/David-Guillot/dd53227141fd62ff5db6ef23c929f7b1)
to launch SchemaCrawler

## Building From Source Code

To use SchemaCrawler in your development projects, or to build SchemaCrawler from the source code, read 
about [building].


## Additional Installation Options

Additional installation options are available from the [SchemaCrawler ecosystem](ecosystem.html).


[FAQs]: faq.html
[resources]: resources.html
[how-tos]: how-to.html
[database support]: database-support.html
[building]: building.html
[Java]: https://www.java.com/
[SchemaCrawler examples]: http://github.com/schemacrawler/SchemaCrawler/releases/
[SchemaCrawler distribution with examples]: http://github.com/schemacrawler/SchemaCrawler/releases/
[SchemaCrawler jars]: https://search.maven.org/search?q=g:us.fatehi%20a:schemacrawler*
[SchemaCrawler Docker image]: https://hub.docker.com/r/schemacrawler/schemacrawler/
[Gradle]: https://gradle.org/
[Groovy]: http://www.groovy-lang.org/
[Ruby]: http://www.ruby-lang.org/en/
[Python]: https://www.python.org/
[Graphviz]: http://www.graphviz.org/
[Spring Framework]: http://www.springsource.org/spring-framework
[Apache Velocity]: https://velocity.apache.org/
[Apache Maven]: https://maven.apache.org/
[Apache ant]: https://ant.apache.org/
