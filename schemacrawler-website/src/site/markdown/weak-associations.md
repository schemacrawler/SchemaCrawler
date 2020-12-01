# Weak Associations

Weak associations are inferred associations between tables, similar to foreign keys, 
even if there is no foreign key explicitly defined in the database schema between the 
tables. Ruby on Rails table schemes are supported, as well as other simple naming 
conventions. Table name prefixes are automatically detected. Weak associations are 
output in the schema diagrams as a dotted line, and are also output in the text formats.

You can use the `--weak-associations=true` or `--weak-associations=false` to control the 
display of weak associations on schema diagrams and text output. You can also use the 
`schemacrawler.format.show_weak_associations=true` option in the 
`schemacrawler.config.properties` file to show weak associations.
