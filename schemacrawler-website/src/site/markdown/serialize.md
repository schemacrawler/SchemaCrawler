# SchemaCrawler Serialization

SchemaCrawler allows it's underlying schema metadata model to be serialized into 
various formats that can be programatically consumed by other applications and systems. 
SchemaCrawler's native serialization format uses Java serialization, and is tied
to a given version of Java and a given version of SchemaCrawler. This type of 
serialization is most useful when using the SchemaCrawler Interactive Shell,
since you can save off the schema metadata model to be used in a later shell
session, in which case you do not need an active connection to the database.
SchemaCrawler does not offer deserialization using formats other than Java
serialization.

SchemaCrawler needs [Jackson](https://github.com/FasterXML/jackson) jars on the 
classpath to serialize to JSON and YAML, and these can be downloaded with the 
download tool provided with the SchemaCrawler download.


## How to Serialize a Catalog

SchemaCrawler serialization can be run using the 
`--command=serialize` command-line option. The serialized schema metadata model
will be saved to a file.

For more details, see the `serialize` example in the 
[SchemaCrawler examples](http://github.com/schemacrawler/SchemaCrawler/releases/) 
download.

SchemaCrawler serialization can produce output in Java,
[JavaScript object notation (JSON)](snapshot-examples/snapshot.json) or
[YAML](snapshot-examples/snapshot.yaml) format. 
(Click on the links for example output.) 
A serialized schema metadata model will be produced in the format specified using the 
`--output-format` command-line option. For example,
`--output-format=json` will generate a output in JSON format.

## How to Load a Serialized Catalog

You can load a serialized version of a catalog using the [`offline`](offline.html)
server type. Please make sure that you use the same version of SchemaCrawler to 
serialize snapshots as you use when you load those offline snapshots.
