# SchemaCrawler - Interactive Shell Example

## Description
The command example demonstrates the use of the SchemaCrawler Interactive Shell.

## How to Run
1. Make sure that java is on your PATH
2. Start the test database server by following instructions in the `_testdb/README.html` file
3. Start a system command shell (`cmd` on Windows or `bash` on Linux) in the SchemaCrawler distribution directory, that is, the `_schemacrawler` directory.
4. Run `schemacrawler.cmd --shell` (or `schemacrawler.sh --shell` on Unix), to start the SchemaCrawler Interactive Shell.
5. In the SchemaCrawler Interactive Shell,
    1. Type `help` to get detailed help on all the commands available
    2. Type `connect --server hsqldb --user sa --password= --database schemacrawler` to connect to the database
    3. Type `sys --is-connected` to verify the connection
    4. Type `load --info-level standard` to load metadata
    5. Type `sys --is-loaded` to verify that metadata is loaded
    6. Type `execute --command brief` to run the "brief" command
    7. Type `quit` to quit the SchemaCrawler Interactive Shell

## How to Experiment
1. Try using different SchemaCrawler Interactive Shell commands, such as "limit", and see how they affect the output.
