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
import static us.fatehi.utility.Utility.isBlank;
import java.util.Collection;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.IdentifierQuotingStrategy;
import schemacrawler.schemacrawler.Identifiers;
import schemacrawler.schemacrawler.IdentifiersBuilder;
import schemacrawler.tools.command.chatgpt.functions.DatabaseObjectListFunctionParameters.DatabaseObjectType;

public class DatabaseObjectListFunctionReturn implements FunctionReturn {

  private static final String NEW_LINE = String.format("%n");
  private static final Identifiers identifiers =
      IdentifiersBuilder.builder()
          .withIdentifierQuotingStrategy(IdentifierQuotingStrategy.quote_all)
          .toOptions();
  private final DatabaseObjectType databaseObjectType;
  private final Collection<? extends NamedObject> databaseObjects;

  public DatabaseObjectListFunctionReturn(
      final DatabaseObjectType databaseObjectType,
      final Collection<? extends NamedObject> databaseObjects) {
    this.databaseObjectType =
        requireNonNull(databaseObjectType, "No database object type provided");
    this.databaseObjects = requireNonNull(databaseObjects, "No database objects provided");
  }

  @Override
  public String render() {
    if (databaseObjects.isEmpty()) {
      return noData();
    }
    final StringBuilder buffer = new StringBuilder();

    if (databaseObjectType == DatabaseObjectType.SCHEMAS) {
      renderSchemas(buffer);
    } else {
      renderDatabaseObjects(buffer);
    }
    return buffer.toString();
  }

  protected void renderDatabaseObjects(final StringBuilder buffer) {
    buffer
        .append("These are the ")
        .append(databaseObjectType.toReadableString())
        .append(" in the database catalog:")
        .append(NEW_LINE);
    for (final NamedObject databaseObject : databaseObjects) {
      buffer
          .append("- ")
          .append(identifiers.quoteFullName((DatabaseObject) databaseObject))
          .append(NEW_LINE);
    }
  }

  protected void renderSchemas(final StringBuilder buffer) {
    if (databaseObjects.size() == 1) {
      final NamedObject schema = databaseObjects.stream().findFirst().get();
      if (isBlank(schema.getFullName())) {
        buffer.append("The database catalog has only the default schema.").append(NEW_LINE);
        return;
      }
    }
    buffer.append("These are the schemas in the database catalog:").append(NEW_LINE);
    for (final NamedObject databaseObject : databaseObjects) {
      buffer
          .append("- ")
          .append(identifiers.quoteFullName((Schema) databaseObject))
          .append(NEW_LINE);
    }
  }

  private String noData() {
    final StringBuilder buffer = new StringBuilder();
    buffer
        .append("No ")
        .append(databaseObjectType.toReadableString())
        .append(" found in the database catalog")
        .append(".")
        .append(NEW_LINE);
    return buffer.toString();
  }
}
