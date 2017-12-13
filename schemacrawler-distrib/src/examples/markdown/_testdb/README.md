# SchemaCrawler - Test Database

## Description
A small footprint, light-weight database server, with some test data for running the SchemaCrawler examples.

## How to Run the Database Server
1. Make sure that java is on your PATH
2. Start a command shell in the `_testdb` directory
3. Start the test database server by running the StartDatabaseServer script

## How to Stop the Database Server
1. Press Crtl-C in the database server command shell, or kill the process

## How to Restore the Test Database
If you have dropped any tables or altered any data, you can restore the test database.

1. First stop the database server, following the instructions above
2. Delete all of the `hsqldb.schemacrawler.*` files in the `_testdb` directory
3. Restart the database server, following the instructions above
