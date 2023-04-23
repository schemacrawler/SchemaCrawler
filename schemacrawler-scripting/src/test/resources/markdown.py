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
        .withIdentifierQuotingStrategy(IdentifierQuotingStrategy.quote_all) \
        .toOptions()

print('')
for table in catalog.tables:
    print('##  ' + table.fullName)

    print('')
    print('## Columns')
    for column in table.columns:
        print('- ' + column.name + ' (' + column.columnDataType.toString() + ')')

    if table.hasPrimaryKey():
        print('')
        print('## Primary Key')
        primaryKey = table.primaryKey
        print('('
              + MetaDataUtility.getColumnsListAsString(primaryKey, identifiers) + ') ')

    if not table.indexes.isEmpty():
        print('')
        print('## Indexes')
        for index in table.indexes:
            if table.hasPrimaryKey() and \
                    MetaDataUtility.getColumnsListAsString(table.primaryKey, identifiers) == \
                    MetaDataUtility.getColumnsListAsString(index, identifiers):
                continue
            print('- ' + index.name + ' ('
                  + MetaDataUtility.getColumnsListAsString(index, identifiers) + ')',
                  end='')
            if index.unique:
                print(', unique index', end='')
            print('')

    if not table.referencingTables.isEmpty():
        print('')
        print('## Foreign Keys')
        for childTable in table.referencingTables:
            print('-  ' + table.fullName + ' --> ' + childTable.fullName)

    print('')
    print('')
    print('')
