from schemacrawler.schema import TableRelationshipType

print catalog.crawleInfo

for table in catalog.tables:
  print ''
  print table.fullName
  for childTable in table.getRelatedTables(TableRelationshipType.child):
    print "  [child] " + childTable.fullName
