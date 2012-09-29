print database.schemaCrawlerInfo
print database.databaseInfo
print database.jdbcDriverInfo

for table in database.tables:
  print ''
  print table.fullName
  for column in table.columns:
    print "  " + column.name
