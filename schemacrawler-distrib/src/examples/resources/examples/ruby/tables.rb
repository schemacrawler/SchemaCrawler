require 'java'

def schemacrawler
  Java::Schemacrawler
end

puts catalog.crawlInfo

for table in catalog.tables
  puts ''
  puts table.fullName
  for childTable in table.referencingTables
    puts "  [child] " + childTable.fullName
  end
end
