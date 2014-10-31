puts "WARNING: DROPPING ALL TABLES. RESTART THE DATABASE SERVER TO GET THEM BACK"

statement = $connection.createStatement()
for table in $catalog.tables
	begin
		sql = "DROP " + table.type.toString().upcase + " " + table.fullName
		puts "Executing SQL: " + sql
		statement.executeUpdate(sql)
	rescue Exception => e
		puts e.message
	end
end
