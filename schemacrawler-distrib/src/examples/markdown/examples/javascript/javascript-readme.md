# SchemaCrawler - JavaScript Example

## Description
SchemaCrawler allows scripting with database metadata, using JavaScript. This
example shows how to script with JavaScript.

## How to Setup
1. Make sure that SchemaCrawler is [installed on your system](https://www.schemacrawler.com/downloads.html)
2. Make sure that `schemacrawler` is on your PATH

## How to Run
1. Make sure that java is on your PATH
2. Start the test database server by following instructions in the `_testdb/README.html` file
3. Start a command shell in the javascript example directory 
4. Run `javascript.cmd tables.js` (or `javascript.sh tables.js` on Unix) 

## How to Experiment
1. Run `javascript.cmd droptables.js` (or `javascript.sh droptables.js` on Unix). 
   (In order to restore the database, restart the database server.) 
2. Try modifying the JavaScript in the *.js files to do different things, 
   including generating exceptions. 
