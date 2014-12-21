# encoding: utf-8
puts $catalog.schemaCrawlerInfo
puts ''
puts $catalog.databaseInfo
puts ''
puts $catalog.jdbcDriverInfo

for table in $catalog.tables
  puts ''
  puts table.fullName
  for column in table.columns
    puts "  " + column.name
  end
end