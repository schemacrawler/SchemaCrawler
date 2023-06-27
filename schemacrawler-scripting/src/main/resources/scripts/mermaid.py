from __future__ import print_function
import re


# Mermaid only allows alphanumeric identifiers
def cleanname(name):
    namepattern = r'[^-\d\w]'
    cleanedname = re.sub(namepattern, '', name)
    if not cleanedname:
        cleanedname = "UNKNOWN"
    return cleanedname


print('erDiagram')
print('')
for table in catalog.tables:
    print('  ' + cleanname(table.fullName) + ' {')
    for column in table.columns:
        print('    ' + cleanname(column.columnDataType.name) + ' ' + cleanname(column.name),
              end='')
        if column.isPartOfPrimaryKey():
            print(' PK', end='')
        elif column.isPartOfForeignKey():
            print(' FK', end='')
        elif column.isPartOfUniqueIndex():
            print(' UK', end='')
        if column.hasRemarks():
            print(' "' + ' '.join(column.remarks.splitlines()) + '"',
                  end='')
        print()
    print('  }')
    print('')

for table in catalog.tables:
    for childTable in table.referencingTables:
        print('  ' + cleanname(table.fullName) + ' ||--o{ ' +
              cleanname(childTable.fullName) + ' : "foreign key"')
