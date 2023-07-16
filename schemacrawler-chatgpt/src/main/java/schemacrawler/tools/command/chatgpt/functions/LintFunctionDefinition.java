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

import java.util.function.Function;
import java.util.regex.Pattern;
import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.GrepOptionsBuilder;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.options.Config;

public final class LintFunctionDefinition
    extends AbstractExecutableFunctionDefinition<LintFunctionParameters> {

  public LintFunctionDefinition() {
    super(
        "Lint database schemas. Find problems with database design, such as no indexes on foreign keys.",
        LintFunctionParameters.class);
  }

  @Override
  protected Config createAdditionalConfig(final LintFunctionParameters args) {
    return new Config();
  }

  @Override
  protected SchemaCrawlerOptions createSchemaCrawlerOptions(final LintFunctionParameters args) {
    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSynonyms(new ExcludeAll())
            .includeSequences(new ExcludeAll())
            .includeRoutines(new ExcludeAll());
    final Pattern grepTablesPattern =
        Pattern.compile(String.format(".*(?i)%s(?-i).*", args.getTableNameContains()));
    final GrepOptionsBuilder grepOptionsBuilder =
        GrepOptionsBuilder.builder().includeGreppedTables(grepTablesPattern);
    return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
        .withLimitOptions(limitOptionsBuilder.toOptions())
        .withGrepOptions(grepOptionsBuilder.toOptions());
  }

  @Override
  protected String getCommand() {
    return "lint";
  }

  @Override
  protected Function<Catalog, Boolean> getResultsChecker(final LintFunctionParameters args) {
    return catalog -> !catalog.getTables().isEmpty();
  }
}
