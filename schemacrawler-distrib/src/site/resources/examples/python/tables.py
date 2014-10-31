print catalog.schemaCrawlerInfo
print catalog.databaseInfo
print catalog.jdbcDriverInfo

for table in catalog.tables:
  print ''
  print table.fullName
  for column in table.columns:
    print "  " + column.name
