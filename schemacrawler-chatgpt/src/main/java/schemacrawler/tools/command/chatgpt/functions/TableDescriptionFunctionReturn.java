/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.tools.command.chatgpt.functions;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.command.chatgpt.functions.TableDecriptionFunctionParameters.TableDescriptionScope.FOREIGN_KEYS;
import static schemacrawler.tools.command.chatgpt.functions.TableDecriptionFunctionParameters.TableDescriptionScope.INDEXES;
import static schemacrawler.tools.command.chatgpt.functions.TableDecriptionFunctionParameters.TableDescriptionScope.PRIMARY_KEY;
import static schemacrawler.tools.command.chatgpt.functions.TableDecriptionFunctionParameters.TableDescriptionScope.TRIGGERS;
import static schemacrawler.utility.MetaDataUtility.joinColumns;
import java.util.Collection;
import schemacrawler.schema.ActionOrientationType;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.ConditionTimingType;
import schemacrawler.schema.EventManipulationType;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Index;
import schemacrawler.schema.IndexType;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Table;
import schemacrawler.schema.Trigger;
import schemacrawler.schema.View;
import schemacrawler.schemacrawler.IdentifierQuotingStrategy;
import schemacrawler.schemacrawler.Identifiers;
import schemacrawler.schemacrawler.IdentifiersBuilder;
import schemacrawler.tools.command.chatgpt.functions.TableDecriptionFunctionParameters.TableDescriptionScope;

public class TableDescriptionFunctionReturn implements FunctionReturn {

  private static final String NEW_LINE = String.format("%n");
  private static final Identifiers identifiers =
      IdentifiersBuilder.builder()
          .withIdentifierQuotingStrategy(IdentifierQuotingStrategy.quote_all)
          .toOptions();
  private final Table table;

  private final TableDescriptionScope scope;

  protected TableDescriptionFunctionReturn(final Table table, final TableDescriptionScope scope) {
    this.table = requireNonNull(table, "Table not provided");
    this.scope = requireNonNull(scope, "Table description scope not provided");
  }

  @Override
  public String render() {
    switch (scope) {
      case COLUMNS:
        return renderColumns();
      case PRIMARY_KEY:
        return renderPrimaryKey();
      case INDEXES:
        return renderIndexes();
      case FOREIGN_KEYS:
        return renderForeignKeys();
      case TRIGGERS:
        return renderTriggers();
      default:
        return renderColumns()
            + NEW_LINE
            + renderPrimaryKey()
            + NEW_LINE
            + renderIndexes()
            + NEW_LINE
            + renderForeignKeys()
            + NEW_LINE
            + renderTriggers();
    }
  }

  private String noData(final TableDescriptionScope scope) {
    final StringBuilder buffer = new StringBuilder();
    if (table instanceof View) {
      buffer.append("View ");
    } else {
      buffer.append("Table ");
    }
    buffer
        .append(identifiers.quoteFullName(table))
        .append(" has no ")
        .append(scope.toReadableString())
        .append(".")
        .append(NEW_LINE);
    return buffer.toString();
  }

  private String renderColumns() {
    final StringBuilder buffer = new StringBuilder();
    tableName(buffer, TableDescriptionScope.COLUMNS);

    for (final Column column : table.getColumns()) {
      buffer.append(
          String.format(
              "%2d. %s of type %s%s%s%n",
              column.getOrdinalPosition(),
              identifiers.quoteName(column.getName()),
              column.getType() + column.getWidth(),
              column.isNullable() ? "" : " and is not nullable",
              column.isAutoIncremented() ? " and is auto-incremented" : ""));
    }
    return buffer.toString();
  }

  private String renderForeignKeys() {

    final Collection<ForeignKey> foreignKeys = table.getImportedForeignKeys();
    if (foreignKeys.isEmpty()) {
      return noData(FOREIGN_KEYS);
    }

    final StringBuilder buffer = new StringBuilder();
    tableName(buffer, FOREIGN_KEYS);

    for (final ForeignKey foreignKey : foreignKeys) {
      buffer
          .append(
              String.format("- %s with references", identifiers.quoteName(foreignKey.getName())))
          .append(NEW_LINE);
      for (final ColumnReference columnReference : foreignKey.getColumnReferences()) {
        buffer
            .append(
                String.format(
                    "    %s to %s",
                    identifiers.quoteName(columnReference.getForeignKeyColumn().getName()),
                    identifiers.quoteFullName(columnReference.getPrimaryKeyColumn())))
            .append(NEW_LINE);
      }
    }
    return buffer.toString();
  }

  private String renderIndexes() {

    final Collection<Index> indexes = table.getIndexes();
    if (indexes.isEmpty()) {
      return noData(INDEXES);
    }

    final StringBuilder buffer = new StringBuilder();
    tableName(buffer, INDEXES);

    for (final Index index : indexes) {
      final String indexType =
          index.getType() == IndexType.other || index.getType() == IndexType.unknown
              ? ""
              : " (" + index.getType() + ")";
      buffer
          .append(
              String.format(
                  "- %s%s on columns %s",
                  identifiers.quoteName(index.getName()),
                  indexType,
                  joinColumns(index.getColumns(), false, identifiers)))
          .append(NEW_LINE);
    }
    return buffer.toString();
  }

  private String renderPrimaryKey() {
    if (!table.hasPrimaryKey()) {
      return noData(PRIMARY_KEY);
    }

    final StringBuilder buffer = new StringBuilder();
    tableName(buffer, PRIMARY_KEY);

    final PrimaryKey primaryKey = table.getPrimaryKey();
    buffer
        .append(
            String.format(
                "%s on columns %s",
                identifiers.quoteName(primaryKey.getName()),
                joinColumns(primaryKey.getConstrainedColumns(), false, identifiers)))
        .append(NEW_LINE);
    return buffer.toString();
  }

  private String renderTriggers() {

    final Collection<Trigger> triggers = table.getTriggers();
    if (triggers.isEmpty()) {
      return noData(TRIGGERS);
    }

    final StringBuilder buffer = new StringBuilder();
    tableName(buffer, TRIGGERS);

    for (final Trigger trigger : triggers) {
      buffer.append(String.format("- %s", identifiers.quoteName(trigger.getName())));
      if (trigger.getConditionTiming() != ConditionTimingType.unknown
          && trigger.getEventManipulationType() != EventManipulationType.unknown) {
        buffer.append(
            String.format(
                " %s %s", trigger.getConditionTiming(), trigger.getEventManipulationType()));
      }
      if (trigger.getActionOrientation() != ActionOrientationType.unknown) {
        buffer.append(String.format(" on %s", trigger.getActionOrientation()));
      }
      buffer.append(NEW_LINE);
    }
    return buffer.toString();
  }

  private void tableName(final StringBuilder buffer, final TableDescriptionScope scope) {
    requireNonNull(buffer);
    if (table instanceof View) {
      buffer.append("View ");
    } else {
      buffer.append("Table ");
    }
    buffer
        .append(identifiers.quoteFullName(table))
        .append(" has the following ")
        .append(scope.toReadableString())
        .append(":")
        .append(NEW_LINE);
  }
}
