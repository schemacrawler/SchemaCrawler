/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.script;

import static java.util.stream.Collectors.toList;
import static us.fatehi.utility.Utility.isBlank;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnReference;
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

  private static final String GENERATED_FK_PREFIX = "SCHCRWLR_";

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

  public String foreignKeyColumns(final ForeignKey foreignKey) {
    if (foreignKey == null) {
      return "";
    }
    return MetaDataUtility.joinColumns(
        foreignKey.getConstrainedColumns(), false, quotedIdentifiers);
  }

  public Table foreignKeyTable(final ForeignKey foreignKey) {
    return foreignKey == null ? null : foreignKey.getForeignKeyTable();
  }

  public boolean hasName(final ForeignKey foreignKey) {
    if (foreignKey == null) {
      return false;
    }
    final String name = foreignKey.getName();
    return !isBlank(name) && !name.startsWith(GENERATED_FK_PREFIX);
  }

  public boolean isView(final Table table) {
    return table != null && table.getTableType() != null && table.getTableType().isView();
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

  public String primaryKeyColumns(final ForeignKey foreignKey) {
    if (foreignKey == null) {
      return "";
    }
    final List<Column> pkColumns =
        foreignKey.getColumnReferences().stream()
            .map(ColumnReference::getPrimaryKeyColumn)
            .collect(toList());
    return MetaDataUtility.joinColumns(pkColumns, false, quotedIdentifiers);
  }

  public Table primaryKeyTable(final ForeignKey foreignKey) {
    return foreignKey == null ? null : foreignKey.getPrimaryKeyTable();
  }

  private boolean isPrimaryKeyEquivalentIndex(final Table table, final Index index) {
    if (table == null || index == null || !table.hasPrimaryKey()) {
      return false;
    }
    return Objects.equals(columns(table.getPrimaryKey()), columns(index));
  }
}
