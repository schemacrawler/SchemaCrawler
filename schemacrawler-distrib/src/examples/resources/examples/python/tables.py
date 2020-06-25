from schemacrawler.schema import TableRelationshipType

print(catalog.crawlInfo)

for table in catalog.tables:
  print('')
  print(table.fullName)
  for childTable in table.getRelatedTables(TableRelationshipType.child):
    print("  [child] " + childTable.fullName)
