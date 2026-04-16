# Copyright (c) Sualeh Fatehi
# SPDX-License-Identifier: EPL-2.0

print(f'Project "{title}" {{')
print(f'  database_type: "{crawl_info.databaseVersion()}"')
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
        pk_table_name = support.cleanFullName(fk.getPrimaryKeyTable())
        fk_table_name = support.cleanFullName(fk.getForeignKeyTable())
        pk_cols = support.pkColumns(fk)
        fk_cols = support.fkColumns(fk)
        print(f'  "{pk_table_name}".({pk_cols}) < "{fk_table_name}".({fk_cols})', end='')
        print(f' [update: {fk.getUpdateRule()}, delete: {fk.getDeleteRule()}]', end='')
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
