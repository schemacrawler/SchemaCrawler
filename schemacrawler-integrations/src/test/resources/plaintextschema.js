print(catalog.crawlInfo)

for each (var table in catalog.getTables())
{
  print('');
  print(table.fullName);
  for each (var column in table.columns)
  {
    print("  " + column.name);
  }      
}
