from schemacrawler.schema import TableRelationshipType # pylint: disable=import-error
import re

print('erDiagram')
print('')
for table in catalog.tables:
  print('  ' + re.sub(r'\.', '-', table.fullName) + ' {')
  for column in table.columns:
    print('    ' + re.sub(r'\([\d ,]+\)|\[[\d ,]+\]|\s+', '', column.columnDataType.name) + ' ' + column.name)
  print('  }')
  print('')
  
for table in catalog.tables:  
  for childTable in table.getRelatedTables(TableRelationshipType.child):
    print('  ' + re.sub(r'\.', '-', table.fullName) + ' ||--o{ ' + re.sub(r'\.', '-', childTable.fullName) + ' : "foreign key"')
