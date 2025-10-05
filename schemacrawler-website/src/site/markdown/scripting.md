# SchemaCrawler Database Scripting

SchemaCrawler is a command-line tool that allows you to script against your
database, using JavaScript or [Python](https://www.python.org/).
(SchemaCrawler supports any scripting language supported on the JDK.) Database
meta-data is provided to your script, as the "database" object, and you can
use any of the API methods to obtain details about your schema. A live
database connection is provided to your script as the "connection" object. You
can use standard JDBC to script against the database.

For more details, see scripting example in the 
[SchemaCrawler examples](https://www.schemacrawler.com/downloads.html#running-examples-locally/)
download, in the `examples\javascript` and
`examples\python` directories.

An hypothetical example of SchemaCrawler JavaScript support is in the
following script, which attempts to drop all the tables in the database.

```javascript
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
```

**Note:** SchemaCrawler's scripting and templating functionality allows the execution of arbitrary scripts or templates.
It is recommended not to distribute the SchemaCrawler scripting jar file in production, unless you have an explicit need
for this functionality. Do not include this dependency in your SchemaCrawler project unless you need it.
