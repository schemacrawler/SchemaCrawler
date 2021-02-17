from __future__ import print_function
from schemacrawler.schema import TableRelationshipType # pylint: disable=import-error
import re

print('Project "' + catalog.crawlInfo.runId + '" {')
print('  database_type: "' + re.sub(r'\"', '', catalog.crawlInfo.databaseVersion.toString()) + '"')
print("  Note: '''")
print(catalog.crawlInfo)
print("  '''")
print("}")
    
for table in catalog.getTables():
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

# Table groups
for schema in catalog.schemas:
  print('TableGroup "' + re.sub(r'\"', '', schema.fullName) + '" {')
  for table in catalog.getTables(schema):
    print('  "' + re.sub(r'\"', '', table.fullName) + '\"')
  print('}')
  print('')
