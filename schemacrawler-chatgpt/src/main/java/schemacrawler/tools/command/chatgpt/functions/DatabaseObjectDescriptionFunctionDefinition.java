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

import static schemacrawler.tools.command.chatgpt.functions.DatabaseObjectDescriptionFunctionParameters.DatabaseObjectsScope.ROUTINES;
import static schemacrawler.tools.command.chatgpt.functions.DatabaseObjectDescriptionFunctionParameters.DatabaseObjectsScope.SEQUENCES;
import static schemacrawler.tools.command.chatgpt.functions.DatabaseObjectDescriptionFunctionParameters.DatabaseObjectsScope.SYNONYMS;
import java.util.function.Function;
import java.util.regex.Pattern;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.command.chatgpt.functions.DatabaseObjectDescriptionFunctionParameters.DatabaseObjectsScope;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.options.Config;

public final class DatabaseObjectDescriptionFunctionDefinition
    extends AbstractExecutableFunctionDefinition<DatabaseObjectDescriptionFunctionParameters> {

  @JsonPropertyDescription(
      "Part of the name of database objects to find. For example, 'ABC' is a part of 'QWEABCXYZ'.")
  @JsonProperty(required = true)
  private String objectNameContains;

  public DatabaseObjectDescriptionFunctionDefinition() {
    super(
        "Gets the details and description of database objects like routines (that is, functions and stored procedures), sequences, or synonyms.",
        DatabaseObjectDescriptionFunctionParameters.class);
  }

  public String getObjectNameContains() {
    return objectNameContains;
  }

  public void setObjectNameContains(final String objectNameContains) {
    this.objectNameContains = objectNameContains;
  }

  @Override
  protected Config createAdditionalConfig(final DatabaseObjectDescriptionFunctionParameters args) {
    final DatabaseObjectsScope scope = args.getDatabaseObjectsScope();
    final SchemaTextOptionsBuilder schemaTextOptionsBuilder = SchemaTextOptionsBuilder.builder();
    if (scope != SEQUENCES) {
      schemaTextOptionsBuilder.noSequences();
    } // fall through - no else
    if (scope != SYNONYMS) {
      schemaTextOptionsBuilder.noSynonyms();
    } // fall through - no else
    if (scope != ROUTINES) {
      schemaTextOptionsBuilder.noRoutines();
    } // fall through - no else
    schemaTextOptionsBuilder.noInfo();
    return schemaTextOptionsBuilder.toConfig();
  }

  @Override
  protected SchemaCrawlerOptions createSchemaCrawlerOptions(
      final DatabaseObjectDescriptionFunctionParameters args) {

    final Pattern inclusionPattern =
        Pattern.compile(String.format(".*(?i)%s(?-i).*", args.getDatabaseObjectNameContains()));
    final DatabaseObjectsScope scope = args.getDatabaseObjectsScope();
    final LimitOptionsBuilder limitOptionsBuilder = LimitOptionsBuilder.builder();
    switch (scope) {
      case SEQUENCES:
        limitOptionsBuilder.includeSequences(new RegularExpressionInclusionRule(inclusionPattern));
      case SYNONYMS:
        limitOptionsBuilder.includeSynonyms(new RegularExpressionInclusionRule(inclusionPattern));
      case ROUTINES:
        limitOptionsBuilder.includeRoutines(new RegularExpressionInclusionRule(inclusionPattern));
      default:
        // No action
    }
    limitOptionsBuilder.includeTables(new ExcludeAll());

    return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
        .withLimitOptions(limitOptionsBuilder.toOptions());
  }

  @Override
  protected String getCommand() {
    return "schema";
  }

  @Override
  protected Function<Catalog, Boolean> getResultsChecker(
      final DatabaseObjectDescriptionFunctionParameters args) {
    final DatabaseObjectsScope scope = args.getDatabaseObjectsScope();
    switch (scope) {
      case SEQUENCES:
        return catalog -> !catalog.getSequences().isEmpty();
      case SYNONYMS:
        return catalog -> !catalog.getSynonyms().isEmpty();
      case ROUTINES:
        return catalog -> !catalog.getRoutines().isEmpty();
      default:
        return catalog -> false;
    }
  }
}
