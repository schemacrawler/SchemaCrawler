# SchemaCrawler - Ruby Scripting Example

## Description
SchemaCrawler allows scripting with database metadata, using Ruby. This
example shows how to script with Ruby.

## How to Run
1. Install [ant](http://ant.apache.org/), and make sure that ant is on your path 
2. Make sure that java is on your PATH
3. Start the database server by running the StartDatabaseServer script from the distribution directory 
4. Start a command shell in the ruby example directory 
5. Run `ruby_setup.cmd` (or `ruby_setup.sh` on Unix) to download and setup Ruby scripting support 
6. Run `ruby.cmd tables.rb` (or `ruby.sh tables.rb` on Unix) 

## How to Experiment
1. Run `ruby.cmd droptables.rb` (or `ruby.sh droptables.rb` on Unix). 
   (In order to restore the database, restart the database server.) 
2. Try modifying the Ruby in the *.rb files to do different things. 
   You also have access to a live database connection. 
