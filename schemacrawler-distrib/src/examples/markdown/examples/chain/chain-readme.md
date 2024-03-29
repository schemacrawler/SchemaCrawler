# SchemaCrawler - Chain Example

## Description
SchemaCrawler allows scripting with database metadata, using JavaScript. This
example shows how to "chain" or run multiple SchemaCrawler commands.

## How to Setup
1. Make sure that java is on your PATH
2. Start a command shell in the `_downloader` directory 
3. Run `download.cmd javascript` (or `download.sh javascript` on Unix) to
   install support for JavaScript, if you are using Java 15 or above

## How to Run
1. Make sure that java is on your PATH
2. Start the test database server by following instructions in the `_testdb/README.html` file
3. Start a command shell in the chain example directory 
4. Run `chain.cmd chain.js`  (or `chain.sh chain.js` on Unix) 

## How to Experiment
1. Try modifying the JavaScript in the `chain.js` file to run different SchemaCrawler commands. 
