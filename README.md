[![Build Status](https://travis-ci.org/sualeh/SchemaCrawler.svg?branch=master)](https://travis-ci.org/sualeh/SchemaCrawler)

# SchemaCrawler

**[See SchemaCrawler website](http://www.SchemaCrawler.com/)** 

SchemaCrawler is a free database schema discovery and comprehension tool. SchemaCrawler has a good mix of useful features for data governance. You can [search for database schema objects](http://sualeh.github.io/SchemaCrawler/schemacrawler_grep.html) using regular expressions, and output the schema and data in a readable text format. The output serves for database documentation, and is designed to be [diff-ed](http://en.wikipedia.org/wiki/Diff) against other database schemas. SchemaCrawler also generates [schema diagrams.](http://sualeh.github.io/SchemaCrawler/diagramming.html) You can [execute scripts](http://sualeh.github.io/SchemaCrawler/scripting.html) in any standard scripting language against your database. You can find potential schema design issues with [lint.](http://sualeh.github.io/SchemaCrawler/lint.html)

SchemaCrawler supports almost any database that has a JDBC driver, but for convenience is [bundled with drivers](http://sualeh.github.io/SchemaCrawler/database-support.html) for some commonly used RDBMS systems. SchemaCrawler works with any operating system that supports Java SE 8 or better.

SchemaCrawler is also a Java API that makes working with database metadata as
easy as working with plain old Java objects. In order to use SchemaCrawler in your [Apache Maven] projects, add a dependency to SchemaCrawler in your pom.xml.

<div class="source"><pre>
&lt;dependencies&gt;
  ...
  &lt;dependency&gt;
    &lt;groupId&gt;us.fatehi&lt;/groupId&gt;
    &lt;artifactId&gt;schemacrawler&lt;/artifactId&gt;
    &lt;version&gt;14.01.01&lt;/version&gt;
  &lt;/dependency&gt;
&lt;/dependencies&gt;
</pre></div>
