# Copyright (c) Sualeh Fatehi
# SPDX-License-Identifier: EPL-2.0

import re
import java

TableRelationshipType = java.type("schemacrawler.schema.TableRelationshipType")
IdentifiersBuilder = java.type("schemacrawler.schemacrawler.IdentifiersBuilder")
IdentifierQuotingStrategy = java.type("schemacrawler.schemacrawler.IdentifierQuotingStrategy")
MetaDataUtility = java.type("schemacrawler.utility.MetaDataUtility")

if title:
    print('# ' + title)
else:
    print('# Database Schema')

identifiers = IdentifiersBuilder.builder().toOptions()

print('')
for schema in catalog.getSchemas():
    
    tables = catalog.getTables(schema)
    if not tables:
        continue
        
    print('## ' + schema.getFullName())
    
    print('')
    for table in tables:
        print('### ' + table.getName(), end="")
        if not table.getTableType().isView():
            print(' (table)', end='')
        else:
            print(' (view)', end='')
        print('')
        table_remarks = table.getRemarks()
        if table_remarks is not None:
            print(table_remarks)

        print('')
        print('### Columns')
        for column in table.getColumns():
            print('- ', end='')
            part_of_primary_key = column.isPartOfPrimaryKey()
            part_of_foreign_key = column.isPartOfForeignKey()
            if part_of_primary_key:
                print('**', end='')
            elif part_of_foreign_key:
                print('*', end='')
            print(column.getName(), end='')
            if part_of_primary_key:
                print('**', end='')
            elif part_of_foreign_key:
                print('*', end='')
            print(' (' + column.getColumnDataType().toString() + ')', end='')
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
                  MetaDataUtility.getColumnsListAsString(primaryKey, identifiers) + ') ')

        indexes = table.getIndexes()
        if not indexes.isEmpty():
            print('')
            print('### Indexes')
            for index in indexes:
                if table.hasPrimaryKey() and \
                        MetaDataUtility.getColumnsListAsString(table.getPrimaryKey(), identifiers) == \
                        MetaDataUtility.getColumnsListAsString(index, identifiers):
                    continue
                print('- ' + index.getName() + ' (' +
                      MetaDataUtility.getColumnsListAsString(index, identifiers) + ')',
                      end='')
                if index.isUnique():
                    print(' (unique index)', end='')
                print('')

        foreign_keys = table.getImportedForeignKeys()
        if not foreign_keys.isEmpty():
            print('')
            print('### Foreign Keys')
            for fk in foreign_keys:
                pkTable = fk.getPrimaryKeyTable()
                fkTable = fk.getForeignKeyTable()
                for columnReference in fk.getColumnReferences():
                    print('- ', end='')
                    if fk.getName() and not fk.getName().startswith('SCHCRWLR_'):
                        print(fk.getName(), end='')
                    pkColumn = columnReference.getPrimaryKeyColumn()
                    fkColumn = columnReference.getForeignKeyColumn()
                    print(' (*' + fkColumn.getName() + '* --> **' + pkColumn.getShortName() + "**)", end='')
                    print('')

        print('')
        print('')
        print('')
