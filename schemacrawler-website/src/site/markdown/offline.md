# SchemaCrawler Offline Snapshot

SchemaCrawler allows you to save off your database metadata into an 
offline snapshot, for future use, using the [`serialize`](serialize.html) command. 
Later, you can connect to this offline snapshot as you would to a regular database. 
This allows you to store historical versions of your schema, or to query your 
database even if you no longer have access to it. Please make sure that you use
the same version of SchemaCrawler to serialize snapshots as you use when you 
load those offline snapshots.

You can use any of the rich SchemaCrawler functionality with offline 
snapshots, including grep, diagramming, scripting, templating and 
producing output in a variety of formats. 

## How to Create an Offline Snapshot

Use the [`serialize`](serialize.html) command with Java serialization.
It is best to create the snapshot with the `--info-level=maximum`
and no [limit, filter or grep options](schemacrawler-shell.html).

## How to Use an Offline Snapshot

In order to connect to an offline snapshot, use the `offline` database
server type, and then use any SchemaCrawler command that you would like
to use. You can apply [limit, filter or grep options](schemacrawler-shell.html)
too.

Use the following command-line options to "connect" to your offline catalog,
assuming that you have previously serialized to a file called "offline_db.ser": 
`--server=offline --database=offline_db.ser`
No username and password are required.
