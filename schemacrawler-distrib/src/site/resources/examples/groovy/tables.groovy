import schemacrawler.schema.TableRelationshipType

println catalog.schemaCrawlerInfo
println catalog.databaseInfo
println catalog.jdbcDriverInfo

for (table in catalog.tables)
{
  println ''
  println table.fullName
  for (childTable in table.getRelatedTables(TableRelationshipType.child))
  {
    println "  [child] " + childTable.fullName
  }
}
