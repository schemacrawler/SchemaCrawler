from __future__ import print_function
import re

print('erDiagram')
print('')
for table in catalog.tables:
    print('  ' + re.sub(r'\.', '-', table.fullName) + ' {')
    for column in table.columns:
        print('    ' + re.sub(r'\([\d ,]+\)|\[[\d ,]+\]|\s+', '',
                              column.columnDataType.name) + ' ' + column.name, end='')
        if column.hasRemarks():
            print(' "' + ' '.join(column.remarks.splitlines()) + '"', end='')
        print()
    print('  }')
    print('')

for table in catalog.tables:
    for childTable in table.referencingTables:
        print('  ' + re.sub(r'\.', '-', table.fullName) + ' ||--o{ ' +
              re.sub(r'\.', '-', childTable.fullName) + ' : "foreign key"')
