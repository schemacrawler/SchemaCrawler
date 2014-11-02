# SchemaCrawler - Python Scripting Example

## Description
SchemaCrawler allows scripting with database metadata, using Python. This
example shows how to script with Python.

## How to Run
1. Install [ant,](http://ant.apache.org/) and make sure that ant is on your path 
2. Make sure that java is on your PATH
3. Start the database server by running the StartDatabaseServer script from the distribution directory 
4. Start a command shell in the python example directory 
5. Run `python_setup.cmd` (or `python_setup.sh` on Unix) to download and setup Python scripting support 
6. Run `python.cmd tables.py` (or `python.sh tables.py` on Unix) 

## How to Experiment
1. Run `python.cmd droptables.py` (or `python.sh droptables.py` on Unix). 
   (In order to restore the database, restart the database server.) 
2. Try modifying the Python in the *.py files to do different things. 
   You also have access to a live database connection. 
