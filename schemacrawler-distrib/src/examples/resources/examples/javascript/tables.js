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

var printChildren = function()
{
  print(catalog.crawlInfo);

  for each (var table in catalog.getTables())
  {
    print('');
    print(table.fullName);
    var children = table.getRelatedTables(TableRelationshipType.child);
    for each (var childTable in children)
    {
      print("  [child] " + childTable.fullName);
    }
  }
};

printChildren();

}
