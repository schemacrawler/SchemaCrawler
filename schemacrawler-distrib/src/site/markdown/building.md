# Programming with SchemaCrawler

## Using SchemaCrawler in Your Projects

### Jars from the Central Repository
All of the [SchemaCrawler jars] are available on The Central Repository. 
They can be used as dependencies in [Gradle] or [Apache Maven] projects, or with any other
build system that supports the Central Repository.

### Apache Maven Projects
In order to use SchemaCrawler in your [Apache Maven] projects, add a dependency to SchemaCrawler in your `pom.xml`.

```xml
<dependencies>
  ...
  <dependency>
    <groupId>us.fatehi</groupId>
    <artifactId>schemacrawler</artifactId>
    <version>16.3.0</version>
  </dependency>
</dependencies>
```

### Gradle Projects
In order to use SchemaCrawler in your [Gradle] projects, add a dependency to SchemaCrawler in your `build.gradle`.

```groovy
dependencies {
  compile group: 'us.fatehi', name: 'schemacrawler', version: '16.3.0'
}
```

### SchemaCrawler Docker Image
The official [SchemaCrawler Docker image] is available on Docker Hub. 
The SchemaCrawler command-line and the interactive shell are available in the image, 
with open-source JDBC drivers.


## Building From the Source Code

### Pre-requisites for Building
- Install the latest version of [Java](https://www.java.com/)
- Install [Graphviz], and put it on your system `PATH`

## Apache Maven Build
The [Apache Maven] build is a comprehensive build that runs unit tests, constructs the 
SchemaCrawler jar, and can also create the project web-site. 

- Install [Apache Maven], and make sure it is on your classpath 
- Open a command console in the SchemaCrawler `schemacrawler-parent` directory
- Run `mvn package` from the SchemaCrawler `schemacrawler-parent` directory

You can create the  SchemaCrawler distribution by running `mvn -Dcomplete package`. The SchemaCrawler distribution will be created in the `target` of the `schemacrawler-distrib` module.

## Eclipse and IntelliJ IDEA
Before importing the various SchemaCrawler projects into [Eclipse] or [IntelliJ IDEA], make sure that you have a successful [Apache Maven] build. [Apache Maven] will download all the dependencies needed to build SchemaCrawler. 

## Proprietary JDBC Drivers
The Apache Maven build depends on some proprietary JDBC drivers for IBM DB2 and Oracle. 
Download the [IBM DB2 JDBC drivers] place them in the `schemacrawler-db2` source directory, and install them into your local Apache Maven repository using the provided install command. 
Similarly, download the [Oracle JDBC drivers] put them into the `schemacrawler-oracle` source directory, and install them locally.


[Java]: https://www.java.com/
[Eclipse]: http://www.eclipse.org/downloads/eclipse-packages/
[IntelliJ IDEA]: https://www.jetbrains.com/idea/download/
[SchemaCrawler examples]: http://github.com/schemacrawler/SchemaCrawler/releases/
[SchemaCrawler jars]: https://search.maven.org/search?q=g:us.fatehi%20a:schemacrawler*
[SchemaCrawler Docker image]: https://hub.docker.com/r/schemacrawler/schemacrawler/
[Apache ant]: http://ant.apache.org/
[Gradle]: https://gradle.org/
[Groovy]: http://www.groovy-lang.org/
[Ruby]: http://www.ruby-lang.org/en/
[Python]: https://www.python.org/
[Graphviz]: http://www.graphviz.org/
[Apache Velocity]: http://velocity.apache.org/
[Apache Maven]: http://maven.apache.org/
[m2e Maven Integration for Eclipse]: http://eclipse.org/m2e/
[IBM DB2 JDBC drivers]: http://www-306.ibm.com/software/data/db2/express/download.html
[Oracle JDBC drivers]: http://www.oracle.com/technetwork/database/enterprise-edition/jdbc-112010-090769.html
[Clover]: http://www.atlassian.com/software/clover/
[how-tos]: how-to.html
