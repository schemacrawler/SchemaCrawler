// Define all standard Java packages:
var JavaPackages = new JavaImporter(
  java.lang,
  java.util,
  java.io,
  java.nio
);

// Define all classes:
var TableRelationshipType = Java.type('schemacrawler.schema.TableRelationshipType');
    
with (JavaPackages) {
 
var dropTables = function()
{
  var statement = connection.createStatement();
  var tables = catalog.tables.toArray();
  for (var i = (tables.length - 1); i >= 0; i--)
  {
    var table = tables[i];
    var sql = "DROP " + table.type + " " + table.fullName;
    print("Executing SQL: " + sql);
    try
    {
      statement.executeUpdate(sql);
    } catch (e)
    {
      print("");
      print(e.message);
      print("(Not dropping any more tables, due to exception)");
      return;
    }
  }
};

dropTables();

}
