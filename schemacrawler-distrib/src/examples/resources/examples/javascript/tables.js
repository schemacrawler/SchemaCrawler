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
    var forEach = Array.prototype.forEach;

    print(catalog.crawlInfo);

    forEach.call(catalog.getTables(), function(table)
    {
      print('');
      print(table.fullName);
      var children = table.getRelatedTables(TableRelationshipType.child);
      forEach.call(children, function(childTable)
      {
        print("  [child] " + childTable.fullName);
      });
    });
  };

  printChildren();

}
