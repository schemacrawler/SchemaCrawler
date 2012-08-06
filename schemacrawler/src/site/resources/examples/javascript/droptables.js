var dropTables = function()
{
  println(database.schemaCrawlerInfo);
  println(database.databaseInfo);
  println(database.jdbcDriverInfo);
  var statement = connection.createStatement();
  var tables = database.tables.toArray();
  for ( var i = (tables.length - 1); i >= 0; i--)
  {
    var table = tables[i];
    var sql = "DROP " + table.type + " " + table.fullName;
    println("Executing SQL: " + sql);
    try
    {
      statement.executeUpdate(sql);
    } catch (e)
    {
      println("");
      println(e.message);
      println("(Not dropping any more tables, due to exception)");
      return;
    }
  }
};

dropTables();
