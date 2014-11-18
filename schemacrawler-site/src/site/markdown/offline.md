# Offline Snapshot
 
SchemaCrawler allows you to save off your database metadata into an 
offline snapshot, for future use. Later, you can connect to this offline 
snapshot as you would to a regular database. This allows you to store 
historical versions of your schema, or to query your database even if 
you no longer have access to it. 

You can use any of the rich SchemaCrawler functionality with offline 
snapshots, including grep, diagramming, scripting, templating and 
producing output in a variety of formats. 

## Creating an Offline Snapshot
 
In order to create an offline snapshot, simply use SchemaCrawler to 
connect to your database, but use the `serialize` command. Direct the 
output to a file, and make sure you preserve this output file. 

Use the following command-line options in addition to the ones you use 
to connect to your database: `-c=serialize -o=offline_db.xml` 

## Using an Offline Snapshot

In order to connect to an offline snapshot, use the `offline` database
server type, and then use any SchemaCrawler command that you would like
to use.

Use the following command-line options in addition to the command: 
`-server=offline -database=offline_db.xml`
