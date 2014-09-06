println catalog.schemaCrawlerInfo
println catalog.databaseInfo
println catalog.jdbcDriverInfo

for (table in catalog.tables)
{
  println ''
  println table.fullName
  for (column in table.columns)
  {
    println "  " + column.name
  }
}
