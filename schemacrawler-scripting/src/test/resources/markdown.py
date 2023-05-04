from __future__ import print_function
import re
from schemacrawler.schema import \
    TableRelationshipType  # pylint: disable=import-error
from schemacrawler.schemacrawler import \
    IdentifiersBuilder  # pylint: disable=import-error
from schemacrawler.schemacrawler import \
    IdentifierQuotingStrategy  # pylint: disable=import-error
from schemacrawler.utility import \
    MetaDataUtility  # pylint: disable=import-error


if title:
    print('# ' + title)
else:
    print('# Database Schema')

identifiers = \
    IdentifiersBuilder.builder() \
        .toOptions()

print('')
for schema in catalog.getSchemas():
    
    tables = catalog.getTables(schema)
    if not tables:
        continue
        
    print('## ' + schema.fullName)
    
    print('')
    for table in tables:
        print('### ' + table.name, end="")
        if not table.tableType.isView():
            print(' (table)', end='')
        else:
            print(' (view)', end='')
        print('')
        table_remarks = table.remarks
        if table_remarks:
            print(table_remarks)

        print('')
        print('### Columns')
        for column in table.columns:
            print('- ', end='')
            part_of_primary_key = column.isPartOfPrimaryKey()
            part_of_foreign_key = column.isPartOfForeignKey()
            if part_of_primary_key:
                print('**', end='')
            elif part_of_foreign_key:
                print('*', end='')
            print(column.name, end='')
            if part_of_primary_key:
                print('**', end='')
            elif part_of_foreign_key:
                print('*', end='')
            print(' (' + column.columnDataType.toString() + ')', end='')
            column_remarks = column.remarks
            if column_remarks:
                print('    ')
                print('\n    '.join(column_remarks.splitlines()), end='')
            print()

        if table.hasPrimaryKey():
            print('')
            print('### Primary Key')
            primaryKey = table.primaryKey
            print('- ' + primaryKey.name + ' ('
                  + MetaDataUtility.getColumnsListAsString(primaryKey, identifiers) + ') ')

        if not table.indexes.isEmpty():
            print('')
            print('### Indexes')
            for index in table.indexes:
                if table.hasPrimaryKey() and \
                        MetaDataUtility.getColumnsListAsString(table.primaryKey, identifiers) == \
                        MetaDataUtility.getColumnsListAsString(index, identifiers):
                    continue
                print('- ' + index.name + ' ('
                      + MetaDataUtility.getColumnsListAsString(index, identifiers) + ')',
                      end='')
                if index.unique:
                    print(' (unique index)', end='')
                print('')

        if not table.referencingTables.isEmpty():
            print('')
            print('### Foreign Keys')
            for childTable in table.referencingTables:
                print('-  ' + table.fullName + ' --> ' + childTable.fullName)

        print('')
        print('')
        print('')
