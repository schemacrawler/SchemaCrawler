from __future__ import print_function
import re
from schemacrawler.schema import TableRelationshipType  # pylint: disable=import-error
from schemacrawler.schemacrawler import IdentifierQuotingStrategy  # pylint: disable=import-error
from schemacrawler.utility import MetaDataUtility  # pylint: disable=import-error

print("@startuml")
print("""
hide empty methods
!define schema(x) package x <<Rectangle>>
!define table(x) entity x << (T, white) table >>
!define view(x) entity x << (V, yellow) view >>
!define primary_key(x) <b><color:#b8861b><&key></color> x</b>
!define foreign_key(x) <color:#aaaaaa><&key></color> x
!define column(x) {field} <color:#efefef><&media-record></color> x
""")

# Tables
for schema in catalog.getSchemas():
    if not catalog.getTables(schema):
        continue
    print('schema(' + re.sub(r'\"', '', schema.name) + ') {')
    print('')
    for table in catalog.getTables(schema):
        if not table.tableType.isView():
            print('table', end='')
        else:
            print('view', end='')        
        print('(' + re.sub(r'\"', '', table.name) + ') {')
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
            print('note left of ' + re.sub(r'\"', '', table.name))
            print(table.remarks)
            print('end note')
            print('')
        for column in table.columns:
            if column.remarks:
                print('note right of ' + re.sub(r'\"', '', table.name) + '::' + column.name \
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
        pkTable = None
        fkTable = None
        for columnReference in fk.columnReferences:
            pkColumn = columnReference.primaryKeyColumn
            fkColumn = columnReference.foreignKeyColumn
            print('' \
                  + re.sub(r'\"', '', pkColumn.parent.fullName) + '::' \
                  + re.sub(r'\"', '', pkColumn.name) \
                  + '  }|--o| ' \
                  + re.sub(r'\"', '', fkColumn.parent.fullName) + '::' \
                  + re.sub(r'\"', '', fkColumn.name) \
                  , end='')
        print(' : < ' \
              + fk.name, end='')
        print(' ' \
              + '\\n[update: ' + fk.updateRule.toString() + ', \\ndelete: ' + fk.deleteRule.toString() + ']' \
              , end='')
        print('')
print('')

print("@enduml")
