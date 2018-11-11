# SchemaCrawler Interactive Shell

SchemaCrawler Interactive Shell is an interactive shell environment that allows you to work with your database metadata. 
You can connect to a database, load the schema metadata catalog, and then execute commands against the metadata.

In order to use SchemaCrawler Interactive Shell, 
download the [latest SchemaCrawler distribution](http://github.com/schemacrawler/SchemaCrawler/releases/). 
Unzip it, and follow instructions in the `shell` example included with the distribution.

Example commands look like:

```sh
connect -server <server> -host <host> -user <host> -password <password> -database <database>
load-catalog -infolevel maximum
execute -command list
```

Results of command output can be redirected into a file. You can also generate schema diagrams. 
The schema metadata remains loaded as long as the shell is open, even if the database connection is closed.
