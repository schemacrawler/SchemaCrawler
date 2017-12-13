# SchemaCrawler Database Scripting

SchemaCrawler is a command-line tool that allows you to script against your
database, using JavaScript, [Groovy](http://www.groovy-lang.org/),
[Ruby](http://www.ruby-lang.org/en/) or [Python](https://www.python.org/).
(SchemaCrawler supports any scripting language supported on the JDK.) Database
meta-data is provided to your script, as the "database" object, and you can
use any of the API methods to obtain details about your schema. A live
database connection is provided to your script as the "connection" object. You
can use standard JDBC to script against the database.

For more details, see scripting example in the 
[SchemaCrawler examples](http://github.com/sualeh/SchemaCrawler/releases/)
download, in the `examples\javascript`, `examples\groovy`, `examples\ruby` and
`examples\python` directories.

An hypothetical example of SchemaCrawler JavaScript support is in the
following script, which attempts to drop all the tables in the database.

<div class="source"><pre> 
var dropTables = function()
{
  println(catalog.schemaCrawlerInfo);
  println(catalog.databaseInfo);
  println(catalog.jdbcDriverInfo);
  var statement = connection.createStatement();
  var tables = catalog.tables.toArray();
  for ( var i = (tables.length - 1); i &gt;= 0; i--)
  {
    var table = tables[i];
    var sql = &quot;DROP &quot; + table.type + &quot; &quot; + table.fullName;
    println(&quot;Executing SQL: &quot; + sql);
    try
    {
      statement.executeUpdate(sql);
    } catch (e)
    {
      println(&quot;&quot;);
      println(e.message);
      println(&quot;(Not dropping any more tables, due to exception)&quot;);
      return;
    }
  }
};

dropTables();      
</pre></div>
