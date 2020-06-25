var forEach = Array.prototype.forEach;

print(catalog.crawlInfo)

forEach.call(catalog.getTables(), function(table)
{
  print('');
  print(table.fullName);
  forEach.call(table.columns, function(column)
  {
    print("  " + column.name);
  });
});
