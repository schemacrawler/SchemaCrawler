# Extensions with Catalog Attributes

SchemaCrawler can read and incorporate table and column remarks, and weak association definitions 
from a YAML file. Examples of attributes file are shown below. Then you can run SchemaCrawler with a
`--attributes-file <path>` command-line option. 

SchemaCrawler needs [Jackson](https://github.com/FasterXML/jackson) jars on the classpath to read 
the YAML file, and these provided with the SchemaCrawler download.


## Adding Table and Column Remarks

You can read and incorporate table and column remarks into the SchemaCrawler schema
by creating a file like the one below. These remarks will be show in SchemaCrawler output.

```yaml
name: catalog
tables:
- catalog: PUBLIC
  schema: BOOKS
  name: AUTHORS
  remarks:
  - Overwritten remarks line 1
  columns:
  - name: FIRSTNAME
    remarks:
    - Overwritten remarks line 1
    - Overwritten remarks line 2
    attributes:
      tag1: tagvalue1
  - name: LASTNAME
    remarks:
    - Overwritten remarks line 1
```

## Creating Weak Associations

You can create weak associations between columns of two tables (or even the same table)
by creating a file like the one below. These weak associations will be shown in 
SchemaCrawler output, if you edit the SchemaCrawler `/config/schemacrawler.config.properties` 
file, and uncomment `schemacrawler.format.show_weak_associations` and set it to be true.

Weak associations can be between columns of tables of the SchemaCrawler schema and columns
in external schemas. This is useful for documenting data lineage or other types of associations.
You can provide remarks for weak associations which will also be shown in SchemaCrawler output.

```yaml
name: catalog
weak-associations:
- name: multi_line_remarks
  referenced-table:
    catalog: PUBLIC
    schema: BOOKS
    name: AUTHORS
  referencing-table:
    catalog: PUBLIC
    schema: BOOKS
    name: BOOKS
  column-references:
    ID: ID
  remarks:
  - "Some remarks line 1"
  - "Some remarks line 2"
- name: multi_remarks_reference
  referenced-table:
    catalog: PUBLIC
    schema: PUBLISHER SALES
    name: SALES
  referencing-table:
    catalog: PRIVATE
    schema: ALLSALES
    name: REGIONS
  column-references:
    POSTALCODE: POSTALCODE
    COUNTRY: COUNTRY
  remarks:
  - "Other remarks line 1"
  - "Other remarks line 2"
```
