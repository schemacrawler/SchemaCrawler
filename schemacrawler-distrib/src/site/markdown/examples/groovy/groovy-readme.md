# SchemaCrawler - Groovy Scripting Example

## Description
SchemaCrawler allows scripting with database metadata, using Groovy. This
example shows how to script with Groovy.

## How to Setup
1. Make sure that java is on your PATH
2. Start a command shell in the `ivy` directory 
3. Run `download.cmd groovy` (or `download.sh groovy` on Unix) to
   install Groovy scripting support

## How to Run
1. Make sure that java is on your PATH
2. Start the database server by running the StartDatabaseServer script from the distribution directory 
3. Start a command shell in the `groovy` example directory 
4. Run `groovy.cmd tables.groovy`  (or `groovy.sh tables.groovy` on Unix) 

## How to Experiment
1. Run `groovy.cmd droptables.groovy`  (or `groovy.sh droptables.groovy` on Unix). 
   (In order to restore the database, restart the database server.) 
2. Try modifying the Groovy in the *.groovy files to do different things. 
   You also have access to a live database connection. 
