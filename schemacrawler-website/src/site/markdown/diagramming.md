# SchemaCrawler Database Diagramming

SchemaCrawler generates database diagrams using [Graphviz](http://www.graphviz.org/) in any of the 
[output formats supported by Graphviz](http://www.graphviz.org/doc/info/output.html). 
SchemaCrawler is unique among database diagramming tools in that you do not need to 
know the table names or column names that you are interested in. All you need to know 
is what to search for, in the form of a regular expression. You can filter out tables, 
views, and columns based on regular expressions, using 
[grep](faq.html#whats-schemacrawler-grep) functionality. SchemaCrawler has powerful 
command-line options to match tables, and then find other tables related to the matched 
ones, whether they are parent or child tables. If your schema changes, you can simply 
regenerate the diagram, without having to know the exact changes that were made to the 
schema.

SchemaCrawler relies on [Graphviz](http://www.graphviz.org/) to generate diagrams.
Install [Graphviz](http://www.graphviz.org/) first, and ensure that it is on the system 
PATH. If installing [Graphviz](http://www.graphviz.org/) is not an option for you, use 
the pure Java Graphviz library. Instructions are provided with 
[the SchemaCrawler download](https://github.com/schemacrawler/SchemaCrawler/releases), 
in the `diagram` example.
Currently, the the pure Java Graphviz library is only able to generate PNG and SVG diagrams.

Then you can run SchemaCrawler with the correct command-line options - for 
example, 
`--command=schema --output-format=png --output-file=graph.png` 

See the diagram example in the 
[SchemaCrawler examples](http://github.com/schemacrawler/SchemaCrawler/releases/) 
download. An example of a SchemaCrawler database diagram is below.

<a href="diagram-examples/diagram.png" data-lightbox="lightbox" title="SchemaCrawler database diagram">
<img class="img-fluid img-thumbnail" src="diagram-examples/diagram.png" style="width: 200px;" />
</a>


## Database Diagram Options

SchemaCrawler offers several options to change what you see on the database diagram. Here are a few variations:

- Suppress schema names and foreign key names, using the `--portable-names` command-line option.
<br />
<a href="diagram-examples/diagram_2_portablenames.png" data-lightbox="lightbox" title="SchemaCrawler database diagram">
<img class="img-fluid img-thumbnail" src="diagram-examples/diagram_2_portablenames.png" style="width: 200px;" />
</a>
- Show only significant columns, such as primary and foreign key columns, and columns that are part of unique indexes. Use the `--info-level=standard --command=brief`command-line option.
<br />
<a href="diagram-examples/diagram_3_important_columns.png" data-lightbox="lightbox" title="SchemaCrawler database diagram">
<img class="img-fluid img-thumbnail" src="diagram-examples/diagram_3_important_columns.png" style="width: 200px;" />
</a>
- Show column ordinals, by setting configuration option `schemacrawler.format.show_ordinal_numbers=true` in the configuration file.
<br />
<a href="diagram-examples/diagram_4_ordinals.png" data-lightbox="lightbox" title="SchemaCrawler database diagram">
<img class="img-fluid img-thumbnail" src="diagram-examples/diagram_4_ordinals.png" style="width: 200px;" />
</a>
- Display columns in alphabetical order, using the `--sort-columns` command-line option.
<br />
<a href="diagram-examples/diagram_5_alphabetical.png" data-lightbox="lightbox" title="SchemaCrawler database diagram">
<img class="img-fluid img-thumbnail" src="diagram-examples/diagram_5_alphabetical.png" style="width: 200px;" />
</a>
- Grep for columns, and also display outgoing relationships, using `--grep-columns=.*\\.BOOKS\\..*\\.ID` as a command-line option.
<br />
<a href="diagram-examples/diagram_6_grep.png" data-lightbox="lightbox" title="SchemaCrawler database diagram">
<img class="img-fluid img-thumbnail" src="diagram-examples/diagram_6_grep.png" style="width: 200px;" />
</a>
- Grep for columns, but only show matching tables, using `--grep-columns=.*\\.BOOKS\\..*\\.ID -only-matching` as command-line options.
<br />
<a href="diagram-examples/diagram_7_grep_onlymatching.png" data-lightbox="lightbox" title="SchemaCrawler database diagram">
<img class="img-fluid img-thumbnail" src="diagram-examples/diagram_7_grep_onlymatching.png" style="width: 200px;" />
</a>
- Do not show cardinality on the diagrams, to avoid clutter. Set configuration option `schemacrawler.graph.show.primarykey.cardinality=false` and `schemacrawler.graph.show.foreignkey.cardinality=false` in the configuration file.
<br />
<a href="diagram-examples/diagram_8_no_cardinality.png" data-lightbox="lightbox" title="SchemaCrawler database diagram">
<img class="img-fluid img-thumbnail" src="diagram-examples/diagram_8_no_cardinality.png" style="width: 200px;" />
</a>
- Show table row counts on the diagrams, set configuration option `schemacrawler.format.show_row_counts=true` in the configuration file, and use `--info-level=maximum` on the command-line. Then run SchemaCrawler with the `--load-row-counts` command-line option.
<br />
<a href="diagram-examples/diagram_9_row_counts.png" data-lightbox="lightbox" title="SchemaCrawler database diagram">
<img class="img-fluid img-thumbnail" src="diagram-examples/diagram_9_row_counts.png" style="width: 200px;" />
</a>
- Do not show catalog and schema colors on the diagrams, set configuration option `schemacrawler.format.no_schema_colors=true` in the configuration file.
<br />
<a href="diagram-examples/diagram_10_no_schema_colors.png" data-lightbox="lightbox" title="SchemaCrawler database diagram">
<img class="img-fluid img-thumbnail" src="diagram-examples/diagram_10_no_schema_colors.png" style="width: 200px;" />
</a>
- Show a title on the diagram, use `--title "Books and Publishers Schema"` on the command-line.
<br />
<a href="diagram-examples/diagram_11_title.png" data-lightbox="lightbox" title="SchemaCrawler database diagram">
<img class="img-fluid img-thumbnail" src="diagram-examples/diagram_11_title.png" style="width: 200px;" />
</a>
</a>
- Set Graphviz attributes for the graph, node and edge, for example, set configuration option `schemacrawler.graph.graphviz.graph.splines=ortho` in the configuration file.
<br />
<a href="diagram-examples/diagram_12_graphviz_attributes.png" data-lightbox="lightbox" title="SchemaCrawler database diagram">
<img class="img-fluid img-thumbnail" src="diagram-examples/diagram_12_graphviz_attributes.png" style="width: 200px;" />
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
​    
```        
schemacrawler.graph.graphviz_opts=-Gdpi=300
```

### Embedded Diagrams

SchemaCrawler can generate [SVG diagrams embedded in HTML output](snapshot-examples/snapshot.svg.html). To generate this
format, run SchemaCrawler with an `--output-format=htmlx` command-line argument. Please edit the SchemaCrawler 
configuration file, `schemacrawler.config.properties`, and comment out or delete the line 
`schemacrawler.graph.graphviz_opts=-Gdpi=300`.


## Tips

- Adobe Acrobat Reader sometimes cannot render PDF files generated by GraphViz. In this case, please use another
PDF viewer, such as [Foxit](https://www.foxitsoftware.com/products/pdf-reader/).
- To set GraphViz command-line options, edit the SchemaCrawler 
configuration file, `schemacrawler.config.properties`, and edit the line with
`schemacrawler.graph.graphviz_opts`.


## SchemaCrawler Diagrams in Use

Schemacrawler database diagrams in use at the Scrum meeting at the Software Development Departement of [La Ville de Nouméa](http://www.noumea.nc/). 
Photograph courtesy of [Adrien Sales](https://www.linkedin.com/in/adrien-sales).

<a href="images/SchemaCrawler_Noumea.jpg" data-lightbox="sc-in-use" data-title="Schemacrawler database diagrams in use">
<img class="img-fluid img-thumbnail" style="width: 50%" src="images/SchemaCrawler_Noumea.jpg" alt="Schemacrawler database diagrams in use" />
</a>

