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
    project_name = title
else:
    project_name = catalog.crawlInfo.runId

print('Project "' + project_name + '" {')
print('  database_type: "' + re.sub(r'\"', '',
                                    catalog.crawlInfo.databaseVersion.toString()) + '"')
print("  Note: '''")
print(catalog.crawlInfo)
print("  '''")
print("}")

identifiers = \
      IdentifiersBuilder.builder() \
          .withIdentifierQuotingStrategy(IdentifierQuotingStrategy.quote_all) \
          .toOptions()

# Columns
for table in catalog.getTables():
    print('Table "' + re.sub(r'\"', '', table.fullName) + '" {')
    for column in table.columns:
        print('  "' + column.name + '" "' + column.columnDataType.name + '"',
              end='')
        # Column attributes
        print(' [', end='')
        if not column.nullable:
            print('not ', end='')
        print('null', end='')
        if column.hasDefaultValue():
            print(', default: "' + column.defaultValue + '"', end='')
        if column.hasRemarks():
            print(', note: "' + column.remarks + '"', end='')
        print(']', end='')
        print()
    if table.hasRemarks():
        print("  Note: '''")
        print(table.remarks)
        print("  '''")
    # Primary keys and indexes
    if table.hasPrimaryKey() or not table.indexes.isEmpty():
        print('  indexes {')
        if table.hasPrimaryKey():
            primaryKey = table.primaryKey
            print('    ('
                  + MetaDataUtility.getColumnsListAsString(primaryKey, identifiers) + ') '
                  + '[pk]')
        if not table.indexes.isEmpty():
            for index in table.indexes:
                if table.hasPrimaryKey() and \
                    MetaDataUtility.getColumnsListAsString(table.primaryKey, identifiers) == \
                    MetaDataUtility.getColumnsListAsString(index, identifiers):
                    continue
                print('    ('
                      + MetaDataUtility.getColumnsListAsString(index, identifiers) + ')',
                      end='')
                print(' [name: "' + index.name + '"', end='')
                if index.unique:
                    print(', unique', end='')
                print(']')
        print('  }')
    print('}')
    print('')

# Foreign keys
for table in catalog.tables:
    for fk in table.exportedForeignKeys:
        print('Ref "' + fk.name + '" {')
        pkTable = None
        fkTable = None
        for columnReference in fk.columnReferences:
            pkTable = columnReference.primaryKeyColumn.parent
            fkTable = columnReference.foreignKeyColumn.parent
        print('  "' \
              + re.sub(r'\"', '', pkTable.fullName) + '".('
              + MetaDataUtility.getColumnsListAsString(fk,
                                                       TableRelationshipType.parent,
                                                       identifiers)
              + ') < "'
              + re.sub(r'\"', '', fkTable.fullName) + '".('
              + MetaDataUtility.getColumnsListAsString(fk,
                                                       TableRelationshipType.child,
                                                       identifiers)
              + ')', end='')
        print(
            ' [update: ' + fk.updateRule.toString() + ', delete: ' + fk.deleteRule.toString() + ']',
            end='')
        print()
        print("}")
        print('')
print('')

# Table groups
for schema in catalog.schemas:
    print('TableGroup "' + re.sub(r'\"', '', schema.fullName) + ' " {')
    for table in catalog.getTables(schema):
        print('  "' + re.sub(r'\"', '', table.fullName) + '"')
    print('}')
    print('')
