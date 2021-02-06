# Extensions with Catalog Attributes

SchemaCrawler can read and incorporate table and column remarks from a YAML file. Create a file
with remarks similar to the one below. Then you can run SchemaCrawler with a
`--attributes-file <path>` command-line option. 

SchemaCrawler needs [Jackson](https://github.com/FasterXML/jackson) jars on the classpath to read 
the YAML file, and these can be downloaded with the download tool provided with the SchemaCrawler 
download.


## Example Attributes File

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
