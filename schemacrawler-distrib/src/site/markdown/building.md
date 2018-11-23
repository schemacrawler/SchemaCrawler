# Using SchemaCrawler in Projects and Building SchemaCrawler

## Pre-requisites for Building

- Install the latest version of [Java](https://www.java.com/)
- Install [Graphviz], and put it on your `PATH`

## SchemaCrawler Docker Image
The official [SchemaCrawler Docker image] is available on Docker Hub.


# Using SchemaCrawler in Your Projects

## Jars from the Central Repository
All of the [SchemaCrawler jars] are available on The Central Repository. 
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
    &lt;version&gt;15.02.01&lt;/version&gt;
  &lt;/dependency&gt;
&lt;/dependencies&gt;
</pre></div>

## Gradle Projects
In order to use SchemaCrawler in your [Gradle] projects, add a dependency to SchemaCrawler in your `build.gradle`.

<div class="source"><pre>
dependencies {
  compile group: 'us.fatehi', name: 'schemacrawler', version: '15.02.01'
}
</pre></div>


# Building From the Source Code

## Apache Maven Build
The [Apache Maven] build is a comprehensive build that runs unit tests, constructs the 
SchemaCrawler jar, and can also create the project web-site. 

- Install [Apache Maven], and make sure it is on your classpath 
- Open a command console in the SchemaCrawler `schemacrawler-parent` directory
- Run `mvn package` from the SchemaCrawler `schemacrawler-parent` directory

The SchemaCrawler distribution will be created in the `target` of the `schemacrawler-distrib` 
directory.

## Eclipse
Before importing the various SchemaCrawler projects into [Eclipse], make sure that you have a successful [Apache Maven] build. [Apache Maven] will download all the dependencies needed to build SchemaCrawler. 

## Proprietary JDBC Drivers
The Apache Maven build depends on some proprietary JDBC drivers for IBM DB2 and Oracle. 
Download the [IBM DB2 JDBC drivers] place them in the `schemacrawler-db2` source directory, 
and install them into your local Apache Maven repository using the provided install command. 
Similarly, download the [Oracle JDBC drivers] put them into the `schemacrawler-oracle` source 
directory, and install them locally.


[Java]: https://www.java.com/
[Eclipse]: http://www.eclipse.org/downloads/eclipse-packages/
[SchemaCrawler examples]: http://github.com/schemacrawler/SchemaCrawler/releases/
[SchemaCrawler jars]: https://search.maven.org/search?q=g:us.fatehi%20a:schemacrawler* 
[SchemaCrawler Docker image]: https://hub.docker.com/r/schemacrawler/schemacrawler/
[Apache ant]: http://ant.apache.org/
[Gradle]: https://gradle.org/
[Groovy]: http://www.groovy-lang.org/
[Ruby]: http://www.ruby-lang.org/en/
[Python]: https://www.python.org/
[Graphviz]: http://www.graphviz.org/
[Spring Framework]: http://www.springsource.org/spring-framework
[Apache Velocity]: http://velocity.apache.org/
[Apache Maven]: http://maven.apache.org/
[m2e Maven Integration for Eclipse]: http://eclipse.org/m2e/
[IBM DB2 JDBC drivers]: http://www-306.ibm.com/software/data/db2/express/download.html
[Oracle JDBC drivers]: http://www.oracle.com/technetwork/database/enterprise-edition/jdbc-112010-090769.html
[Clover]: http://www.atlassian.com/software/clover/
[how-tos]: how-to.html
