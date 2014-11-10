# SchemaCrawler Plugins

There are several ways to use and extend SchemaCrawler.

## Database Scripting
You can script against your database, using the scripting language of your choice. A live
database connection is provided. For more information, see [SchemaCrawler Database Scripting](scripting.html).

## Use SchemaCrawler in a Maven Project
To use SchemaCrawler in your [Apache Maven] projects, add a dependency to SchemaCrawler in your pom.xml,
as decribed in the [Getting Started](readme.html).

However, if you are creating new [Apache Maven] project, you can use a SchemaCrawler archetype.
To generate your new project, run:

`mvn archetype:generate -DgroupId=com.mycompany.app -DartifactId=my-app 
   -DarchetypeGroupId=us.fatehi -DarchetypeArtifactId=schemacrawler-archetype-maven-project 
   -DinteractiveMode=false` 

You can use any groupId and artifactId that you like. An example Java class
will be generated for you.

## Plugin New SchemaCrawler Commands
You can create a new SchemaCrawler command, and plug it into the SchemaCrawler framework. 
To generate your new SchemaCrawler command plugin project, run: 

`mvn archetype:generate -DgroupId=com.mycompany.app -DartifactId=my-command-plugin 
  -DarchetypeGroupId=us.fatehi -DarchetypeArtifactId=schemacrawler-archetype-plugin-command 
  -DinteractiveMode=false`

You can use any groupId and artifactId that you like. Example Java code
will be generated for you. Build your command jar file using `mvn package`. Then, copy 
the your command jar file into the SchemaCrawler lib directory. When you see a listing of commands, 
using `sc.cmd -help` (or `sc.sh -help` on Unix), your new command will be included.

Later, you can go back, and modify the code to do what you need it to. You can then rebuild, and redeploy to 
the SchemaCrawler lib directory.

## Plugin New SchemaCrawler Linters
You can create a new SchemaCrawler linter, and plug it into the SchemaCrawler framework. 
To generate your new SchemaCrawler lint plugin project, run: 
 
`mvn archetype:generate -DgroupId=com.mycompany.app -DartifactId=my-lint-plugin 
  -DarchetypeGroupId=us.fatehi -DarchetypeArtifactId=schemacrawler-archetype-plugin-lint 
  -DinteractiveMode=false`

You can use any groupId and artifactId that you like. Example Java code
will be generated for you. Build your lint jar file using `mvn package`. Then, copy 
the your lint jar file into the SchemaCrawler lib directory. When you run the lint command, 
your new linter will be included.

Later, you can go back, and modify the code to do what you need it to. You can then rebuild, and redeploy to 
the SchemaCrawler lib directory.

