// Copyright (c) Sualeh Fatehi
// SPDX-License-Identifier: EPL-2.0

println "WARNING: DROPPING ALL TABLES. RESTART THE DATABASE SERVER TO GET THEM BACK"

statement = connection.createStatement()
tables = catalog.tables
for (table in catalog.tables)
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
