# SchemaCrawler - Ruby Scripting Example

## Description
SchemaCrawler allows scripting with database metadata, using Ruby. This
example shows how to script with Ruby.

## How to Setup
1. Make sure that java is on your PATH
2. Start a command shell in the `ivy` directory 
3. Run `download.cmd ruby` (or `download.sh ruby` on Unix) to
   install Ruby scripting support

## How to Run
1. Make sure that java is on your PATH
2. Start the test database server by running the StartDatabaseServer script from the distribution directory 
3. Start a command shell in the `ruby` example directory
4. Run `ruby.cmd tables.rb` (or `ruby.sh tables.rb` on Unix) 

## How to Experiment
1. Run `ruby.cmd droptables.rb` (or `ruby.sh droptables.rb` on Unix). 
   (In order to restore the database, restart the database server.) 
2. Try modifying the Ruby in the *.rb files to do different things. 
   You also have access to a live database connection. 
