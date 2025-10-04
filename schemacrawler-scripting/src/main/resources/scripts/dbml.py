# Copyright (c) Sualeh Fatehi
# SPDX-License-Identifier: EPL-2.0

import re
import java

# Import Java classes explicitly using java.type
TableRelationshipType = java.type("schemacrawler.schema.TableRelationshipType")
IdentifiersBuilder = java.type("schemacrawler.schemacrawler.IdentifiersBuilder")
IdentifierQuotingStrategy = java.type("schemacrawler.schemacrawler.IdentifierQuotingStrategy")
MetaDataUtility = java.type("schemacrawler.utility.MetaDataUtility")

if title:
    project_name = title
else:
    project_name = catalog.getCrawlInfo().getRunId()

print('Project "' + project_name + '" {')
print('  database_type: "' + re.sub(r'\"', '',
                                   catalog.getCrawlInfo().getDatabaseVersion().toString()) + '"')
print("  Note: '''")
print(catalog.getCrawlInfo())
print("  '''")
print("}")

identifiers = (IdentifiersBuilder.builder()
               .withIdentifierQuotingStrategy(IdentifierQuotingStrategy.quote_all)
               .toOptions())

# Columns
for table in catalog.getTables():
    print('Table "' + re.sub(r'\"', '', table.getFullName()) + '" {')
    for column in table.getColumns():
        print('  "' + column.getName() + '" "' + column.getColumnDataType().getName() + '"',
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
                  + MetaDataUtility.getColumnsListAsString(primaryKey, identifiers) + ') '
                  + '[pk]')
        if not table.getIndexes().isEmpty():
            for index in table.getIndexes():
                if (table.hasPrimaryKey() and
                    MetaDataUtility.getColumnsListAsString(table.getPrimaryKey(), identifiers) ==
                    MetaDataUtility.getColumnsListAsString(index, identifiers)):
                    continue
                print('    ('
                      + MetaDataUtility.getColumnsListAsString(index, identifiers) + ')',
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
        pkTable = None
        fkTable = None
        for columnReference in fk.getColumnReferences():
            pkTable = columnReference.getPrimaryKeyColumn().getParent()
            fkTable = columnReference.getForeignKeyColumn().getParent()
        print('  "' \
              + re.sub(r'\"', '', pkTable.getFullName()) + '".('
              + MetaDataUtility.getColumnsListAsString(fk,
                                                       TableRelationshipType.parent,
                                                       identifiers)
              + ') < "'
              + re.sub(r'\"', '', fkTable.getFullName()) + '".('
              + MetaDataUtility.getColumnsListAsString(fk,
                                                       TableRelationshipType.child,
                                                       identifiers)
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
