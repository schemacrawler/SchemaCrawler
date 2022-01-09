var printChildren = function()
{
  var forEach = Array.prototype.forEach;

  print(catalog.getCrawlInfo());

  forEach.call(catalog.getTables(), function(table)
  {
    print('');
    print(table.getFullName());
    var children = table.getReferencingTables();
    forEach.call(children, function(childTable)
    {
      print("  [child] " + childTable.getFullName());
    });
  });
};

printChildren();
