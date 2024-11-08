/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static schemacrawler.tools.command.chatgpt.functions.TableDecriptionFunctionParameters.TableDescriptionScope.COLUMNS;
import static schemacrawler.tools.command.chatgpt.functions.TableDecriptionFunctionParameters.TableDescriptionScope.DEFAULT;
import static schemacrawler.tools.command.chatgpt.functions.TableDecriptionFunctionParameters.TableDescriptionScope.FOREIGN_KEYS;
import static schemacrawler.tools.command.chatgpt.functions.TableDecriptionFunctionParameters.TableDescriptionScope.INDEXES;
import static schemacrawler.tools.command.chatgpt.functions.TableDecriptionFunctionParameters.TableDescriptionScope.PRIMARY_KEY;
import static schemacrawler.tools.command.chatgpt.functions.TableDecriptionFunctionParameters.TableDescriptionScope.TRIGGERS;
import java.util.function.Function;
import java.util.regex.Pattern;
import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.GrepOptionsBuilder;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.command.chatgpt.functions.TableDecriptionFunctionParameters.TableDescriptionScope;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.options.Config;

public final class TableDecriptionFunctionDefinition
    extends AbstractExecutableFunctionDefinition<TableDecriptionFunctionParameters> {

  public TableDecriptionFunctionDefinition() {
    super(
        "Gets the details and description of database tables or views, including columns, foreign keys, indexes and triggers.",
        TableDecriptionFunctionParameters.class);
  }

  @Override
  protected Config createAdditionalConfig(final TableDecriptionFunctionParameters args) {
    final TableDescriptionScope scope = args.getDescriptionScope();
    final SchemaTextOptionsBuilder schemaTextOptionsBuilder = SchemaTextOptionsBuilder.builder();
    if (scope != DEFAULT) {
      if (scope != COLUMNS) {
        schemaTextOptionsBuilder.noTableColumns();
      } // fall through - no else
      if (scope != PRIMARY_KEY) {
        schemaTextOptionsBuilder.noPrimaryKeys();
      } // fall through - no else
      if (scope != FOREIGN_KEYS) {
        schemaTextOptionsBuilder.noForeignKeys();
        schemaTextOptionsBuilder.noWeakAssociations();
      } // fall through - no else
      if (scope != INDEXES) {
        schemaTextOptionsBuilder.noIndexes();
      } // fall through - no else
      if (scope != TRIGGERS) {
        schemaTextOptionsBuilder.noTriggers();
      } // fall through - no else
    }
    schemaTextOptionsBuilder.noTableConstraints().noAlternateKeys().noInfo();
    return schemaTextOptionsBuilder.toConfig();
  }

  @Override
  protected SchemaCrawlerOptions createSchemaCrawlerOptions(
      final TableDecriptionFunctionParameters args) {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSynonyms(new ExcludeAll())
            .includeSequences(new ExcludeAll())
            .includeRoutines(new ExcludeAll());
    final Pattern grepTablesPattern = makeNameInclusionPattern(args.getTableName());
    final GrepOptionsBuilder grepOptionsBuilder =
        GrepOptionsBuilder.builder().includeGreppedTables(grepTablesPattern);
    return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
        .withLimitOptions(limitOptionsBuilder.toOptions())
        .withGrepOptions(grepOptionsBuilder.toOptions());
  }

  @Override
  protected String getCommand() {
    return "schema";
  }

  @Override
  protected Function<Catalog, Boolean> getResultsChecker(
      final TableDecriptionFunctionParameters args) {
    return catalog -> !catalog.getTables().isEmpty();
  }
}
