# Copyright (c) Sualeh Fatehi
# SPDX-License-Identifier: EPL-2.0

print(f'# {title}')

print()
for schema in catalog.getSchemas():

  tables = catalog.getTables(schema)
  if not tables:
    continue

  print(f'## {schema.getFullName()}')

  print()
  for table in tables:
    print(f'### {table.getName()} ({support.type(table)})')
    if table.hasRemarks():
      print(table.getRemarks())

    print()
    print('### Columns')
    for column in table.getColumns():
      if column.isPartOfPrimaryKey():
        col_name = f'**{column.getName()}**'
      elif column.isPartOfForeignKey():
        col_name = f'*{column.getName()}*'
      else:
        col_name = column.getName()
      print(f'- {col_name} ({support.columnType(column)})', end='')
      column_remarks = support.indent(column.getRemarks(), 4)
      if column_remarks:
        print('    ')
        print(f'{column_remarks}', end='')
      else:
        print()

    if table.hasPrimaryKey():
      print()
      print('### Primary Key')
      pk = table.getPrimaryKey()
      pk_name = f"{pk.getName()} " if support.hasName(pk) else ''
      print(f'- {pk_name}({support.columns(pk)})')

    indexes = support.nonPrimaryIndexes(table)
    if not indexes.isEmpty():
      print()
      print('### Indexes')
      for index in indexes:
        unique = ' (unique index)' if index.isUnique() else ''
        print(f'- {index.getName()} ({support.columns(index)}){unique}')

    foreign_keys = table.getImportedForeignKeys()
    if not foreign_keys.isEmpty():
      print()
      print('### Foreign Keys')
      for fk in foreign_keys:
        fk_name = f"{fk.getName()} " if support.hasName(fk) else ''
        pk_cols = support.pkColumns(fk)
        fk_cols = support.fkColumns(fk)
        print(f'- {fk_name}([{fk_cols}] --> [{pk_cols}])')

    print()
    print()
    print()
