# Getting Started

## FAQs
Before downloading SchemaCrawler, be sure to read the [FAQs] and take a look at the [resources].

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

## Installing From the Debian Package
The [Debian schemacrawler package] is maintained by
Adrien Sales <Adrien.Sales at GMail>.

<a href="https://bintray.com/adriens/deb/schemacrawler/_latestVersion">
<img src="https://api.bintray.com/packages/adriens/deb/schemacrawler/images/download.svg" /></a>

Using the command line, add the following to your `/etc/apt/sources.list` system config file: 
<div class="source"><pre>
echo "deb http://dl.bintray.com/adriens/deb {distribution} {components}" | sudo tee -a /etc/apt/sources.list 
</pre></div>
and for source:
<div class="source"><pre>
echo "deb-src http://dl.bintray.com/adriens/deb {distribution} {components}" | sudo tee -a /etc/apt/sources.list
</pre></div>

SchemaCrawler gets installed in `/opt/schemacrawler`.
You can run SchemaCrawler using the `schemacrawler` command, which will be on the PATH.

# Using SchemaCrawler in Your Projects

## Jars from the Central Repository

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/us.fatehi/schemacrawler/badge.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3Aus.fatehi%20a%3Aschemacrawler*)

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
    &lt;version&gt;14.04.04&lt;/version&gt;
  &lt;/dependency&gt;
&lt;/dependencies&gt;
</pre></div>

## Gradle Projects
In order to use SchemaCrawler in your [Gradle](https://gradle.org/) projects, add a dependency to SchemaCrawler in your `build.gradle`.

<div class="source"><pre>
dependencies {
  compile group: 'us.fatehi', name: 'schemacrawler', version: '14.04.04'
}
</pre></div>

## OSGi
The SchemaCrawler jar file is bundled as an [OSGi bundle], and can be deployed into your OSGi Service Platform 
and your OSGi Bundle Repository.

# Building From the Source Code

## Maven Build
The Maven build is a comprehensive build that runs unit tests, constructs the SchemaCrawler jar, and can 
also create the project web-site. Download [Apache Maven] Then run `mvn package` from the SchemaCrawler directory. 
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
