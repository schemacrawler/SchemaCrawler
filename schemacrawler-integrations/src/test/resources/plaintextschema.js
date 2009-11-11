var printDb = function()
{
  print(database.databaseInfo + "\n");
  var catalogs = database.catalogs;
  for ( var c = 0; c < catalogs.length; c++)
  {
    var schemas = catalogs[c].schemas;
    for ( var i = 0; i < schemas.length; i++)
    {
      print(schemas[i].getFullName() + "\n");
      var tables = schemas[i].tables;
      for ( var j = 0; j < tables.length; j++)
      {
        print("o--> " + tables[j].name + "\n");
        var columns = tables[j].columns;
        for ( var k = 0; k < columns.length; k++)
        {
          print("     o--> " + columns[k].name + "\n");
        }
      }
    }
  }
};

printDb();
