# SchemaCrawler - Diagram Example

## Description
The diagram example demonstrates the integration of SchemaCrawler with GraphViz.

## How to Run
1. Make sure that java is on your PATH
2. Start the test database server by running the StartDatabaseServer script from the distribution directory 
3. Start a command shell in the diagram example directory 
4. Run `diagram.cmd` (or `diagram.sh` on Unix) 

## How to Experiment
1. Try using grep options to include certain tables. For example, try using a command-line option of `-grepcolumns=.*\\.AUTHOR.*`
2. Try controlling display of foreign-key names, column ordinal numbers, and schema names by setting the 
   following properties in the SchemaCrawler configuration file, `config/schemacrawler.config.properties`. 

```           
schemacrawler.format.show_ordinal_numbers=true        
schemacrawler.format.hide_foreignkey_names=true
schemacrawler.format.show_unqualified_names=true
```    

3. Try using GraphViz command-line options by setting the following property in the SchemaCrawler configuration file, 
   `config/schemacrawler.config.properties`. 
    
```        
schemacrawler.graph.graphviz_opts=-Gdpi=300
```    
