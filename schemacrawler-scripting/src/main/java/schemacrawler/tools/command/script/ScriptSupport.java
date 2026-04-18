/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.script;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.DescribedObject;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.IdentifierQuotingStrategy;
import schemacrawler.schema.Identifiers;
import schemacrawler.schema.IdentifiersBuilder;
import schemacrawler.schema.Index;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Table;
import schemacrawler.utility.MetaDataUtility;

public final class ScriptSupport {

  private final Identifiers quotedIdentifiers;

  public ScriptSupport() {
    quotedIdentifiers =
        IdentifiersBuilder.builder()
            .withIdentifierQuotingStrategy(IdentifierQuotingStrategy.quote_all)
            .toOptions();
  }

  public String cleanFullName(final NamedObject namedObject) {
    if (namedObject == null) {
      return "";
    }
    return namedObject.getFullName().replace("\"", "");
  }

  public String cleanName(final NamedObject namedObject) {
    if (namedObject == null) {
      return "";
    }
    return namedObject.getName().replace("\"", "");
  }

  public List<ColumnReference> columnReferences(final ForeignKey foreignKey) {
    final List<ColumnReference> refs = new ArrayList<>();
    if (foreignKey == null || foreignKey.getColumnReferences() == null) {
      return refs;
    }
    refs.addAll(foreignKey.getColumnReferences());
    return refs;
  }

  public String columns(final Index index) {
    if (index == null) {
      return "";
    }
    return MetaDataUtility.getColumnsListAsString(index, quotedIdentifiers);
  }

  public String columns(final PrimaryKey primaryKey) {
    if (primaryKey == null) {
      return "";
    }
    return MetaDataUtility.getColumnsListAsString(primaryKey, quotedIdentifiers);
  }

  public String columnType(final Column column) {
    if (column == null || column.getColumnDataType() == null) {
      return "";
    }
    return column.getColumnDataType().getName();
  }

  public String fkColumns(final ForeignKey foreignKey) {
    if (foreignKey == null) {
      return "";
    }
    return MetaDataUtility.joinColumns(
        foreignKey.getConstrainedColumns(), false, quotedIdentifiers);
  }

  public boolean hasName(final ForeignKey foreignKey) {
    return !MetaDataUtility.isSystemGeneratedForeignKeyName(foreignKey);
  }

  public String indent(final String text, final int indent) {
    if (text == null) {
      return "";
    }
    return text.indent(indent);
  }

  public List<Index> nonPrimaryIndexes(final Table table) {
    final List<Index> indexes = new ArrayList<>();
    if (table == null || table.getIndexes() == null || table.getIndexes().isEmpty()) {
      return indexes;
    }
    for (final Index index : table.getIndexes()) {
      if (!isPrimaryKeyEquivalentIndex(table, index)) {
        indexes.add(index);
      }
    }
    return indexes;
  }

  public String pkColumns(final ForeignKey foreignKey) {
    if (foreignKey == null) {
      return "";
    }
    final List<Column> pkColumns =
        foreignKey.getColumnReferences().stream()
            .map(ColumnReference::getPrimaryKeyColumn)
            .collect(toList());
    return MetaDataUtility.joinColumns(pkColumns, false, quotedIdentifiers);
  }

  /**
   * Puts remarks on a single line.
   *
   * @param describedObject Object with remarks
   * @return Remarks on a single line
   */
  public String remarks(final DescribedObject describedObject) {
    if (describedObject == null || !describedObject.hasRemarks()) {
      return "";
    }
    return describedObject.getRemarks().replaceAll("\\R", " ").replace('"', '\'').strip();
  }

  public String stripName(final NamedObject namedObject) {
    if (namedObject == null) {
      return "";
    }
    return namedObject.getName().replace("[^\\d\\w\\-]", "");
  }

  public String type(final Table table) {
    return MetaDataUtility.getSimpleTypeName(table).toString();
  }

  private boolean isPrimaryKeyEquivalentIndex(final Table table, final Index index) {
    if (table == null || index == null || !table.hasPrimaryKey()) {
      return false;
    }
    return Objects.equals(columns(table.getPrimaryKey()), columns(index));
  }
}
