# SchemaCrawler - New SchemaCrawler Database Connector Plugin

## Description
SchemaCrawler comes with Apache Maven archetypes to create new Apache Maven projects.
You can use these archetypes to very quickly create a new SchemaCrawler database connector plugin.

## How to Run
1. Install [Apache Maven](http://maven.apache.org/), and make sure that Apache Maven is on your PATH 
2. Make sure that java is on your PATH
3. Start a command shell in the `new-dbconnector-plugin` example directory 
4. Run `mvn archetype:generate -DgroupId=com.mycompany -DartifactId=my-dbconnector-plugin 
  -DarchetypeGroupId=us.fatehi -DarchetypeArtifactId=schemacrawler-archetype-plugin-dbconnector -DarchetypeVersion=14.09.03
  -DinteractiveMode=false` to generate your new SchemaCrawler database connector plugin project 
  (you can use any groupId and artifactId that you like)
5. Check the output your new Apache Maven project
6. Build your database connector jar file, using `mvn package`
7. Copy the your database connector jar file into the SchemaCrawler lib directory
8. When you try to connect to the new database using the `-server` command, your new database connector will be used

## How to Experiment
1. Modify the example code, and get it doing what you need it to do. 
