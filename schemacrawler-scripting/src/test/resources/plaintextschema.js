var forEach = Array.prototype.forEach;

print(catalog.getCrawlInfo())

forEach.call(catalog.getTables(), function(table)
{
  print('');
  print(table.getFullName());
  forEach.call(table.getColumns(), function(column)
  {
    print("  " + column.getName());
  });
});
