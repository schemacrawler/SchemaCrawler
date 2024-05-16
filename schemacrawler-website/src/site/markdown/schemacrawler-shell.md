# SchemaCrawler Interactive Shell

SchemaCrawler Interactive Shell is an interactive shell environment that allows you to work with your database metadata.

The easiest way to learn how to use the SchemaCrawler Interactive Shell is by doing the 
[live online tutorial](https://killercoda.com/schemacrawler). The tutorial works from within 
any browser with no software or plugins needed.

You can connect to a database, load the schema metadata catalog, and then execute commands against the metadata.

In order to use SchemaCrawler Interactive Shell, download the [latest SchemaCrawler distribution](https://www.schemacrawler.com/downloads.html#running-examples-locally/).
Unzip it, and follow instructions in the `shell` example included with the distribution. 
You can start the SchemaCrawler Interactive Shell from the command-line with a `--shell` argument. You can also download [platform-specific SchemaCrawler installers](https://github.com/schemacrawler/SchemaCrawler-Installers/releases/) to use the SchemaCrawler Interactive Shell.

Once the SchemaCrawler Interactive Shell starts up, you will get the `schemacrawler>` command-prompt, and you can interact with SchemaCrawler using shell commands.

Example shell commands look like:

```sh
help
connect --server <server> --host <host> --user <host> --password <password> --database <database>
load --info-level maximum
execute --command list
```

Results of command output can be redirected into a file. You can also generate schema diagrams.
The schema metadata remains loaded as long as the shell is open, even if the database connection is closed.


## SchemaCrawler Sequence of Operation

SchemaCrawler has a number of phases when executing a command. The phases run in this order, whether you are using the command-line or the SchemaCrawler interactive shell.

1. **connect**: The first thing SchemaCrawler does is to connect to your database. There are two ways to connect - using a JDBC database connection URL, or, if a database plugin is available, using the server, host, port and database method. 
   For more help, run `help connect` in the SchemaCrawler interactive shell, or `-h connect` from the command-line. To get a list of all available database plugins, run `help servers`, or `-h servers` from the command-line. To get specific help for a database plugin, run a command similar to `help server:mysql`, or `-h server:mysql`. And `help drivers` (`-h drivers`) to get a list of available JDBC drivers, so you can research on the internet about how to connect using a database connection URL.
2. **limit**: Limit the tables and routines loaded into memory. This is an optional step, but is a good idea for speed of operation. Logically, think of limiting as if to say that schemas or tables that are not included do not exist. 
   For more help, run `help limit` in the SchemaCrawler interactive shell, or `-h limit` from the command-line.
3. **grep**: Search for metadata using regular expression. You can search for regular expressions using column names or search within column definitions. This is an optional step. 
   For more help, run `help grep` in the SchemaCrawler interactive shell, or `-h grep` from the command-line.
4. **filter**: Filters table based on foreign key relationships, also called parent-child relationships. You can filter to any number of generations. This is an optional step. 
For more help, run `help filter` in the SchemaCrawler interactive shell, or `-h filter` from the command-line.
5. **load**: Loads the database schema metadata into memory. If you change any of the limit, grep, or filter options, you will need to load database schema metadata again in the SchemaCrawler interactive shell. This is because during the load process, SchemaCrawler eliminates any tables that are not matched by limit, grep, or filter operations. The load command uses multiple catalog loaders, which run sequentially. You provide options for these loaders with the `load` command.
   For more help, run `help load` in the SchemaCrawler interactive shell, or `-h load` from the command-line. To get a list of all available commands, run `help loaders`, or `-h loaders` from the command-line. To get specific help for a database plugin, run a command similar to `help loader:weakassociationsloader`, or `-h loader:weakassociationsloader`.
6. **execute**: Executes a SchemaCrawler command, including commands to generate diagrams. Numerous commands are available, and more can be plugged-in. 
   For more help, run `help execute` in the SchemaCrawler interactive shell, or `-h execute` from the command-line. To get a list of all available commands, run `help commands`, or `-h commands` from the command-line. To get specific help for a database plugin, run a command similar to `help command:schema`, or `-h command:schema`.



The live online tutorial on the [interactive shell](https://killercoda.com/schemacrawler/scenario/schemacrawler-shell) walks you through these concepts to help you understand them.
