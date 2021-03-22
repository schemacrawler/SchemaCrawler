# Weak Associations

Weak associations are inferred associations between tables, similar to foreign keys, 
even if there is no foreign key explicitly defined in the database schema between the 
tables. Ruby on Rails table schemes are supported, as well as other simple naming 
conventions. Table name prefixes are automatically detected. Weak associations are 
output in the schema diagrams as a dotted line, and are also output in the text formats.

You can use the `--weak-associations=true` to infer and
display weak associations on schema diagrams and text output.

In addition, you can [define your own weak attributes in an attributes file](attributes.html) 
and have them loaded in and displayed in SchemaCrawler output.
