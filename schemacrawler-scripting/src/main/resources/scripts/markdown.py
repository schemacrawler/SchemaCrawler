# Copyright (c) Sualeh Fatehi
# SPDX-License-Identifier: EPL-2.0

print(f'# {title}')

print('')
for schema in catalog.getSchemas():

    tables = catalog.getTables(schema)
    if not tables:
        continue

    print(f'## {schema.getFullName()}')

    print('')
    for table in tables:
        table_type = 'view' if support.isView(table) else 'table'
        print(f'### {table.getName()} ({table_type})')
        if table.hasRemarks():
            print(table.getRemarks())

        print('')
        print('### Columns')
        for column in table.getColumns():
            if column.isPartOfPrimaryKey():
                col_name = f'**{column.getName()}**'
            elif column.isPartOfForeignKey():
                col_name = f'*{column.getName()}*'
            else:
                col_name = column.getName()
            print(f'- {col_name} ({support.columnType(column)})', end='')
            column_remarks = column.getRemarks()
            if column_remarks:
                print('    ')
                print('\n    '.join(column_remarks.splitlines()), end='')
            print()

        if table.hasPrimaryKey():
            print('')
            print('### Primary Key')
            primaryKey = table.getPrimaryKey()
            print(f'- {primaryKey.getName()} ({support.columns(primaryKey)}) ')

        indexes = support.nonPrimaryIndexes(table)
        if not indexes.isEmpty():
            print('')
            print('### Indexes')
            for index in indexes:
                unique = ' (unique index)' if index.isUnique() else ''
                print(f'- {index.getName()} ({support.columns(index)}){unique}')

        foreign_keys = table.getImportedForeignKeys()
        if not foreign_keys.isEmpty():
            print('')
            print('### Foreign Keys')
            for fk in foreign_keys:
                for columnReference in support.columnReferences(fk):
                    fk_name = fk.getName() if support.hasName(fk) else ''
                    pkColumn = columnReference.getPrimaryKeyColumn()
                    fkColumn = columnReference.getForeignKeyColumn()
                    print(f'- {fk_name} (*{fkColumn.getName()}* --> **{pkColumn.getShortName()}**)')

        print('')
        print('')
        print('')
