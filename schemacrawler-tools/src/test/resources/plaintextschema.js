var printCatalog = function()
{
  print(catalog.crawlHeaderInfo);
  
  var schemas = catalog.schemas.toArray();
  for ( var i = 0; i < schemas.length; i++)
  {
    print(schemas[i].fullName);
    var tables = catalog.getTables(schemas[i]).toArray();
    for ( var j = 0; j < tables.length; j++)
    {
      print("o--> " + tables[j].name);
      var columns = tables[j].columns.toArray();
      for ( var k = 0; k < columns.length; k++)
      {
        print("     o--> " + columns[k].name);
      }
    }
  }
};

printCatalog();
