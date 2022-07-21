# Modeling Foreign Keys

## Context and Problem Statement

Find the best way to model foreign keys. Model foreign keys consistently with a table's other constraints. Also make both exported and imported keys available (directly referenced) from tables.


## Considered Options

- One way is to view foreign-keys as maps between two tables. Make both exported and imported keys available from tables. There is no dependency between tables and foreign keys. Lookup keys do not include schema references.
- Or model foreign keys more consistently with the SQL standard view, as a constraint on the referencing table. However, make the foreign key available as an exported key on the referenced table. Lookup keys will include schema references to the referencing or importing table that contains the foreign key. It is easier to replace table constraints with foreign keys since they have the same lookup keys. The foreign key will usually have more schema information then the table constraint.


## Decision Outcome

In previous versions of SchemaCrawler, foreign keys were viewed as maps between two tables. From 16.16.9 onwards, SchemaCrawler views foreign keys more consistently with the SQL standard view.
