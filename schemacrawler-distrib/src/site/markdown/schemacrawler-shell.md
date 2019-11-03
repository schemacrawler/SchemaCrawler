# SchemaCrawler Interactive Shell

SchemaCrawler Interactive Shell is an interactive shell environment that allows you to work with your database metadata. 

The easiest way to learn how to use the SchemaCrawler Interactive Shell is by doing the [online tutorial on Katacoda](https://www.katacoda.com/schemacrawler/scenarios/schemacrawler-shell).

You can connect to a database, load the schema metadata catalog, and then execute commands against the metadata.

In order to use SchemaCrawler Interactive Shell, download the [latest SchemaCrawler distribution](http://github.com/schemacrawler/SchemaCrawler/releases/). 
Unzip it, and follow instructions in the `shell` example included with the distribution. You can start the SchemaCrawler Interactive Shell from the command-line with a `--shell` argument.

Once the SchemaCrawler Interactive Shell starts up, you will get the `schemacrawler>` command-prompt, and you can interact with SchemaCrawler using shell commands.

Example shell commands look like:

```sh
help
connect --server <server> --host <host> --user <host> --password <password> --database <database>
load-catalog --info-level maximum
execute --command list
```

Results of command output can be redirected into a file. You can also generate schema diagrams. 
The schema metadata remains loaded as long as the shell is open, even if the database connection is closed.