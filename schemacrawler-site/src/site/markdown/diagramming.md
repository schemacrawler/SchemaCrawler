# SchemaCrawler Database Diagramming

SchemaCrawler generates database diagrams, using [Graphviz](http://www.graphviz.org/).
You can filter out tables, views, columns, stored procedure and functions based on regular expressions,
using the [grep](faq.html#whats-schemacrawler-grep) functionality.

SchemaCrawler is unique among database diagramming tools in that you do not need to know the
table names or column names that you are interested in. All you need is a search expression,
in the form of a regular expression. SchemaCrawler has powerful command-line options to match tables,
and then find other related tables, whether they are parent or child tables. If your schema changes,
you can simply regenerate the diagram, without having to know the exact changes that were made.

To use generate SchemaCrawler diagrams, for install [Graphviz](http://www.graphviz.org/).
Then you can run SchemaCrawler with the correct command-line options - for example,
`-command graph -outputformat png -outputfile=graph.png` See the diagram example
in the [SchemaCrawler examples](http://github.com/sualeh/SchemaCrawler/releases/)
download. An example of a SchemaCrawler database diagram is below.

You can provide additional GraphViz command-line options using the `SC_GRAPHVIZ_OPTS`
environmental variable, or pass in the additional arguments using the `SC_GRAPHVIZ_OPTS`
Java system property. SchemaCrawler does not set the dpi, or resolution of generated graphs.
A useful GraphViz command-line option to set is `-Gdpi=300`.

You can decide whether foreign-key names, column ordinal numbers, and schema names are displayed
by setting the following properties in the SchemaCrawler configuration file,
`schemacrawler.config.properties`.

```
schemacrawler.format.show_ordinal_numbers=true
schemacrawler.format.hide_foreignkey_names=true
schemacrawler.format.show_unqualified_names=true
```

You can how table row counts on the database diagram,
by setting the following properties in the SchemaCrawler configuration file,
`schemacrawler.config.properties`, and using `-infolevel=maximum`

```
schemacrawler.format.show_row_counts=true
```

For more details, see the diagram example in the
[SchemaCrawler examples](http://github.com/sualeh/SchemaCrawler/releases/)
download.

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
- Show significant columns, such as primary and foreign key columns, and columns that are part of unique indexes. Use the `-infolevel=standard -command=brief `command-line option.
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
- Grep for columns, but only show matching tables, using -grepcolumns=.*\\.BOOKS\\..*\\.ID and -only-matching as command-line options.
<br />
<a href="images/diagram_7_grep_onlymatching.png" data-toggle="lightbox" title="SchemaCrawler database diagram">
<img src="images/diagram_7_grep_onlymatching.png" width="200" />
</a>
- Do not show cardinality on the diagrams, to avoid clutter. Set configuration option `schemacrawler.graph.show.primarykey.cardinality=false` and `schemacrawler.graph.show.foreignkey.cardinality=false` in the configuration file.
<br />
<a href="images/diagram_8_no_cardinality.png" data-toggle="lightbox" title="SchemaCrawler database diagram">
<img src="images/diagram_8_no_cardinality.png" width="200" />
</a>
- Show table row counts on the diagrams,set configuration option `schemacrawler.format.show_row_counts=true` and `-infolevel=maximum` on the command-line.
<br />
<a href="images/diagram_9_row_counts.png" data-toggle="lightbox" title="SchemaCrawler database diagram">
<img src="images/diagram_9_row_counts.png" width="200" />
</a>

## SchemaCrawler Diagrams in Use

Schemacrawler database diagrams in use at the Scrum meeting at the Software Development Departement of [La Ville de Nouméa](http://www.noumea.nc/). Photograph courtesy of Adrien Sales.

<a href="images/SchemaCrawler_Noumea.jpg" data-toggle="lightbox" title="Schemacrawler database diagrams in use">
<img src="images/SchemaCrawler_Noumea.jpg" width="200" />
</a>
