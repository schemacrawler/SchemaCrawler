# SchemaCrawler Output

SchemaCrawler generates clean [plain
text,](snapshot-examples/snapshot.text) [comma-separated text
(CSV),](snapshot-examples/snapshot.csv)
[HTML5,](snapshot-examples/snapshot.html) [HTML5 with embedded
diagrams,](snapshot-examples/snapshot.svg.html) or [JavaScript object
notation (JSON)](snapshot-examples/snapshot.json) output. The HTML5
output is a combination of valid XML (that can be manipulated by XML
tools or XSLT), and HTML that can be viewed in a browser. The output
serves for database documentation, and is designed to be
[diff-ed](http://en.wikipedia.org/wiki/Diff) against other database
schemas.


SchemaCrawler is unique among database documentation tools in that you
do not need to know the table names or column names that you are
interested in. All you need is a search expression, in the form of a
regular expression. SchemaCrawler has powerful command-line options to
match tables, and then find other related tables, whether they are
parent or child tables. If your schema changes, you can simply
regenerate the diagram, without having to know the exact changes that
were made.

## Text Output Options

SchemaCrawler outputs complete details of your database schema [(see
HTML output)](html-examples/html.html), and in addition offers several
options to change what you see on the database diagram. Here are a few
variations, with examples in SchemaCrawler's HTML output format:

-   Suppress schema names and foreign key names, using the
    `-portablenames` command-line option [(see
    HTML output)](html-examples/html_2_portablenames.html).
-   Show significant columns, such as primary and foreign key columns,
    and columns that are part of unique indexes. Use the
    `-infolevel=standard -command=brief` command-line option [(see
    HTML output)](html-examples/html_3_important_columns.html).
-   Show column ordinals, by setting configuration option
    `schemacrawler.format.show_ordinal_numbers=true` in the
    configuration file [(see
    HTML output)](html-examples/html_4_ordinals.html).
-   Display columns in alphabetical order, using the `-sortcolumns`
    command-line option [(see
    HTML output)](html-examples/html_5_alphabetical.html).
-   Grep for columns, and also display outgoing relationships, using
    `-grepcolumns=.*\\.BOOKS\\..*\\.ID` as a command-line option, with
    an appropriate regular expression [(see
    HTML output)](html-examples/html_6_grep.html).
-   Grep for columns, but only show matching tables, using
    `-grepcolumns=.*\\.BOOKS\\..*\\.ID` and `-only-matching` as
    command-line options [(see
    HTML output)](html-examples/html_7_grep_onlymatching.html).
