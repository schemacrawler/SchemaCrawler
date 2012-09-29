println "WARNING: DROPPING ALL TABLES. RESTART THE DATABASE SERVER TO GET THEM BACK"

statement = connection.createStatement()
tables = database.tables
for (table in database.tables)
{
	try {
		sql = "DROP " + table.type + " " + table.fullName
		println "Executing SQL: " + sql
		statement.executeUpdate(sql)
	}
	catch (Exception e) {	
		println e.message
	}
}
