# Getting Started

## Downloads

### Complete SchemaCrawler Distribution
The complete [SchemaCrawler examples] distribution in a zip file is available on the SchemaCrawler GitHub page.

### SchemaCrawler Jars from the Central Repository
[SchemaCrawler jars] are available from the Maven Central Repository, for use with build tools
such as [Apache Maven], [Gradle], and Ivy.

### SchemaCrawler Docker Image
The official [SchemaCrawler Docker image] is available on Docker Hub.
The [Dockerfile is on GitHub](https://github.com/sualeh/SchemaCrawler-Docker).

## FAQs
Before downloading SchemaCrawler, be sure to read the [FAQs] and take a look at the [resources].

Also read the [Scribe Tools](http://scribetools.readthedocs.org/en/latest/schemacrawler/index.html) Read The Docs guide.

## Examples
The first thing to try is the [SchemaCrawler examples].

Several examples on how to use SchemaCrawler on the command-line, with [Apache ant] or with [Apache Maven], 
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
- Optionally, install [Graphviz], if you want to create database diagrams
- Optionally, install [Apache ant] and [Apache Maven], 
  if you want to try out the examples

## Cross-platform Install
Installing SchemaCrawler is as simple as unzipping a file. First, download the [SchemaCrawler examples] zip file,
and unzip it into a directory. You will have an examples folder, and with it, the SchemaCrawler
distribution, in a directory called `_schemacrawler`. You can make a copy of this `_schemacrawler`
directory to any location on your hard-disk, and rename the directory to something appropriate.
Then, put this directory on your PATH. Once you open a command shell, you can run SchemaCrawler
using `schemacrawler.cmd` (or `schemacrawler.sh` on Unix).

SchemaCrawler gets installed in `/opt/schemacrawler`.
You can run SchemaCrawler using the `schemacrawler` command, which will be on the PATH.

## SchemaCrawler Docker Image
The official [SchemaCrawler Docker image](https://hub.docker.com/r/sualeh/schemacrawler/) is available on Docker Hub, 
and using this will reduce some of your installation steps.

## Tweaking Your Installation

- If you install SchemaCrawler to some other location, you can use 
  [David Guillot's shell script](https://gist.github.com/David-Guillot/dd53227141fd62ff5db6ef23c929f7b1)
  to launch SchemaCrawler

# Using SchemaCrawler in Your Projects

## Jars from the Central Repository
All of the [SchemaCrawler jars] are available on the Maven Central Repository. 
They can be used as dependencies in [Gradle] or [Apache Maven] projects, or with any other
build system that supports the Central Repository.

## Apache Maven Projects
In order to use SchemaCrawler in your [Apache Maven] projects, add a dependency to SchemaCrawler in your `pom.xml`.

<div class="source"><pre>
&lt;dependencies&gt;
  ...
  &lt;dependency&gt;
    &lt;groupId&gt;us.fatehi&lt;/groupId&gt;
    &lt;artifactId&gt;schemacrawler&lt;/artifactId&gt;
    &lt;version&gt;14.14.04&lt;/version&gt;
  &lt;/dependency&gt;
&lt;/dependencies&gt;
</pre></div>

## Gradle Projects
In order to use SchemaCrawler in your [Gradle] projects, add a dependency to SchemaCrawler in your `build.gradle`.

<div class="source"><pre>
dependencies {
  compile group: 'us.fatehi', name: 'schemacrawler', version: '14.14.04'
}
</pre></div>

## OSGi
The SchemaCrawler jar file is bundled as an [OSGi bundle], and can be deployed into your OSGi Service Platform 
and your OSGi Bundle Repository.

# Building From the Source Code

## Apache Maven Build
The Maven build is a comprehensive build that runs unit tests, constructs the SchemaCrawler jar, 
and can also create the project web-site. 

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

- SchemaCrawler is grateful to Adrien Sales for developing the [Debian SchemaCrawler package].
- SchemaCrawler is grateful to Atlassian for providing a license for [Clover].

[FAQs]: faq.html
[resources]: resources.html
[SchemaCrawler examples]: http://github.com/sualeh/SchemaCrawler/releases/
[SchemaCrawler jars]: http://search.maven.org/#search%7Cga%7C1%7Cg%3Aus.fatehi%20schemacrawler 
[SchemaCrawler Docker image]: https://hub.docker.com/r/sualeh/schemacrawler/
[Apache ant]: http://ant.apache.org/
[Gradle]: https://gradle.org/
[Groovy]: http://www.groovy-lang.org/
[Ruby]: http://www.ruby-lang.org/en/
[Python]: https://www.python.org/
[Graphviz]: http://www.graphviz.org/
[Spring Framework]: http://www.springsource.org/spring-framework
[Apache Velocity]: http://velocity.apache.org/
[Apache Maven]: http://maven.apache.org/
[OSGi bundle]: http://en.wikipedia.org/wiki/OSGi#Bundles
[m2e Maven Integration for Eclipse]: http://eclipse.org/m2e/
[IBM DB2 JDBC drivers]: http://www-306.ibm.com/software/data/db2/express/download.html
[Oracle JDBC drivers]: http://www.oracle.com/technetwork/database/enterprise-edition/jdbc-112010-090769.html
[Clover]: http://www.atlassian.com/software/clover/
[how-tos]: how-to.html
[Debian SchemaCrawler package]: https://github.com/adriens/schemacrawler-deb
