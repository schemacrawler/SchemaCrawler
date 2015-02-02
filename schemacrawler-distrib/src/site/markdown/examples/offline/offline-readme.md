# SchemaCrawler - Offline Snapshot Example
 
SchemaCrawler allows you to save off your database metadata into an 
offline snapshot, for future use. Later, you can connect to this offline 
snapshot as you would to a regular database.

## How to Run
1. Follow the instructions in the [commandline](../commandline/commandline-readme.html) example. 
2. To create an offline snapshot, run 
   `sc.cmd -server=hsqldb -database=schemacrawler -user=sa -password= -infolevel=maximum -command=serialize -o=offline.db` 
   (use `sc.sh` instead of `sc.cmd` on Unix)
3. To use the offline snapshot, run 
   `sc.cmd -server=offline -database=offline.db -infolevel=standard -command=schema` 
   (use `sc.sh` instead of `sc.cmd` on Unix)
   
## How to Experiment
1. Try other SchemaCrawler commands with the offline snapshot.
