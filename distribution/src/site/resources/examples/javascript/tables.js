var printDatabase = function()
{
  println(database.schemaCrawlerInfo);
  println(database.databaseInfo);
  println(database.jdbcDriverInfo);

  var schemas = database.schemas;
  for ( var i = 0; i < schemas.length; i++)
  {
    var schema = schemas[i];
    println(schema.fullName);
    var tables = schema.tables;
    for ( var j = 0; j < tables.length; j++)
    {
      var table = tables[j];
      println("o--> " + table.name);
      var columns = table.columns;
      for ( var k = 0; k < columns.length; k++)
      {
        var column = columns[k];
        println("     o--> " + column.name);
      }
    }
  }
};

printDatabase();
