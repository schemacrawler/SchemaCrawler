println catalog.crawlHeaderInfo

for (table in catalog.tables)
{
  println ''
  println table.fullName
  for (column in table.columns)
  {
    println "  " + column.name
  }
}
