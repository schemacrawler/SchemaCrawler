from __future__ import print_function
import re

print("@startuml")
print("""
hide empty methods
!define schema(name, slug) package "name" as slug <<Rectangle>>
!define table(name, slug) entity "name" as slug << (T, white) table >>
!define view(name, slug) entity "name" as slug << (V, yellow) view >>
!define primary_key(name) <b><color:#b8861b><&key></color> name</b>
!define foreign_key(name) <color:#aaaaaa><&key></color> name
!define column(name) {field} <color:#efefef><&media-record></color> name
""")

# Tables
for schema in catalog.getSchemas():
    if not catalog.getTables(schema):
        continue
    print('schema(' + re.sub(r'\"', '', schema.fullName)  + ', ' + schema.key().slug() + ') {')
    print('')
    for table in catalog.getTables(schema):
        if not table.tableType.isView():
            print('table', end='')
        else:
            print('view', end='')
        print('(' + re.sub(r'\"', '', table.name) + ', ' + table.key().slug() + ') {')
        for column in table.columns:
            if column.isPartOfPrimaryKey():
                print('  primary_key', end='')
            elif column.isPartOfForeignKey():
                print('  foreign_key', end='')
            else:
                print('  column', end='')
            print('(' + column.name + '): ' + column.columnDataType.name, end='')
            print(' ', end='')
            if not column.nullable:
                print('NOT ', end='')
            print('NULL', end='')
            print('')
        print('}')
        print('')
        if table.remarks:
            print('note left of ' + table.key().slug())
            print(table.remarks)
            print('end note')
            print('')
        for column in table.columns:
            if column.remarks:
                print('note right of ' + table.key().slug() + '::' + column.name \
                    + ' #Aquamarine')
                print(column.remarks)
                print('end note')
                print('')
        print('')
        print('')
    print('')
    print('}')
    print('')
    print('')

# Foreign keys
for table in catalog.tables:
    for fk in table.exportedForeignKeys:
        pkTable = fk.primaryKeyTable
        fkTable = fk.foreignKeyTable
        for columnReference in fk.columnReferences:
            pkColumn = columnReference.primaryKeyColumn
            fkColumn = columnReference.foreignKeyColumn
            print('' \
                  + pkTable.schema.key().slug() + '.'
                  + pkTable.key().slug() + '::'
                  + re.sub(r'\"', '', pkColumn.name)
                  + '  }|--o| ' \
                  + fkTable.schema.key().slug() + '.'
                  + fkTable.key().slug() + '::'
                  + re.sub(r'\"', '', fkColumn.name)
                  , end='')
            print(' : < '
                  + fk.name, end='')
            print('')
print('')

print("@enduml")
