# SchemaCrawler - New SchemaCrawler Command Plugin

## Description
SchemaCrawler comes with Apache Maven archetypes to create new Apache Maven projects.
You can use these archetypes to very quickly create a new SchemaCrawler command plugin.

## How to Run
1. Install [Apache Maven](http://maven.apache.org/), and make sure that Apache Maven is on your PATH 
2. Make sure that java is on your PATH
3. Start a command shell in the `new-command-plugin` example directory 
4. Run `mvn archetype:generate -DgroupId=com.mycompany -DartifactId=my-command-plugin 
  -DarchetypeGroupId=us.fatehi -DarchetypeArtifactId=schemacrawler-archetype-plugin-command -DarchetypeVersion=14.10.06
  -DinteractiveMode=false` to generate your new SchemaCrawler command plugin project (you can use any groupId and artifactId that you like)
5. Check the output your new Apache Maven project
6. Build your command jar file, using `mvn package`
7. Copy the your command jar file into the SchemaCrawler lib directory
8. See a listing of commands, including your new command, using `schemacrawler.cmd -help` (or `schemacrawler.sh -help` on Unix)

## How to Experiment
1. Modify the example code, and get it doing what you need it to do. 
