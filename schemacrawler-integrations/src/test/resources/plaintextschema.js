var printDb = function() {
  print(database.getDatabaseInfo() + "\n");
  var catalogs = database.getCatalogs();
  for ( var c = 0; c < catalogs.length; c++) {
    var schemas = catalogs[c].getSchemas();
    for ( var i = 0; i < schemas.length; i++) {
      print(schemas[i].getFullName() + "\n");
      var tables = schemas[i].getTables();
      for ( var j = 0; j < tables.length; j++) {
        print("o--> " + tables[j].getName() + "\n");
        var columns = tables[j].getColumns();
        for ( var k = 0; k < columns.length; k++) {
          print("     o--> " + columns[k].getName() + "\n");
        }
      }
    }
  }
};

printDb();
