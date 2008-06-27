print(schema.getDatabaseInfo());
print("\n");
print("\n");

var tables = schema.getTables();
for (i = 0; i < tables.length; i++)
{
  print(tables[i].getName());
  print("\n");

  var columns = tables[i].getColumns();
  for (j = 0; j < columns.length; j++)
  {
    print("-- ");
    print(columns[j].getName());
    print("\n");
  }
  print("\n");
}
