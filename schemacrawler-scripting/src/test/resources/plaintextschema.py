print(catalog.getCrawlInfo())

for table in catalog.getTables():
  print('')
  print(table.getFullName())
  for column in table.getColumns():
    print("  " + column.getName())
