from schemacrawler.schema import TableRelationshipType # pylint: disable=import-error
import re

for table in catalog.tables:
  print('Table "' + re.sub(r'\"', '', table.fullName) + '" {')
  for column in table.columns:
    print('  "' + column.name + '" "' + column.columnDataType.name + '" ' )
  if table.hasRemarks():
    print("  Note: '''")
    print(table.remarks)
    print("  '''")
  print('}')
  print('')
  
"""
for table in catalog.tables:  
  for childTable in table.getRelatedTables(TableRelationshipType.child):
    print('  ' + table.name + ' ||--o{ ' + childTable.name + ' : "foreign key"')
  print('')
"""
