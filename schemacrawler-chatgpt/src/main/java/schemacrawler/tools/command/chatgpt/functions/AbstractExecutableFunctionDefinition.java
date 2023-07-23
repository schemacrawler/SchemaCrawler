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

import static us.fatehi.utility.Utility.isBlank;
import java.util.Collection;
import java.util.function.Function;
import java.util.regex.Pattern;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.tools.command.chatgpt.FunctionParameters;
import schemacrawler.tools.command.chatgpt.FunctionReturn;
import schemacrawler.tools.command.chatgpt.utility.ConnectionDatabaseConnectionSource;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import schemacrawler.utility.MetaDataUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

public abstract class AbstractExecutableFunctionDefinition<P extends FunctionParameters>
    extends AbstractFunctionDefinition<P> {

  protected AbstractExecutableFunctionDefinition(
      final String description, final Class<P> parameters) {
    super(description, parameters);
  }

  @Override
  public Function<P, FunctionReturn> getExecutor() {
    if (catalog == null) {
      throw new ExecutionRuntimeException("Catalog is not provided");
    }
    return args -> {
      final SchemaCrawlerExecutable executable = createExecutable(args);
      final Function<Catalog, Boolean> resultsChecker = getResultsChecker(args);
      return new ExecutableFunctionReturn(executable, resultsChecker);
    };
  }

  protected abstract Config createAdditionalConfig(final P args);

  protected abstract SchemaCrawlerOptions createSchemaCrawlerOptions(final P args);

  protected abstract String getCommand();

  protected abstract Function<Catalog, Boolean> getResultsChecker(final P args);

  protected Pattern makeNameInclusionPattern(final String name) {
    if (isBlank(name)) {
      return Pattern.compile(".*");
    }
    final boolean hasDefaultSchema = hasDefaultSchema();
    return Pattern.compile(String.format(".*%s(?i)%s(?-i)", hasDefaultSchema ? "" : "\\.", name));
  }

  private SchemaCrawlerExecutable createExecutable(final P args) {

    final SchemaCrawlerOptions options = createSchemaCrawlerOptions(args);
    final Config config = createAdditionalConfig(args);
    final String command = getCommand();

    // Re-filter catalog
    MetaDataUtility.reduceCatalog(catalog, options);

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
    executable.setSchemaCrawlerOptions(options);
    executable.setAdditionalConfiguration(config);
    executable.setCatalog(catalog);
    if (connection != null) {
      final DatabaseConnectionSource databaseConnectionSource =
          new ConnectionDatabaseConnectionSource(connection);
      executable.setDataSource(databaseConnectionSource);
    }

    return executable;
  }

  private boolean hasDefaultSchema() {
    final Collection<Schema> schemas = catalog.getSchemas();
    final int schemaCount = schemas.size();
    for (final Schema schema : schemas) {
      if (isBlank(schema.getFullName()) && schemaCount == 1) {
        return true;
      }
    }
    return false;
  }
}
