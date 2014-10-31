if (typeof println != 'function') {
   // Account for Java 8 Nashorn engine
   println = function(args) { print(args); };
}

var printDb = function()
{
  println(catalog.schemaCrawlerInfo);
  println(catalog.databaseInfo);
  println(catalog.jdbcDriverInfo);

  var schemas = catalog.schemas.toArray();
  for ( var i = 0; i < schemas.length; i++)
  {
    println(schemas[i].fullName);
    var tables = catalog.getTables(schemas[i]).toArray();
    for ( var j = 0; j < tables.length; j++)
    {
      println("o--> " + tables[j].name);
      var columns = tables[j].columns.toArray();
      for ( var k = 0; k < columns.length; k++)
      {
        println("     o--> " + columns[k].name);
      }
    }
  }
};

printDb();
