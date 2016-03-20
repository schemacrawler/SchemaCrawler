// Define all standard Java packages:
var JavaPackages = new JavaImporter(
    java.util,
    java.io,
    java.nio);

// Define all classes:
var TableRelationshipType = Java.type('schemacrawler.schema.TableRelationshipType');
    
with (JavaPackages) {
 
var printChildren = function()
{ 
  for each (var schema in catalog.schemas)
  {
    print(schema.fullName);
    for each (var table in catalog.getTables(schema))
    {
      print("  o--> " + table.fullName);
      var children = table.getRelatedTables(TableRelationshipType.child);
      for each (var childTable in children)
      {
        print("    [child] " + childTable.fullName);
      }      
    }
  }
};

printChildren();

}
