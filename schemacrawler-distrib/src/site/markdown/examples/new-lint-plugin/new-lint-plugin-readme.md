# SchemaCrawler - New SchemaCrawler Lint Plugin

## Description
SchemaCrawler comes with Apache Maven archetypes to create new Apache Maven projects.
You can use these archetypes to very quickly create a new SchemaCrawler lint plugin.

## How to Run
1. Install [Apache Maven](http://maven.apache.org/), and make sure that Apache Maven is on your PATH 
2. Make sure that java is on your PATH
3. Start a command shell in the `new-lint-plugin` example directory 
4. Run `mvn archetype:generate -DgroupId=com.mycompany -DartifactId=my-lint-plugin
  -DarchetypeGroupId=us.fatehi -DarchetypeArtifactId=schemacrawler-archetype-plugin-lint -DarchetypeVersion=14.10.03
  -DinteractiveMode=false` to generate your new SchemaCrawler lint plugin project (you can use any groupId and artifactId that you like)
5. Check the output your new Apache Maven project
6. Build your lint jar file, using `mvn package`
7. Copy the your lint jar file into the SchemaCrawler lib directory
8. When you run the lint command, your new linter will be included

## How to Experiment
1. Modify the example code, and get it doing what you need it to do. 
