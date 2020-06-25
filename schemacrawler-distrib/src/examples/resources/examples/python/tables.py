from schemacrawler.schema import TableRelationshipType # pylint: disable=import-error

print(catalog.crawlInfo)

for table in catalog.tables:
  print('')
  print(table.fullName)
  for childTable in table.getRelatedTables(TableRelationshipType.child):
    print("  [child] " + childTable.fullName)
