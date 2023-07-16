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

import static java.nio.file.Files.newOutputStream;
import static schemacrawler.tools.offline.jdbc.OfflineConnectionUtility.newOfflineDatabaseConnectionSource;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.zip.GZIPOutputStream;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.formatter.serialize.JavaSerializedCatalog;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.IOUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

public abstract class AbstractExecutableFunctionDefinition<P extends FunctionParameters>
    extends AbstractFunctionDefinition<P> {

  protected AbstractExecutableFunctionDefinition(
      final String description, final Class<P> parameters) {
    super(description, parameters);
  }

  @Override
  public Function<P, FunctionReturn> getExecutor() {
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

  private SchemaCrawlerExecutable createExecutable(final P args) {

    final DatabaseConnectionSource databaseConnectionSource = createOfflineDatasource();

    final SchemaCrawlerOptions options = createSchemaCrawlerOptions(args);
    final Config config = createAdditionalConfig(args);
    final String command = getCommand();

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
    executable.setSchemaCrawlerOptions(options);
    executable.setAdditionalConfiguration(config);
    executable.setDataSource(databaseConnectionSource);

    return executable;
  }

  private DatabaseConnectionSource createOfflineDatasource() {
    try {
      final Path serializedCatalogFile =
          IOUtility.createTempFilePath("sc_java_serialization", "ser");
      new JavaSerializedCatalog(catalog)
          .save(new GZIPOutputStream(newOutputStream(serializedCatalogFile)));
      return newOfflineDatabaseConnectionSource(serializedCatalogFile);
    } catch (final Exception e) {
      throw new ExecutionRuntimeException("Could not create an offline connection", e);
    }
  }
}
