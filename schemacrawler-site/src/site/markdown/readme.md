# Getting Started

## Downloads

### Complete SchemaCrawler distribution

The complete SchemaCrawler distribution with examples.

[![Main distribution](https://img.shields.io/badge/zip-download-brightgreen.svg)](https://github.com/sualeh/SchemaCrawler/releases/latest)

### SchemaCrawler Debian package
The [SchemaCrawler Debian package](http://github.com/adriens/schemacrawler-deb) is maintained by
Adrien Sales <Adrien.Sales at GMail>.

[![Debian package](https://img.shields.io/badge/download-deb-7B2A90.svg)](https://github.com/adriens/schemacrawler-deb/releases/latest)

### SchemaCrawler RPM package
The [SchemaCrawler RPM package](https://github.com/adriens/schemacrawler-rpm) is maintained by
Adrien Sales <Adrien.Sales at GMail>.

[![RPM package](https://img.shields.io/badge/download-rpm-7B2A90.svg)](https://github.com/adriens/schemacrawler-rpm/releases/latest)

### Jars from the Central Repository

SchemaCrawler jars are available from the Maven Central Repository too, for use with build tools
such as Maven, Gradle, and Ivy.

[![Maven Central](https://img.shields.io/maven-central/v/us.fatehi/schemacrawler.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3Aus.fatehi%20schemacrawler)


## FAQs
Before downloading SchemaCrawler, be sure to read the [FAQs] and take a look at the [resources].

Also read the [Scribe Tools](http://scribetools.readthedocs.org/en/latest/schemacrawler/index.html) Read The Docs guide.

## Examples
The first thing to try is the [SchemaCrawler examples].

Several examples on how to use SchemaCrawler on the command-line, with [ant] or with [Maven], 
as an API, how to script your database using JavaScript, [Groovy],
[Ruby] or [Python], how to create database diagrams with [Graphviz], how to integrate with the 
[Spring Framework], and how to use the [Apache Velocity] templating integration are provided with the 
[SchemaCrawler examples] download.

## How-tos
Once you start getting comfortable with SchemaCrawler, and need to know more about how to do things, 
read the [how-tos] section.

## Command-line Help
SchemaCrawler provides detailed command-line help. Simply run `schemacrawler.cmd` (or
`schemacrawler.sh` on Unix) with no command-line arguments for help.

# Installation

## Pre-requisites for Installation

- Install the latest version of [Java](https://www.java.com/)
- Optionally, install [GraphViz](http://www.graphviz.org/), if you want to create database diagrams
- Optionally, install [Apache ant](http://ant.apache.org/) and [Apache Maven](http://maven.apache.org/), 
  if you want to try out the examples

## Cross-platform Install
Installing SchemaCrawler is as simple as unzipping a file. First, 
[download SchemaCrawler](http://github.com/sualeh/SchemaCrawler/releases/),
and unzip it into a directory. You will have an examples folder, and with it, the SchemaCrawler
distribution, in a directory called `_schemacrawler`. You can make a copy of this `_schemacrawler`
directory to any location on your hard-disk, and rename the directory to something appropriate.
Then, put this directory on your PATH. Once you open a command shell, you can run SchemaCrawler
using `schemacrawler.cmd` (or `schemacrawler.sh` on Unix).

SchemaCrawler gets installed in `/opt/schemacrawler`.
You can run SchemaCrawler using the `schemacrawler` command, which will be on the PATH.

# Using SchemaCrawler in Your Projects

## Jars from the Central Repository

[![Maven Central](https://img.shields.io/maven-central/v/us.fatehi/schemacrawler.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3Aus.fatehi%20schemacrawler)

[All of the SchemaCrawler jars](http://search.maven.org/#search%7Cga%7C1%7Cg%3Aus.fatehi%20a%3Aschemacrawler*) 
are available on the Maven Central Repository. 
They can be used as dependencies in [Gradle](https://gradle.org/) or [Apache Maven] projects, or with any other
build system that supports the Central Repository.

## Maven Projects
In order to use SchemaCrawler in your [Apache Maven] projects, add a dependency to SchemaCrawler in your `pom.xml`.

<div class="source"><pre>
&lt;dependencies&gt;
  ...
  &lt;dependency&gt;
    &lt;groupId&gt;us.fatehi&lt;/groupId&gt;
    &lt;artifactId&gt;schemacrawler&lt;/artifactId&gt;
    &lt;version&gt;14.10.01&lt;/version&gt;
  &lt;/dependency&gt;
&lt;/dependencies&gt;
</pre></div>

## Gradle Projects
In order to use SchemaCrawler in your [Gradle](https://gradle.org/) projects, add a dependency to SchemaCrawler in your `build.gradle`.

<div class="source"><pre>
dependencies {
  compile group: 'us.fatehi', name: 'schemacrawler', version: '14.10.01'
}
</pre></div>

## OSGi
The SchemaCrawler jar file is bundled as an [OSGi bundle], and can be deployed into your OSGi Service Platform 
and your OSGi Bundle Repository.

# Building From the Source Code

## Maven Build
The Maven build is a comprehensive build that runs unit tests, constructs the SchemaCrawler jar, and can also create the project web-site. 

- Install [Apache Maven], and make sure it is on your classpath 
- Open a command console in the SchemaCrawler directory
- Run `mvn install:install-file -DgroupId=org.xerial -DartifactId=sqlite-jdbc -Dversion=3.7.8 -Dfile=./schemacrawler-sqlite/sqlite-jdbc-3.7.8.jar -Dpackaging=jar -DgeneratePom=true`
- Then run `mvn package` from the SchemaCrawler directory

The SchemaCrawler distribution will be created in the `target` of the `distribution` module.

## Eclipse
Before importing the various SchemaCrawler projects into Eclipse, make sure that you have a successful 
Maven build. Maven will download all the dependencies needed to build SchemaCrawler. Also, install the 
[m2e Maven Integration for Eclipse] plugin.

## Proprietary JDBC Drivers
The Maven build depends on some proprietary JDBC drivers for IBM DB2 and Oracle. Download the [IBM DB2 JDBC drivers] 
place them in the schemacrawler-db2 source directory, and install them into your local Maven repository using 
the provided install command. Similarly, download the [Oracle JDBC drivers] put them into the schemacrawler-oracle 
source directory, and install them locally.

# Acknowledgements

- SchemaCrawler is grateful to Adrien Sales for developing the [Debian schemacrawler package].
- SchemaCrawler is grateful to Atlassian for providing a license for [Clover].

[Debian schemacrawler package]: http://github.com/adriens/schemacrawler-deb
[FAQs]: faq.html
[resources]: resources.html
[SchemaCrawler examples]: http://github.com/sualeh/SchemaCrawler/releases/
[ant]: http://ant.apache.org/
[Maven]: http://maven.apache.org/
[Groovy]: http://groovy.codehaus.org/
[Ruby]: http://www.ruby-lang.org/en/
[Python]: http://www.python.org/
[Graphviz]: http://www.graphviz.org/
[Spring Framework]: http://www.springsource.org/spring-framework
[Apache Velocity]: http://velocity.apache.org/
[SchemaCrawler examples]: http://github.com/sualeh/SchemaCrawler/releases/
[Apache Maven]: http://maven.apache.org/
[OSGi bundle]: http://en.wikipedia.org/wiki/OSGi#Bundles
[m2e Maven Integration for Eclipse]: http://eclipse.org/m2e/
[IBM DB2 JDBC drivers]: http://www-306.ibm.com/software/data/db2/express/download.html
[Oracle JDBC drivers]: http://www.oracle.com/technetwork/database/enterprise-edition/jdbc-112010-090769.html
[Clover]: http://www.atlassian.com/software/clover/
[how-tos]: how-to.html
