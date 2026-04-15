# Copyright (c) Sualeh Fatehi
# SPDX-License-Identifier: EPL-2.0

print('# ' + title)

print('')
for schema in catalog.getSchemas():

    tables = catalog.getTables(schema)
    if not tables:
        continue

    print('## ' + schema.getFullName())

    print('')
    for table in tables:
        print('### ' + table.getName(), end="")
        if not support.isView(table):
            print(' (table)', end='')
        else:
            print(' (view)', end='')
        print('')
        if table.hasRemarks():
            print(table.getRemarks())

        print('')
        print('### Columns')
        for column in table.getColumns():
            print('- ', end='')
            column_role = support.columnRole(column).name()
            if column_role == 'PRIMARY_KEY':
                print('**', end='')
            elif column_role == 'FOREIGN_KEY':
                print('*', end='')
            print(column.getName(), end='')
            if column_role == 'PRIMARY_KEY':
                print('**', end='')
            elif column_role == 'FOREIGN_KEY':
                print('*', end='')
            print(' (' + support.columnTypeDisplay(column) + ')', end='')
            column_remarks = column.getRemarks()
            if column_remarks:
                print('    ')
                print('\n    '.join(column_remarks.splitlines()), end='')
            print()

        if table.hasPrimaryKey():
            print('')
            print('### Primary Key')
            primaryKey = table.getPrimaryKey()
            print('- ' + primaryKey.getName() + ' (' +
                  support.columnsList(primaryKey) + ') ')

        indexes = support.nonPrimaryIndexes(table)
        if not indexes.isEmpty():
            print('')
            print('### Indexes')
            for index in indexes:
                print('- ' + index.getName() + ' (' +
                      support.columnsList(index) + ')',
                      end='')
                if index.isUnique():
                    print(' (unique index)', end='')
                print('')

        foreign_keys = table.getImportedForeignKeys()
        if not foreign_keys.isEmpty():
            print('')
            print('### Foreign Keys')
            for fk in foreign_keys:
                for columnReference in support.columnReferences(fk):
                    print('- ', end='')
                    if support.hasName(fk):
                        print(fk.getName(), end='')
                    pkColumn = columnReference.getPrimaryKeyColumn()
                    fkColumn = columnReference.getForeignKeyColumn()
                    print(' (*' + fkColumn.getName() + '* --> **' + pkColumn.getShortName() + "**)", end='')
                    print('')

        print('')
        print('')
        print('')
