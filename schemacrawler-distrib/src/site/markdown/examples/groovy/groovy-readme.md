# SchemaCrawler - Groovy Scripting Example

## Description
SchemaCrawler allows scripting with database metadata, using Groovy. This
example shows how to script with Groovy.

## How to Run
1. Install [ant](http://ant.apache.org/), and make sure that ant is on your path 
2. Make sure that java is on your PATH
3. Start the database server by running the StartDatabaseServer script from the distribution directory 
4. Start a command shell in the groovy example directory 
5. Run `groovy_setup.cmd` (or `groovy_setup.sh` on Unix) to download and setup Groovy scripting support 
6. Run `groovy.cmd tables.groovy`  (or `groovy.sh tables.groovy` on Unix) 

## How to Experiment
1. Run `groovy.cmd droptables.groovy`  (or `groovy.sh droptables.groovy` on Unix). 
   (In order to restore the database, restart the database server.) 
2. Try modifying the Groovy in the *.groovy files to do different things. 
   You also have access to a live database connection. 
