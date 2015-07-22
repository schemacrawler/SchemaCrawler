[![Build Status](https://travis-ci.org/sualeh/SchemaCrawler.svg?branch=master)](https://travis-ci.org/sualeh/SchemaCrawler)

# SchemaCrawler

**[See SchemaCrawler website](http://www.SchemaCrawler.com/)** 

SchemaCrawler is a free database schema discovery and comprehension tool. SchemaCrawler has a good mix of useful features for data governance. You can [search for database schema objects](http://sualeh.github.io/SchemaCrawler/schemacrawler_grep.html) using regular expressions, and output the schema and data in a readable text format. The output serves for database documentation, and is designed to be [diff-ed](http://en.wikipedia.org/wiki/Diff) against other database schemas. SchemaCrawler also generates [schema diagrams.](http://sualeh.github.io/SchemaCrawler/diagramming.html) You can [execute scripts](http://sualeh.github.io/SchemaCrawler/scripting.html) in any standard scripting language against your database. You can find potential schema design issues with [lint.](http://sualeh.github.io/SchemaCrawler/lint.html)

SchemaCrawler supports almost any database that has a JDBC driver, but for convenience is [bundled with drivers](http://sualeh.github.io/SchemaCrawler/database-support.html) for some commonly used RDBMS systems. SchemaCrawler works with any operating system that supports Java SE 8 or better.

SchemaCrawler comes with a set of command-line tools that allow database
metadata to be output as [plain text,](http://sualeh.github.io/SchemaCrawler/snapshot-examples/snapshot.text) 
[comma-separated text (CSV),](http://sualeh.github.io/SchemaCrawler/snapshot-examples/snapshot.csv) 
[HTML5,](http://sualeh.github.io/SchemaCrawler/snapshot-examples/snapshot.html)
[HTML5 with embedded diagrams,](http://sualeh.github.io/SchemaCrawler/snapshot-examples/snapshot.htmlx) or 
[JavaScript object notation (JSON).](http://sualeh.github.io/SchemaCrawler/snapshot-examples/snapshot.json)
The HTML5 output is a combination of valid XML (that
can be manipulated by XML tools or XSLT), and HTML that can be viewed in a
browser. All formats are designed to be easy to
[diff](http://en.wikipedia.org/wiki/Diff) , or find differences with other
schemas that may have been output in the same format.

SchemaCrawler is also a Java API that makes working with database metadata as
easy as working with plain old Java objects. In order to use SchemaCrawler in your [Apache Maven](https://maven.apache.org/) projects, add a dependency to SchemaCrawler in your pom.xml.

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


