# SchemaCrawler - Python Scripting Example

## Description
SchemaCrawler allows scripting with database metadata, using Python. This
example shows how to script with Python.

## How to Setup
1. Make sure that java is on your PATH
2. Start a command shell in the `ivy` directory 
3. Run `download.cmd python` (or `download.sh python` on Unix) to
   install Python scripting support

## How to Run
1. Make sure that java is on your PATH
2. Start the test database server by running the StartDatabaseServer script from the distribution directory 
3. Start a command shell in the `python` example directory
4. Run `python.cmd tables.py` (or `python.sh tables.py` on Unix) 

## How to Experiment
1. Run `python.cmd droptables.py` (or `python.sh droptables.py` on Unix). 
   (In order to restore the database, restart the database server.) 
2. Try modifying the Python in the *.py files to do different things. 
   You also have access to a live database connection. 
