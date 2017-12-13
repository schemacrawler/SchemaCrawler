print "WARNING: DROPPING ALL TABLES. RESTART THE DATABASE SERVER TO GET THEM BACK"

statement = connection.createStatement()
for table in catalog.tables:
  try:
    sql = "DROP " + table.type.toString() + " " + table.fullName
    print "Executing SQL: " + sql
    statement.executeUpdate(sql)
  except:
    print 'Could not drop ' + table.fullName
    continue