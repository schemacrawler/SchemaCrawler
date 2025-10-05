# Copyright (c) Sualeh Fatehi
# SPDX-License-Identifier: EPL-2.0

import re
import java

# Import Java classes explicitly
# Assuming catalog is passed in or previously defined Java object

# Mermaid only allows alphanumeric identifiers
def cleanname(name):
    namepattern = r'[^-\d\w]'
    cleanedname = re.sub(namepattern, '', name)
    if not cleanedname:
        cleanedname = "UNKNOWN"
    return cleanedname


print('erDiagram')
print('')

for table in catalog.getTables():
    print('  ' + cleanname(table.getFullName()) + ' {')
    for column in table.getColumns():
        coltype = column.getColumnDataType().getName()
        colname = column.getName()
        print('    ' + cleanname(coltype) + ' ' + cleanname(colname), end='')
        if column.isPartOfPrimaryKey():
            print(' PK', end='')
        elif column.isPartOfForeignKey():
            print(' FK', end='')
        elif column.isPartOfUniqueIndex():
            print(' UK', end='')
        if column.hasRemarks():
            remarks = ' '.join(column.getRemarks().splitlines())
            print(' "' + remarks + '"', end='')
        print()
    print('  }')
    print('')

for table in catalog.getTables():
    for childTable in table.getDependentTables():
        print('  ' + cleanname(table.getFullName()) + ' ||--o{ ' +
              cleanname(childTable.getFullName()) + ' : "foreign key"')
