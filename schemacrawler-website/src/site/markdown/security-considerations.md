# Security Considerations

SchemaCrawler is constantly updated to make sure it is using the latest dependencies on
third-party libraries, and to keep the code up to date based on recommendations from
security scanning tools. Please follow the guidelines below to use SchemaCrawler in as
secure a way as possible. 


## Diagramming

SchemaCrawler relies on Graphviz to generate schema diagrams. Graphviz needs to be installed
on the system where SchemaCrawler will run, and SchemaCrawler invokes Graphviz from the
system PATH during its execution. 

This functionality is provided in the "schemacrawler-diagram"
jar file. If you do not plan to generate schema diagrams, delete the "schemacrawler-diagram"
jar file from your distribution. If you are using SchemaCrawler programmatically, remove the
dependency on "schemacrawler-diagram".


## Scripting

SchemaCrawler can execute scripts in Python or JavaScript. SchemaCrawler cannot vouch for the security of
the scripts that are executed. 

This functionality is provided in the "schemacrawler-scripting"
jar file. If you do not plan to run scripts with SchemaCrawler, delete the "schemacrawler-scripting"
jar file from your distribution. If you are using SchemaCrawler programmatically, remove the
dependency on "schemacrawler-scripting".


## Offline Snapshots

SchemaCrawler can save snapshots of the database schema metadata model, and load them in later, 
even when the source database is offline. This is done by using standard Java serialization. 
SchemaCrawler cannot guarantee that the metadata model is not used an malicious attack vector.

This functionality is provided in the "schemacrawler-offline"
jar file. If you do not plan to use SchemaCrawler offline snapshots, delete the "schemacrawler-offline"
jar file from your distribution. If you are using SchemaCrawler programmatically, remove the
dependency on "schemacrawler-offline".


## Microsoft SQL Server

SchemaCrawler is distributed with a version of Microsoft SQL Server JDBC driver that is higher than 10, as 
well as the SchemaCrawler database plugin for Microsoft SQL Server. This plugin provides enhanced 
functionality when obtaining Microsoft SQL Server database metadata. The plugin also allows you to connect 
using the server, host and port method (using the `--server`, `--host` and `--port` command-line arguments 
or programmatic equivalents). When using the server, host and port method however, SchemaCrawler turns off 
encrypted connections with the database server.

When using SchemaCrawler to connect to Microsoft SQL Server, the recommended advice is to install a 
trusted certificate on your server, and use url method of communication (using the `--url` command-line 
argument or programmatic equivalent) and making sure to include `encrypt=true` in the connection URL.

