# Copyright (c) Sualeh Fatehi
# SPDX-License-Identifier: EPL-2.0

print(f'Project "{title}" {{')
print(f'  database_type: "{support.databaseVersion()}"')
print("  Note: '''")
print(catalog.getCrawlInfo())
print("  '''")
print("}")

# Columns
for table in catalog.getTables():
    print(f'Table "{support.cleanFullName(table)}" {{')
    for column in table.getColumns():
        print(f'  "{column.getName()}" "{support.columnType(column)}"', end='')
        # Column attributes
        print(' [', end='')
        if not column.isNullable():
            print('not ', end='')
        print('null', end='')
        if column.hasDefaultValue():
            print(f', default: "{column.getDefaultValue()}"', end='')
        if column.hasRemarks():
            print(f', note: "{column.getRemarks()}"', end='')
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
            print(f'    ({support.columns(primaryKey)}) [pk]')
        indexes = support.nonPrimaryIndexes(table)
        if not indexes.isEmpty():
            for index in indexes:
                print(f'    ({support.columns(index)})', end='')
                print(f' [name: "{index.getName()}"', end='')
                if index.isUnique():
                    print(', unique', end='')
                print(']')
        print('  }')
    print('}')
    print('')

# Foreign keys
for table in catalog.getTables():
    for fk in table.getExportedForeignKeys():
        print(f'Ref "{fk.getName()}" {{')
        pkTable = support.primaryKeyTable(fk)
        fkTable = support.foreignKeyTable(fk)
        pk_name = support.cleanFullName(pkTable)
        fk_name = support.cleanFullName(fkTable)
        pk_cols = support.primaryKeyColumns(fk)
        fk_cols = support.foreignKeyColumns(fk)
        print(f'  "{pk_name}".({pk_cols}) < "{fk_name}".({fk_cols})', end='')
        print(f' [update: {fk.getUpdateRule().toString()}, delete: {fk.getDeleteRule().toString()}]', end='')
        print()
        print("}")
        print('')
print('')

# Table groups
for schema in catalog.getSchemas():
    print(f'TableGroup "{support.cleanFullName(schema)} " {{')
    for table in catalog.getTables(schema):
        print(f'  "{support.cleanFullName(table)}"')
    print('}')
    print('')
