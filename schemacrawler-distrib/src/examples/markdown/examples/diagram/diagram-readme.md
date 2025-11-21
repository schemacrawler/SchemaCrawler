# SchemaCrawler - Diagram Example

## Description
The diagram example demonstrates the integration of SchemaCrawler with Graphviz.

## How to Setup
1. Make sure that SchemaCrawler is [installed on your system](https://www.schemacrawler.com/downloads.html)
2. Make sure that `schemacrawler` is on your PATH
3. Install [Graphviz](https://www.graphviz.org/).

### Run With SQLite Database

1. Start a command shell in the diagram example directory 
2. Run `sqlite_diagram.cmd ..\..\_testdb\sc.db sc.pdf` (or `sqlite_diagram.sh  ../../_testdb/sc.db sc.pdf` on Unix) 

### Run With HyperSQL Database

1. Start the test database server by following instructions in the `_testdb/README.html` file
2. Start a command shell in the diagram example directory 
3. Run `diagram.cmd database-diagram.png` (or `diagram.sh database-diagram.png` on Unix) 

## How to Experiment
1. Try other output formats like PDF by changing the extension of the output file
2. Try using grep options to include certain tables. For example, try using a command-line option of `--grep-columns=.*\\.AUTHOR.*`
3. Try controlling display of foreign-key names, column ordinal numbers, and schema names by setting the 
   following properties in the SchemaCrawler configuration file, `config/schemacrawler.config.properties`. 

```           
schemacrawler.format.show_ordinal_numbers=true        
schemacrawler.format.hide_foreignkey_names=true
schemacrawler.format.hide_weakassociation_names=true
schemacrawler.format.show_unqualified_names=true
```   

4. Try using Graphviz command-line options by setting the following property in the SchemaCrawler configuration file, 
   `config/schemacrawler.config.properties`. 
    
```        
schemacrawler.graph.graphviz_opts=-Gdpi=300
```    
