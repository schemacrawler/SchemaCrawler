# SchemaCrawler - Other Diagrams Example

## Description
SchemaCrawler can generate [mermaid](https://mermaid-js.github.io/mermaid/#/entityRelationshipDiagram) 
and [dbdiagram.io](https://dbdiagram.io/home) diagrams with a little scripting with Python.

## How to Setup
1. Make sure that java is on your PATH
2. Start a command shell in the `_downloader` directory 
3. Run `download.cmd python` (or `download.sh python` on Unix) to
   install Python scripting support

## How to Run
1. Make sure that java is on your PATH
2. Start the test database server by following instructions in the `_testdb/README.html` file
3. Start a command shell in the `other-diagrams` example directory
4. Run `other-diagram.cmd mermaid.py` (or `other-diagram.sh mermaid.py` on Unix) and view the output in [mermaid](https://mermaid-js.github.io/mermaid-live-editor)
5. Run `other-diagram.cmd dbml.py` (or `other-diagram.sh dbml.py` on Unix) and view the output in [dbdiagram.io](https://dbdiagram.io/d)
6. Run `other-diagram.cmd plantuml.py` (or `other-diagram.sh plantuml.py` on Unix) and view the output in [PlantUML](http://www.plantuml.com/plantuml/umla)

## How to Experiment
1. Try modifying the Python in the *.py files to do different things. 
