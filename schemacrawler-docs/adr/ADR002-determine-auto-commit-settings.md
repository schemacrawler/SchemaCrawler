# Determine Auto-commit Settings

## Context and Problem Statement

Determine the most appropriate auto-commit settings for SchemaCrawler and manage database transactions. SchemaCrawler mostly needs read-only access to the database, but will need to commit occasionally, specifically when creating the test database, and when passing in a connection to the scripting context for running arbitrary user scripts. In code, though, some database plugins such as the Oracle plugin may need to run some SQL queries before the schema is crawled.


## Considered Options

- The JDBC specification states that by default, a JDBC driver should create a new database connection with an auto-commit set to true, so that there are implicit database transactions. However, to avoid the vagaries of JDBC drivers, it may be better to explicitly set the database connection not to commit and commit explicitly when needed.
- Another way is not to change the default auto-commit setting, but rather decide to commit after checking that auto-commit mode is off. If this way is used, then we have to rely on the JDBC driver giving us an accurate value for the auto-commit mode setting, and that the driver or database will not expect transactions to be managed by the client.


## Decision Outcome

In previous versions of SchemaCrawler, the test schema creator explicitly turned off the auto-commit mode and explicitly committed each DDL or SQL statement. The test framework behaved in the same way, masking the behavior of the Oracle plugin which had to execute some SQL. From 16.16.9 onwards, SchemaCrawler does not explicitly set the auto-commit mode, either in the test schema creator, or in the Oracle plugin. Commits are done where needed after checking if the auto-commit mode is off.
