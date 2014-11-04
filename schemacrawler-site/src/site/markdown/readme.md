# Getting Started

## FAQs
Before downloading SchemaCrawler, be sure to read the [FAQs] and take a look at the [resources].

## Examples
The first thing to try is the [SchemaCrawler examples].

Several examples on how to use SchemaCrawler on the command line, as an [ant] task or [Maven] plugin, 
as an API, how to script your database using JavaScript, [Groovy],
[Ruby] or [Python], how to create database diagrams with [Graphviz], how to integrate with the 
[Spring Framework], and how to use the [Apache Velocity] (templating) integration are provided with the 
[SchemaCrawler examples] download.

## How-tos
Once you start getting comfortable with SchemaCrawler, and need to know more about how to do things, 
read the [how-tos] section.

# Using SchemaCrawler in Your Projects

## Maven Projects
In order to use SchemaCrawler in your [Apache Maven] projects, add a dependency to SchemaCrawler in your pom.xml.

<div class="source"><pre>
&lt;dependencies&gt;
  ...
  &lt;dependency&gt;
    &lt;groupId&gt;us.fatehi&lt;/groupId&gt;
    &lt;artifactId&gt;schemacrawler&lt;/artifactId&gt;
    &lt;version&gt;11.02.01&lt;/version&gt;
  &lt;/dependency&gt;
&lt;/dependencies&gt;
</pre></div>

## OSGi
The SchemaCrawler jar file is bundled as an [OSGi bundle], and can be deployed into your OSGi Service Platform 
and your OSGi Bundle Repository.

# Building From the Source Code

## Maven Build
The Maven build is a comprehensive build that runs unit tests, constructs the SchemaCrawler jar, and can 
also create the project web-site. Download [Apache Maven] Then run `mvn package` from the SchemaCrawler directory. 
The SchemaCrawler distribution will be created in the `target` of the `distribution` module.		

## Eclipse
Before importing the various SchemaCrawler projects into Eclipse, make sure that you have a successful 
Maven build. Maven will download all the dependencies needed to build SchemaCrawler. Also, install the 
[m2e Maven Integration for Eclipse] plugin.

## Proprietary JDBC Drivers
The Maven build depends on some proprietary JDBC drivers for IBM DB2 and Oracle. Download the [IBM DB2 JDBC drivers] 
place them in the schemacrawler-db2 source directory, and install them into your local Maven repository using 
the provided install command. Similarly, download the [Oracle JDBC drivers] put them into the schemacrawler-oracle 
source directory, and install them locally.

# Acknowledgements

## Clover
SchemaCrawler is grateful to Atlassian for providing a license for [Clover].


[FAQs]: faq.html
[resources]: resources.html
[SchemaCrawler examples]: https://sourceforge.net/projects/schemacrawler/files/SchemaCrawler%20Examples/
[ant]: http://ant.apache.org/
[Maven]: http://maven.apache.org/
[Groovy]: http://groovy.codehaus.org/
[Ruby]: http://www.ruby-lang.org/en/
[Python]: http://www.python.org/
[Graphviz]: http://www.graphviz.org/
[Spring Framework]: http://www.springsource.org/spring-framework
[Apache Velocity]: http://velocity.apache.org/
[SchemaCrawler examples]: https://sourceforge.net/projects/schemacrawler/files/SchemaCrawler%20Examples/
[Apache Maven]: http://maven.apache.org/
[OSGi bundle]: http://en.wikipedia.org/wiki/OSGi#Bundles
[m2e Maven Integration for Eclipse]: http://eclipse.org/m2e/
[IBM DB2 JDBC drivers]: http://www-306.ibm.com/software/data/db2/express/download.html
[Oracle JDBC drivers]: http://www.oracle.com/technetwork/database/enterprise-edition/jdbc-112010-090769.html
[Clover]: http://www.atlassian.com/software/clover/
[how-tos]: how-to.html
