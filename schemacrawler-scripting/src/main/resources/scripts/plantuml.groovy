import schemacrawler.schema.Catalog
import schemacrawler.schema.Column
import schemacrawler.schema.Schema
import schemacrawler.schema.Table

println("@startuml")
println('''
!theme plain
hide empty methods

!procedure $schema($name, $slug)
package "$name" as $slug <<Rectangle>>
!endprocedure

!procedure $table($name, $slug)
entity "<b>$name</b>" as $slug << (T, Orange) table >>
!endprocedure

!procedure $view($name, $slug)
entity "<b>$name</b>" as $slug << (V, Aquamarine) view >>
!endprocedure

!procedure $pkfk($name)
<color:#Brown><&key></color> <b>$name</b>
!endprocedure

!procedure $pk($name)
<color:#GoldenRod><&key></color> <b>$name</b>
!endprocedure

!procedure $fk($name)
<color:#Silver><&key></color> $name
!endprocedure

!procedure $column($name)
{field} <color:#White><&media-record></color> $name
!endprocedure

!procedure $pk_index($name, $columns)
  {method}<<PK>> $name ($columns)
!endprocedure

!procedure $fk_constraint($name, $col, $target, $columns)
  {method}<<FK>> $name ($col) <&arrow-right> $target ($columns)
!endprocedure

''')

renderTitle(title, catalog)

// Tables
renderSchemas()

// Foreign keys
renderLinks(catalog)

println()
println("@enduml")


private void renderTitle(String title, Catalog catalog) {
  println('title "' + title + '"')
  println()
  println('legend bottom right')
  println('generated by ' + catalog.crawlInfo.schemaCrawlerVersion.toString())
  println('generated on ' + catalog.crawlInfo.crawlTimestamp)
  println('end legend')
  println()
  println()
}

private renderSchemas(Catalog catalog) {
  catalog.schemas.forEach { schema ->
    if (!catalog.getTables(schema).empty) {
      renderSchema(schema, catalog)
      println()
    }
  }
}

private void renderSchema(Schema schema, Catalog catalog) {
  println("\$schema(\"${schema.fullName.replaceAll('"', '')}\", \"${schema.key().slug()}\") {")
  println()
  renderTables(catalog, schema)
  println()
  println('}')
}

private Object renderTables(Catalog catalog, Schema schema) {
  catalog.getTables(schema).forEach { table ->
    renderTable(table)
    println()
    renderTableNote(table)
    renderColumnNotes(table)
    println()
    println()
  }
}

private void renderTableNote(Table table) {
  if (table.remarks) {
    println('note left of ' + table.key().slug() + ' #LemonChiffon')
    println(table.remarks)
    println('end note')
    println()
  }
}

private List<Column> renderColumnNotes(Table table) {
  table.columns.each { column ->
    if (column.remarks) {
      println('note right of ' + table.key().slug() + '::'
        + column.name + ' #LightCyan')
      println(column.remarks)
      println('end note')
      println()
    }
  }
}

private void renderTable(Table table) {
  def viewType = table.tableType.view ? '$view' : '$table'
  println(viewType + '("' + table.name.replaceAll('"', '') + '", "' + table.key().slug() + '") {')
  table.columns.each { column ->
    def columnType = column.partOfPrimaryKey && column.partOfForeignKey ? '$pkfk' : column.partOfPrimaryKey ? '$pk'
      : column.partOfForeignKey ? '$fk'
      : '$column'
    println('  ' + columnType + '("' + column.name + '"): ' + column.columnDataType.name
      + (column.nullable ? '' : ' NOT NULL'))
  }
  if (table.primaryKey) {
    println "  \$pk_index(\"${table.primaryKey.name}\",\"${table.primaryKey.constrainedColumns.collect { it.name }.join(',')}\")"

  }
  table.foreignKeys.each { fk ->
    println "\$fk_constraint(\"${fk.name}\", \"${fk.constrainedColumns.collect { it.name }.join(',')}\", \"${fk.referencedTable.name}\", \"${fk.columnReferences.collect { it.primaryKeyColumn.name }.join(',')}\" )"
  }
  println('}')
}

private Object renderLinks(Catalog catalog) {
  catalog.tables.each { table ->
    table.exportedForeignKeys.each { fk ->
      def pkTable = fk.primaryKeyTable
      def fkTable = fk.foreignKeyTable
      fk.columnReferences.each { columnReference ->
        def pkColumn = columnReference.primaryKeyColumn
        def fkColumn = columnReference.foreignKeyColumn
        print('' + pkTable.schema.key().slug() + '.'
          + pkTable.key().slug() + '::'
          + pkColumn.name.replaceAll('"', '')
          + '  ||--o{ '
          + fkTable.schema.key().slug() + '.'
          + fkTable.key().slug() + '::'
          + fkColumn.name.replaceAll('"', ''))
        if (fk.name && !fk.name.startsWith('SCHCRWLR_')) {
          println(' : ' + fk.name)
        }
      }
    }
  }
}
