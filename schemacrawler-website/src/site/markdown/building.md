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
    <version>17.2.8</version>
  </dependency>
</dependencies>
```

### Gradle Projects
In order to use SchemaCrawler in your [Gradle] projects, add a dependency to SchemaCrawler in your `build.gradle`.

```groovy
dependencies {
  compile group: 'us.fatehi', name: 'schemacrawler', version: '17.2.8'
}
```

### SchemaCrawler Docker Image
The official [SchemaCrawler Docker image] is available on Docker Hub.
The SchemaCrawler command-line and the interactive shell are available in the image,
with open-source or freely distributable JDBC drivers.


## Building From the Source Code

### Pre-requisites for Building
- Install the latest version of [Java]
- Install [Graphviz], and put it on your system `PATH`

## Apache Maven Build
The [Apache Maven] build is a comprehensive build that runs unit tests, constructs the
SchemaCrawler jar, and can also create the project web-site.

- Install [Apache Maven], and make sure it is on your classpath
- Open a command console in the SchemaCrawler directory
- Run `mvn package`

You can create the  SchemaCrawler distribution by running `mvn -Ddistrib package`. The SchemaCrawler distribution will be created in the `target` of the `schemacrawler-distrib` module.

## Eclipse and IntelliJ IDEA
Before importing the various SchemaCrawler projects into [Eclipse] or [IntelliJ IDEA], make sure that you have a successful [Apache Maven] build. [Apache Maven] will download all the dependencies needed to build SchemaCrawler.

[Java]: https://www.oracle.com/java/technologies/
[Eclipse]: https://www.eclipse.org/downloads/eclipse-packages/
[IntelliJ IDEA]: https://www.jetbrains.com/idea/download/
[SchemaCrawler jars]: https://central.sonatype.com/search?q=us.fatehi.schemacrawler&sort=name
[SchemaCrawler Docker image]: https://hub.docker.com/r/schemacrawler/schemacrawler/
[Gradle]: https://gradle.org/
[Graphviz]: https://www.graphviz.org/
[Apache Maven]: https://maven.apache.org/

