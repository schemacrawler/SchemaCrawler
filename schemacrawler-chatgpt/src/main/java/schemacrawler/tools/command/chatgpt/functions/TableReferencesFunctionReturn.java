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
import java.util.Collection;
import java.util.Collections;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableRelationshipType;
import schemacrawler.schema.View;
import schemacrawler.schemacrawler.IdentifierQuotingStrategy;
import schemacrawler.schemacrawler.Identifiers;
import schemacrawler.schemacrawler.IdentifiersBuilder;
import schemacrawler.tools.command.chatgpt.functions.TableReferencesFunctionParameters.TableReferenceType;

public class TableReferencesFunctionReturn implements FunctionReturn {

  private static final String NEW_LINE = String.format("%n");
  private static final Identifiers identifiers =
      IdentifiersBuilder.builder()
          .withIdentifierQuotingStrategy(IdentifierQuotingStrategy.quote_all)
          .toOptions();
  private final Table table;
  private final TableReferenceType tableReferenceType;

  protected TableReferencesFunctionReturn(final Table table, final TableReferenceType scope) {
    this.table = requireNonNull(table, "Table not provided");
    this.tableReferenceType = requireNonNull(scope, "Table description scope not provided");
  }

  @Override
  public String render() {
    switch (tableReferenceType) {
      case PARENT:
        return renderTableRelationships(TableReferenceType.PARENT);
      case CHILD:
        return renderTableRelationships(TableReferenceType.CHILD);
      case ALL:
        // Fall-through
      default:
        return renderTableRelationships(TableReferenceType.PARENT)
            + NEW_LINE
            + renderTableRelationships(TableReferenceType.CHILD);
    }
  }

  private String noData(final TableReferenceType tableReferenceType) {
    final StringBuilder buffer = new StringBuilder();
    if (table instanceof View) {
      buffer.append("View ");
    } else {
      buffer.append("Table ");
    }
    buffer
        .append(identifiers.quoteFullName(table))
        .append(" has no ")
        .append(tableReferenceType.name().toLowerCase())
        .append(" relationships.")
        .append(NEW_LINE);
    return buffer.toString();
  }

  private String renderTableRelationships(final TableReferenceType tableReferenceType) {
    requireNonNull(tableReferenceType, "No table relationship type specified");

    final Collection<Table> referencedTables;
    switch (tableReferenceType) {
      case PARENT:
        referencedTables = table.getRelatedTables(TableRelationshipType.parent);
        break;
      case CHILD:
        referencedTables = table.getRelatedTables(TableRelationshipType.child);
        break;
      default:
        referencedTables = Collections.emptyList();
    }

    if (referencedTables.isEmpty()) {
      return noData(tableReferenceType);
    }

    final StringBuilder buffer = new StringBuilder();
    tableName(buffer, tableReferenceType);

    for (final Table referencedTable : referencedTables) {
      buffer
          .append(String.format("- %s", identifiers.quoteFullName(referencedTable)))
          .append(NEW_LINE);
    }
    return buffer.toString();
  }

  private void tableName(final StringBuilder buffer, final TableReferenceType tableReferenceType) {
    requireNonNull(buffer);
    if (table instanceof View) {
      buffer.append("View ");
    } else {
      buffer.append("Table ");
    }
    buffer
        .append(identifiers.quoteFullName(table))
        .append(" has the following ")
        .append(tableReferenceType.name().toLowerCase())
        .append(" tables:")
        .append(NEW_LINE);
  }
}
