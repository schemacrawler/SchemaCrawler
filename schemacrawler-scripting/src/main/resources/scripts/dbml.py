# Copyright (c) Sualeh Fatehi
# SPDX-License-Identifier: EPL-2.0

import re

print('Project "' + title + '" {')
print('  database_type: "' + re.sub(r'\"', '', support.databaseVersion()) + '"')
print("  Note: '''")
print(catalog.getCrawlInfo())
print("  '''")
print("}")

# Columns
for table in catalog.getTables():
    print('Table "' + re.sub(r'\"', '', table.getFullName()) + '" {')
    for column in table.getColumns():
        print('  "' + column.getName() + '" "' + support.columnTypeName(column) + '"',
              end='')
        # Column attributes
        print(' [', end='')
        if not column.isNullable():
            print('not ', end='')
        print('null', end='')
        if column.hasDefaultValue():
            print(', default: "' + column.getDefaultValue() + '"', end='')
        if column.hasRemarks():
            print(', note: "' + column.getRemarks() + '"', end='')
        print(']', end='')
        print()
    if table.hasRemarks():
        print("  Note: '''")
        print(table.getRemarks())
        print("  '''")
    # Primary keys and indexes
    if table.hasPrimaryKey() or not table.getIndexes().isEmpty():
        print('  indexes {')
        if table.hasPrimaryKey():
            primaryKey = table.getPrimaryKey()
            print('    ('
                  + support.quotedColumnsList(primaryKey) + ') '
                  + '[pk]')
        indexes = support.nonPrimaryIndexes(table)
        if not indexes.isEmpty():
            for index in indexes:
                print('    ('
                      + support.quotedColumnsList(index) + ')',
                      end='')
                print(' [name: "' + index.getName() + '"', end='')
                if index.isUnique():
                    print(', unique', end='')
                print(']')
        print('  }')
    print('}')
    print('')

# Foreign keys
for table in catalog.getTables():
    for fk in table.getExportedForeignKeys():
        print('Ref "' + fk.getName() + '" {')
        pkTable = support.primaryKeyTable(fk)
        fkTable = support.foreignKeyTable(fk)
        print('  "' \
              + re.sub(r'\"', '', pkTable.getFullName()) + '".('
              + support.primaryKeyColumns(fk)
              + ') < "'
              + re.sub(r'\"', '', fkTable.getFullName()) + '".('
              + support.foreignKeyColumns(fk)
              + ')', end='')
        print(
            ' [update: ' + fk.getUpdateRule().toString() + ', delete: ' + fk.getDeleteRule().toString() + ']',
            end='')
        print()
        print("}")
        print('')
print('')

# Table groups
for schema in catalog.getSchemas():
    print('TableGroup "' + re.sub(r'\"', '', schema.getFullName()) + ' " {')
    for table in catalog.getTables(schema):
        print('  "' + re.sub(r'\"', '', table.getFullName()) + '"')
    print('}')
    print('')
