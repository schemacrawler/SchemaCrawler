# encoding: utf-8
puts $catalog.crawlHeaderInfo

for table in $catalog.tables
  # $stderr.puts table.schema
  if table.schema.to_s == "PUBLIC.FOR_LINT"
    next
  end
  # $stderr.puts table.fullName
  puts ''
  puts table.fullName
  for column in table.columns
    puts "  " + column.name
  end
end