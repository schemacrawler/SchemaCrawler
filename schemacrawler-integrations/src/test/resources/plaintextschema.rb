puts $database.schemaCrawlerInfo
puts ''
puts $database.databaseInfo
puts ''
puts $database.jdbcDriverInfo

for table in $database.tables
  puts ''
  puts table.fullName
  for column in table.columns
    puts "  " + column.name
  end
end