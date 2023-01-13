# SchemaCrawler - API Example

## Description
The API example demonstrates the use of the SchemaCrawler API to create a data
source, and obtain database metadata. The SchemaCrawler API is a much simpler
alternative to using JDBC metadata.

## How to Run
1. Make sure that java is on your PATH
2. Start the test database server by following instructions in the `_testdb/README.html` file
3. Run `api.cmd` (or `api.sh` on Unix) to compile and run the first example 
4. Run `executable.cmd` (or `executable.sh` on Unix) to compile and run the second example 
5. Run `resultset.cmd` (or `resultset.sh` on Unix) to compile and run the third example 

## How to Experiment
1. Try uncommenting the code block in `com.example.ApiExample.java` that modifies the default options. 
2. Read the [SchemaCrawler javadoc](https://javadoc.io/doc/us.fatehi/schemacrawler/), and 
   edit `com.example.ApiExample.java` to print more details. 
3. Make changes to `com.example.ExecutableExample.java` to produce different types of output.
