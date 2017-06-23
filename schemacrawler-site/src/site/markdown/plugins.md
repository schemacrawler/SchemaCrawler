# SchemaCrawler Plugins

There are several ways to use and extend SchemaCrawler.

## Database Scripting
You can script against your database, using the scripting language of your choice. A live
database connection is provided. For more information, see [SchemaCrawler Database Scripting](scripting.html).

## Use SchemaCrawler in anApache Maven Project
To use SchemaCrawler in your [Apache Maven] projects, add a dependency to SchemaCrawler in your pom.xml,
as described in the [Getting Started](readme.html).

However, if you are creating new [Apache Maven] project, you can use a SchemaCrawler archetype.
To generate your new project, run:

`mvn archetype:generate -DgroupId=com.mycompany -DartifactId=my-app 
   -DarchetypeGroupId=us.fatehi -DarchetypeArtifactId=schemacrawler-archetype-maven-project 
   -DinteractiveMode=false` 

You can use any groupId and artifactId that you like. An example Java class
will be generated for you.

## Plugin New SchemaCrawler Commands
You can create a new SchemaCrawler command, and plug it into the SchemaCrawler framework. 
To generate your new SchemaCrawler command plugin project, run: 

`mvn archetype:generate -DgroupId=com.mycompany -DartifactId=my-command-plugin 
  -DarchetypeGroupId=us.fatehi -DarchetypeArtifactId=schemacrawler-archetype-plugin-command 
  -DinteractiveMode=false`

You can use any groupId and artifactId that you like. Example Java code
will be generated for you. Build your command jar file using `mvn package`. Then, copy 
the your command jar file into the SchemaCrawler lib directory. When you see a listing of commands, 
using `schemacrawler.cmd -help` (or `schemacrawler.sh -help` on Unix), your new command will be included.

Later, you can go back, and modify the code to do what you need it to. You can then rebuild, and redeploy to 
the SchemaCrawler lib directory.

## Plugin New SchemaCrawler Linters
You can create a new SchemaCrawler linter, and plug it into the SchemaCrawler framework. 
To generate your new SchemaCrawler lint plugin project, run: 
 
`mvn archetype:generate -DgroupId=com.mycompany -DartifactId=my-lint-plugin 
  -DarchetypeGroupId=us.fatehi -DarchetypeArtifactId=schemacrawler-archetype-plugin-lint 
  -DinteractiveMode=false`

You can use any groupId and artifactId that you like. Example Java code
will be generated for you. Build your lint jar file using `mvn package`. Then, copy 
the your lint jar file into the SchemaCrawler lib directory. When you run the lint command, 
your new linter will be included.

Later, you can go back, and modify the code to do what you need it to. You can then rebuild, and redeploy to 
the SchemaCrawler lib directory.

## Plugin New SchemaCrawler Database Connectors
You can create a new SchemaCrawler connector for a database, and plug it into the SchemaCrawler framework. 
To generate your new SchemaCrawler database connector plugin project, run: 
 
`mvn archetype:generate -DgroupId=com.mycompany -DartifactId=my-dbconnector-plugin 
  -DarchetypeGroupId=us.fatehi -DarchetypeArtifactId=schemacrawler-archetype-plugin-dbconnector 
  -DinteractiveMode=false`

You can use any groupId and artifactId that you like. Example Java code
will be generated for you. Build your database connector jar file using `mvn package`. Then, copy 
the your database connector jar file into the SchemaCrawler lib directory. 

Later, you can go back, and modify the code to do what you need it to. You can then rebuild, and redeploy to 
the SchemaCrawler lib directory. Now you will be able to connect to a new database server type, by using your
new `-server=` argument, and providing host, port, and database information needed to construct the 
database connection URL.


