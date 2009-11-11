var dropTables = function()
{
  println(database.databaseInfo);
  var statement = connection.createStatement();
  var catalogs = database.catalogs;
  for ( var c = 0; c < catalogs.length; c++)
  {
    var schemas = catalogs[c].schemas;
    for ( var i = 0; i < schemas.length; i++)
    {
      var tables = schemas[i].tables;
      for ( var j = 0; j < tables.length; j++)
      {
        try
        {
          println("Attempting to drop table: " + tables[j].fullName);
          statement.executeUpdate("DROP TABLE " + tables[j].fullName);
        }
        catch (e)
        {
          println(e);
          println("(Not dropping any more tables, due to exception)");
          break;
        }
      }
    }
  }
};

dropTables();
