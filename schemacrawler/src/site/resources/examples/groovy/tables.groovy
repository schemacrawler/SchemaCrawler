println database.schemaCrawlerInfo
println database.databaseInfo
println database.jdbcDriverInfo

for (table in database.tables)
{
  println ''
  println table.fullName
  for (column in table.columns)
  {
    println "  " + column.name
  }
}
