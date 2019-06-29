# SchemaCrawler Plugins

There are several ways to use and extend SchemaCrawler.

## Database Scripting
You can script against your database, using the scripting language of your choice. A live
database connection is provided. For more information, see [SchemaCrawler Database Scripting](scripting.html).

## Use SchemaCrawler in an Apache Maven Project
To use SchemaCrawler in your [Apache Maven] projects, add a dependency to SchemaCrawler in your pom.xml,
as described in the [Getting Started](readme.html).

## Plugin New SchemaCrawler Commands
You can create a new SchemaCrawler command, and plug it into the SchemaCrawler framework. 

Fork the [schemacrawler/SchemaCrawler-Plugins-Starter](https://github.com/schemacrawler/SchemaCrawler-Plugins-Starter) project, and build a jar from the `example-command-plugin`. Place this jar on SchemaCrawler's `CLASSPATH` (or drop it into the `lib/` directory, and your new command will be available to SchemaCrawler.

## Plugin New SchemaCrawler Linters
You can create a new SchemaCrawler linter, and plug it into the SchemaCrawler framework. 

Fork the [schemacrawler/SchemaCrawler-Plugins-Starter](https://github.com/schemacrawler/SchemaCrawler-Plugins-Starter) project, and build a jar from the `example-lint-plugin`. Place this jar on SchemaCrawler's `CLASSPATH` (or drop it into the `lib/` directory, and your new linter will be available to SchemaCrawler.

## Plugin New SchemaCrawler Database Connectors
You can create a new SchemaCrawler connector for a database, and plug it into the SchemaCrawler framework. 

Fork the [schemacrawler/SchemaCrawler-Plugins-Starter](https://github.com/schemacrawler/SchemaCrawler-Plugins-Starter) project, and build a jar from the `example-dbconnector-plugin`. Place this jar on SchemaCrawler's `CLASSPATH` (or drop it into the `lib/` directory, and your new database connector will be available to SchemaCrawler.


