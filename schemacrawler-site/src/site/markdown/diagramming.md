# SchemaCrawler Database Diagramming

SchemaCrawler generates database diagrams using [Graphviz](http://www.graphviz.org/) in any of the 
[output formats supported by Graphviz](http://www.graphviz.org/content/output-formats). 
SchemaCrawler is unique among database diagramming tools in that you do not need to 
know the table names or column names that you are interested in. All you need to know 
is what to search for, in the form of a regular expression. You can filter out tables, 
views, and columns based on regular expressions, using 
[grep](faq.html#whats-schemacrawler-grep) functionality. SchemaCrawler has powerful 
command-line options to match tables, and then find other tables related to the matched 
ones, whether they are parent or child tables. If your schema changes, you can simply 
regenerate the diagram, without having to know the exact changes that were made to the 
schema.

Install [Graphviz](http://www.graphviz.org/) first, and ensure that it is on the system 
PATH. Then you can run SchemaCrawler with the correct command-line options - for 
example, 
`-command=schema -outputformat=png -outputfile=graph.png` 

See the diagram example in the 
[SchemaCrawler examples](http://github.com/sualeh/SchemaCrawler/releases/) 
download. An example of a SchemaCrawler database diagram is below.

<a href="images/diagram.png" data-toggle="lightbox" title="SchemaCrawler database diagram">
<img src="images/diagram.png" width="200" />
</a>


## Database Diagram Options

SchemaCrawler offers several options to change what you see on the database diagram. Here are a few variations:

- Suppress schema names and foreign key names, using the `-portablenames` command-line option.
<br />
<a href="images/diagram_2_portablenames.png" data-toggle="lightbox" title="SchemaCrawler database diagram">
<img src="images/diagram_2_portablenames.png" width="200" />
</a>
- Show only significant columns, such as primary and foreign key columns, and columns that are part of unique indexes. Use the `-infolevel=standard -command=brief `command-line option.
<br />
<a href="images/diagram_3_important_columns.png" data-toggle="lightbox" title="SchemaCrawler database diagram">
<img src="images/diagram_3_important_columns.png" width="200" />
</a>
- Show column ordinals, by setting configuration option `schemacrawler.format.show_ordinal_numbers=true` in the configuration file.
<br />
<a href="images/diagram_4_ordinals.png" data-toggle="lightbox" title="SchemaCrawler database diagram">
<img src="images/diagram_4_ordinals.png" width="200" />
</a>
- Display columns in alphabetical order, using the `-sortcolumns` command-line option.
<br />
<a href="images/diagram_5_alphabetical.png" data-toggle="lightbox" title="SchemaCrawler database diagram">
<img src="images/diagram_5_alphabetical.png" width="200" />
</a>
- Grep for columns, and also display outgoing relationships, using `-grepcolumns=.*\\.BOOKS\\..*\\.ID` as a command-line option.
<br />
<a href="images/diagram_6_grep.png" data-toggle="lightbox" title="SchemaCrawler database diagram">
<img src="images/diagram_6_grep.png" width="200" />
</a>
- Grep for columns, but only show matching tables, using `-grepcolumns=.*\\.BOOKS\\..*\\.ID -only-matching` as command-line options.
<br />
<a href="images/diagram_7_grep_onlymatching.png" data-toggle="lightbox" title="SchemaCrawler database diagram">
<img src="images/diagram_7_grep_onlymatching.png" width="200" />
</a>
- Do not show cardinality on the diagrams, to avoid clutter. Set configuration option `schemacrawler.graph.show.primarykey.cardinality=false` and `schemacrawler.graph.show.foreignkey.cardinality=false` in the configuration file.
<br />
<a href="images/diagram_8_no_cardinality.png" data-toggle="lightbox" title="SchemaCrawler database diagram">
<img src="images/diagram_8_no_cardinality.png" width="200" />
</a>
- Show table row counts on the diagrams, set configuration option `schemacrawler.format.show_row_counts=true -infolevel=maximum` on the command-line.
<br />
<a href="images/diagram_9_row_counts.png" data-toggle="lightbox" title="SchemaCrawler database diagram">
<img src="images/diagram_9_row_counts.png" width="200" />
</a>
- Do not show catalog and schema colors on the diagrams, set configuration option `schemacrawler.format.no_schema_colors=true`.
<br />
<a href="images/diagram_10_no_schema_colors.png" data-toggle="lightbox" title="SchemaCrawler database diagram">
<img src="images/diagram_10_no_schema_colors.png" width="200" />
</a>
- Show a title on the diagram, use `-title "Books and Publishers Schema"` on the command-line.
<br />
<a href="images/diagram_11_title.png" data-toggle="lightbox" title="SchemaCrawler database diagram">
<img src="images/diagram_11_title.png" width="200" />
</a>


## Additional Configuration

### Diagram Options

You can decide whether foreign-key names, column ordinal numbers, and schema names are 
displayed by setting the following properties in the SchemaCrawler configuration file, 
`schemacrawler.config.properties`.

```
schemacrawler.format.show_ordinal_numbers=true
schemacrawler.format.hide_foreignkey_names=true
schemacrawler.format.show_unqualified_names=true
```

### Table Row Counts

You can how table row counts on the database diagram,
by setting the following properties in the SchemaCrawler configuration file,
`schemacrawler.config.properties`, and using `-infolevel=maximum`

```
schemacrawler.format.show_row_counts=true
```

### Graphviz Command-line Options

You can provide additional Graphviz command-line options in one of three ways:

* using the `schemacrawler.graph.graphviz_opts` property in the SchemaCrawler configuration file,
* by passing in the additional arguments using the `SC_GRAPHVIZ_OPTS` Java system property, 
* or by setting the `SC_GRAPHVIZ_OPTS` environmental variable.

SchemaCrawler does not set the dpi, or resolution of generated graphs. A useful Graphviz command-line 
option to set is `-Gdpi=300`. In the SchemaCrawler configuration file, 
`schemacrawler.config.properties`, this would look like: 
    
```        
schemacrawler.graph.graphviz_opts=-Gdpi=300
```    

### Embedded Diagrams

SchemaCrawler can generate [SVG diagrams embedded in HTML output](snapshot-examples/snapshot.svg.html). To generate this
format, run SchemaCrawler with an `-outputformat=htmlx` command-line argument. Please edit the SchemaCrawler 
configuration file, `schemacrawler.config.properties`, and comment out or delete the line 
`schemacrawler.graph.graphviz_opts=-Gdpi=300`.


## Tips

- Adobe Acrobat Reader sometimes cannot render PDF files generated by GraphViz. In this case, please use another
PDF viewer, such as [Foxit](https://www.foxitsoftware.com/products/pdf-reader/).
- To set GraphViz command-line options, edit the SchemaCrawler 
configuration file, `schemacrawler.config.properties`, and edit the line with
`schemacrawler.graph.graphviz_opts`.


## SchemaCrawler Diagrams in Use

Schemacrawler database diagrams in use at the Scrum meeting at the Software Development Departement of [La Ville de Nouméa](http://www.noumea.nc/). Photograph courtesy of Adrien Sales.

<a href="images/SchemaCrawler_Noumea.jpg" data-toggle="lightbox" title="Schemacrawler database diagrams in use">
<img src="images/SchemaCrawler_Noumea.jpg" />
</a>
