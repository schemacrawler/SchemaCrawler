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

import static schemacrawler.tools.command.chatgpt.functions.DatabaseObjectListFunctionParameters.DatabaseObjectType.ALL;
import static schemacrawler.tools.command.chatgpt.functions.DatabaseObjectListFunctionParameters.DatabaseObjectType.ROUTINES;
import static schemacrawler.tools.command.chatgpt.functions.DatabaseObjectListFunctionParameters.DatabaseObjectType.SEQUENCES;
import static schemacrawler.tools.command.chatgpt.functions.DatabaseObjectListFunctionParameters.DatabaseObjectType.SYNONYMS;
import static schemacrawler.tools.command.chatgpt.functions.DatabaseObjectListFunctionParameters.DatabaseObjectType.TABLES;
import java.util.function.Function;
import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.command.chatgpt.functions.DatabaseObjectListFunctionParameters.DatabaseObjectType;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.options.Config;

public final class DatabaseObjectListFunctionDefinition
    extends AbstractExecutableFunctionDefinition<DatabaseObjectListFunctionParameters> {

  public DatabaseObjectListFunctionDefinition() {
    super(
        "database-object-list",
        "Lists database objects like tables, routines (that is, functions and stored procedures), sequences, or synonyms.",
        DatabaseObjectListFunctionParameters.class);
  }

  @Override
  protected Config createAdditionalConfig(final DatabaseObjectListFunctionParameters args) {
    final DatabaseObjectType databaseObjectType = args.getDatabaseObjectType();
    final SchemaTextOptionsBuilder schemaTextOptionsBuilder = SchemaTextOptionsBuilder.builder();
    if (databaseObjectType != ALL) {
      if (databaseObjectType != TABLES) {
        schemaTextOptionsBuilder.noTables();
      } // fall through - no else
      if (databaseObjectType != ROUTINES) {
        schemaTextOptionsBuilder.noRoutines();
      } // fall through - no else
      if (databaseObjectType != SEQUENCES) {
        schemaTextOptionsBuilder.noSequences();
      } // fall through - no else
      if (databaseObjectType != SYNONYMS) {
        schemaTextOptionsBuilder.noSynonyms();
      } // fall through - no else
    }
    schemaTextOptionsBuilder.noInfo();
    return schemaTextOptionsBuilder.toConfig();
  }

  @Override
  protected SchemaCrawlerOptions createSchemaCrawlerOptions(
      final DatabaseObjectListFunctionParameters args) {
    final DatabaseObjectType databaseObjectType = args.getDatabaseObjectType();
    final LimitOptionsBuilder limitOptionsBuilder = LimitOptionsBuilder.builder();
    if (databaseObjectType != ALL) {
      if (databaseObjectType != TABLES) {
        limitOptionsBuilder.includeTables(new ExcludeAll());
      } // fall through - no else
      if (databaseObjectType == ROUTINES) {
        limitOptionsBuilder.includeAllRoutines();
      } // fall through - no else
      if (databaseObjectType == SEQUENCES) {
        limitOptionsBuilder.includeAllSequences();
      } // fall through - no else
      if (databaseObjectType == SYNONYMS) {
        limitOptionsBuilder.includeAllSynonyms();
      } // fall through - no else
    }
    return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
        .withLimitOptions(limitOptionsBuilder.toOptions());
  }

  @Override
  protected String getCommand() {
    return "list";
  }

  @Override
  protected Function<Catalog, Boolean> getResultsChecker(
      final DatabaseObjectListFunctionParameters args) {
    final DatabaseObjectType databaseObjectType = args.getDatabaseObjectType();
    switch (databaseObjectType) {
      case TABLES:
        return catalog -> !catalog.getTables().isEmpty();
      case ROUTINES:
        return catalog -> !catalog.getRoutines().isEmpty();
      case SEQUENCES:
        return catalog -> !catalog.getSequences().isEmpty();
      case SYNONYMS:
        return catalog -> !catalog.getSynonyms().isEmpty();
      default:
        return catalog ->
            !(catalog.getTables().isEmpty()
                && catalog.getRoutines().isEmpty()
                && catalog.getSequences().isEmpty()
                && catalog.getSynonyms().isEmpty());
    }
  }
}
