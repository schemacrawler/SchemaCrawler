# SchemaCrawler HTML Output

SchemaCrawler generates clean HTML and text output. You can filter out tables, views, columns, stored procedure and functions based on regular expressions, using grep functionality.

SchemaCrawler is unique among database documentation tools in that you do not need to know the table names or column names that you are interested in. All you need is a search expression, in the form of a regular expression. SchemaCrawler has powerful command-line options to match tables, and then find other related tables, whether they are parent or child tables. If your schema changes, you can simply regenerate the diagram, without having to know the exact changes that were made.


## Text Output Options

SchemaCrawler outputs complete details of your database schema [(see HTML output)](html-examples/html.html), and in addition offers several options to change what you see on the database diagram. Here are a few variations, with examples in SchemaCrawler's HTML output format:

- Suppress schema names and foreign key names, using the `-portablenames` command line option [(see HTML output)](html-examples/html_2_portablenames.html).
- Show significant columns, such as primary and foreign key columns, and columns that are part of unique indexes. Use the `-infolevel=standard -command=brief` command line option [(see HTML output)](html-examples/html_3_important_columns.html).
- Show column ordinals, by setting configuration option `schemacrawler.format.show_ordinal_numbers=true` in the configuration file [(see HTML output)](html-examples/html_4_ordinals.html).
- Display columns in alphabetical order, using the `-sortcolumns` command line option [(see HTML output)](html-examples/html_5_alphabetical.html).
- Grep for columns, and also display outgoing relationships, using `-grepcolumns=.*\\.BOOKS\\..*\\.ID` as a command line option, with an appropriate regular expression [(see HTML output)](html-examples/html_6_grep.html).
- Grep for columns, but only show matching tables, using `-grepcolumns=.*\\.BOOKS\\..*\\.ID` and `-only-matching` as command line options [(see HTML output)](html-examples/html_7_grep_onlymatching.html).
