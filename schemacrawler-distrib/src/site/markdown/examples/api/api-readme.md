# SchemaCrawler - API Example

## Description
The API example demonstrates the use of the SchemaCrawler API to create a data
source, and obtain database metadata. The SchemaCrawler API is a much simpler
alternative to using JDBC metadata.

## How to Run
1. Make sure that java is on your PATH
2. Start the test database server by running the StartDatabaseServer script from the distribution directory 
3. Run `api.cmd` (or `api.sh` on Unix) to compile and run the program 

## How to Experiment
1. Try uncommenting the code block in `ApiExample.java` that modifies the default options. 
2. Read the [SchemaCrawler javadoc](http://www.schemacrawler.com/apidocs/index.html), and 
   edit `ApiExample.java` to print more details. 
