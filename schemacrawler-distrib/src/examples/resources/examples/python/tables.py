from schemacrawler.schema import TableRelationshipType

print catalog.schemaCrawlerInfo
print catalog.databaseInfo
print catalog.jdbcDriverInfo

for table in catalog.tables:
  print ''
  print table.fullName
  for childTable in table.getRelatedTables(TableRelationshipType.child):
    print "  [child] " + childTable.fullName
