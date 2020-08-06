<!-- markdownlint-disable MD041 -->
[![Quick Build](https://github.com/schemacrawler/SchemaCrawler/workflows/Quick%20Build/badge.svg)](https://github.com/schemacrawler/SchemaCrawler/actions?query=workflow%3A%22Quick+Build%22)
[![Integration Tests](https://github.com/schemacrawler/SchemaCrawler/workflows/Integration%20Tests/badge.svg)](https://github.com/schemacrawler/SchemaCrawler/actions?query=workflow%3A%22Integration+Tests%22)
[![codecov](https://codecov.io/gh/schemacrawler/SchemaCrawler/branch/master/graph/badge.svg)](https://codecov.io/gh/schemacrawler/SchemaCrawler)
[![The Central Repository](https://img.shields.io/maven-central/v/us.fatehi/schemacrawler.svg)](https://search.maven.org/search?q=g:us.fatehi%20schemacrawler*)
[![Main distribution](https://img.shields.io/github/downloads/schemacrawler/schemacrawler/total)](https://github.com/schemacrawler/SchemaCrawler/releases/latest)
[![Docker Pulls](https://img.shields.io/docker/pulls/schemacrawler/schemacrawler.svg)](https://hub.docker.com/r/schemacrawler/schemacrawler/)

<img src="https://raw.githubusercontent.com/schemacrawler/SchemaCrawler/master/schemacrawler-distrib/src/site/resources/images/schemacrawler_logo.png" height="100px" width="100px" align="right" />

# SchemaCrawler

> * **Please see the [SchemaCrawler website](https://www.schemacrawler.com/) for more details.**
> * **Explore the SchemaCrawler command-line with a live tutorial on [Katacoda](https://www.katacoda.com/schemacrawler).**

## About

SchemaCrawler is a free database schema discovery and comprehension tool. SchemaCrawler has a good mix of useful features for data governance. You can [search for database schema objects](https://www.schemacrawler.com/schemacrawler_grep.html) using regular expressions, and output the schema and data in a readable text format. The output serves for database documentation, and is designed to be [diff-ed](https://en.wikipedia.org/wiki/Diff) against other database schemas. SchemaCrawler also generates [schema diagrams.](https://www.schemacrawler.com/diagramming.html) You can [execute scripts](https://www.schemacrawler.com/scripting.html) in any standard scripting language against your database. You can find potential schema design issues with [lint](https://www.schemacrawler.com/lint.html). 

SchemaCrawler supports almost any database that has a JDBC driver, but for convenience is [bundled with drivers](https://www.schemacrawler.com/database-support.html) for some commonly used RDBMS systems. SchemaCrawler works with any operating system that supports Java SE 8 or better.

SchemaCrawler is also a Java API that makes working with database metadata as easy as working with plain old Java objects.


## Licensing

SchemaCrawler is available under a number of [licenses](https://www.schemacrawler.com/license.html).


## Distributions and Downloads

[Download releases with tutorials from GitHub.](https://github.com/sualeh/SchemaCrawler/releases) All SchemaCrawler jars are in the [Central Repository](https://search.maven.org/search?q=g:us.fatehi%20a:schemacrawler*). They can be used as dependencies in [Gradle](https://gradle.org/) or [Apache Maven](https://maven.apache.org/) projects, or with any other build system that supports the Central Repository. [Pre-packaged Docker images](https://hub.docker.com/r/schemacrawler/schemacrawler/) are on Docker Hub. 

Additional SchemaCrawler database plugins are available from the [schemacrawler/SchemaCrawler-Database-Plugins](https://github.com/schemacrawler/SchemaCrawler-Database-Plugins) project.


## Support

Please get support on [Stack Overflow](https://stackoverflow.com/search?tab=newest&q=schemacrawler), following the [Guidelines for Support](https://www.schemacrawler.com/consulting.html).
