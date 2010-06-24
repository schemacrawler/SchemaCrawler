var dropTables = function()
{
  println(database.schemaCrawlerInfo);
  println(database.databaseInfo);
  println(database.jdbcDriverInfo);
  var statement = connection.createStatement();
  var schemas = database.schemas;
  for ( var i = 0; i < schemas.length; i++)
  {
    var tables = schemas[i].tables;
    for ( var j = (tables.length - 1); j >= 0; j--)
    {
      var table = tables[j];
      var sql = "DROP " + table.type + " " + table.fullName;
      println("Executing SQL: " + sql);
      try
      {
        statement.executeUpdate(sql);
      }
      catch (e)
      {
        println("");
        println(e.message);
        println("(Not dropping any more tables, due to exception)");
        return;
      }
    }
  }
};

dropTables();
