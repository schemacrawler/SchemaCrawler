println catalog.crawlInfo

for (table in catalog.tables)
{
  println ''
  println table.fullName
  for (childTable in table.referencingTables)
  {
    println "  [child] " + childTable.fullName
  }
}
