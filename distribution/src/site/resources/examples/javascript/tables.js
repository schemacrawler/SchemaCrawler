var printCatalog = function() {
  print(catalog.getDatabaseInfo() + "\n");  
  var schemas = catalog.getSchemas();
  for (var i = 0; i < schemas.length; i++) {
    print(schemas[i].getName() + "\n");
    var tables = schemas[i].getTables();
    for (var j = 0; j < tables.length; j++) {
      print("o--> " + tables[j].getName() + "\n");
      var columns = tables[j].getColumns();
      for (var k = 0; k < columns.length; k++) {
        print("     o--> " + columns[k].getName() + "\n");
      }
    }
  }
};

printCatalog();
