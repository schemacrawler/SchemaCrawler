require 'java'

def schemacrawler
  Java::Schemacrawler
end

java_import schemacrawler.schema.TableRelationshipType

puts catalog.crawlInfo

for table in catalog.tables
  puts ''
  puts table.fullName
  for childTable in table.getRelatedTables(TableRelationshipType.child)
    puts "  [child] " + childTable.fullName
  end
end
