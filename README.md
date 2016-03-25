[![Project Website](https://img.shields.io/badge/project_website-SchemaCrawler-7B2A90.svg)](http://www.schemacrawler.com/)
[![Build Status](https://travis-ci.org/sualeh/SchemaCrawler.svg?branch=master)](https://travis-ci.org/sualeh/SchemaCrawler)
[![Coverage Status](https://coveralls.io/repos/sualeh/SchemaCrawler/badge.svg?branch=master&service=github)](https://coveralls.io/github/sualeh/SchemaCrawler?branch=master)
[![Maven Central](https://img.shields.io/maven-central/v/us.fatehi/schemacrawler.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3Aus.fatehi%20schemacrawler)
[![Join the chat at https://gitter.im/sualeh/SchemaCrawler](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/sualeh/SchemaCrawler?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

# ![SchemaCrawler](https://github.com/sualeh/SchemaCrawler/blob/master/schemacrawler-site/src/site/resources/images/schemacrawler_logo.png?raw=true) SchemaCrawler

SchemaCrawler is a free database schema discovery and comprehension tool. SchemaCrawler has a good mix of useful features for data governance. You can [search for database schema objects](http://sualeh.github.io/SchemaCrawler/schemacrawler_grep.html) using regular expressions, and output the schema and data in a readable text format. The output serves for database documentation, and is designed to be [diff-ed](http://en.wikipedia.org/wiki/Diff) against other database schemas. SchemaCrawler also generates [schema diagrams.](http://sualeh.github.io/SchemaCrawler/diagramming.html) You can [execute scripts](http://sualeh.github.io/SchemaCrawler/scripting.html) in any standard scripting language against your database. You can find potential schema design issues with [lint.](http://sualeh.github.io/SchemaCrawler/lint.html)

SchemaCrawler supports almost any database that has a JDBC driver, but for convenience is [bundled with drivers](http://sualeh.github.io/SchemaCrawler/database-support.html) for some commonly used RDBMS systems. SchemaCrawler works with any operating system that supports Java SE 8 or better.

SchemaCrawler is also a Java API that makes working with database metadata as easy as working with plain old Java objects.

All SchemaCrawler jars are in the [Central Repository](http://search.maven.org/#search%7Cga%7C1%7Cg%3Aus.fatehi%20a%3Aschemacrawler*). They can be used as dependencies in [Gradle](https://gradle.org/) or [Apache Maven](http://maven.apache.org/) projects, or with any other build system that supports the Central Repository.
