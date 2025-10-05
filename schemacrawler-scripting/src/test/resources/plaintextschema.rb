# encoding: utf-8
puts catalog.getCrawlInfo().toString()

catalog.getTables().each do |table|
  # $stderr.puts table.getSchema().toString()
  if table.getSchema().toString() == "PUBLIC.FOR_LINT"
    next
  end
  # $stderr.puts table.getFullName()
  puts ''
  puts table.getFullName()
  table.getColumns().each do |column|
    puts "  " + column.getName()
  end
end
