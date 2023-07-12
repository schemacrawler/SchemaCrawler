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
import static schemacrawler.tools.command.chatgpt.functions.TableDecriptionFunctionParameters.TableDescriptionScope.COLUMNS;
import static schemacrawler.tools.command.chatgpt.functions.TableDecriptionFunctionParameters.TableDescriptionScope.DEFAULT;
import static schemacrawler.tools.command.chatgpt.functions.TableDecriptionFunctionParameters.TableDescriptionScope.FOREIGN_KEYS;
import static schemacrawler.tools.command.chatgpt.functions.TableDecriptionFunctionParameters.TableDescriptionScope.INDEXES;
import static schemacrawler.tools.command.chatgpt.functions.TableDecriptionFunctionParameters.TableDescriptionScope.PRIMARY_KEY;
import static schemacrawler.tools.command.chatgpt.functions.TableDecriptionFunctionParameters.TableDescriptionScope.TRIGGERS;
import static schemacrawler.tools.offline.jdbc.OfflineConnectionUtility.newOfflineDatabaseConnectionSource;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;
import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.schemacrawler.GrepOptionsBuilder;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.tools.command.chatgpt.functions.TableDecriptionFunctionParameters.TableDescriptionScope;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.formatter.serialize.JavaSerializedCatalog;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.IOUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

public final class TableDecriptionFunctionDefinition
    extends AbstractFunctionDefinition<TableDecriptionFunctionParameters> {

  public TableDecriptionFunctionDefinition() {
    super(
        "describe-tables",
        "Gets the details and description of database tables, including columns, foreign keys, indexes and triggers.",
        TableDecriptionFunctionParameters.class);
  }

  @Override
  public Function<TableDecriptionFunctionParameters, FunctionReturn> getExecutor() {
    return args -> {
      final SchemaCrawlerExecutable executable = createExecutable(args);
      return new TableDescriptionFunctionReturn(executable);
    };
  }

  protected Config createConfig(final TableDecriptionFunctionParameters args) {
    final TableDescriptionScope scope = args.getDescriptionScope();
    final SchemaTextOptionsBuilder schemaTextOptionsBuilder = SchemaTextOptionsBuilder.builder();
    if (scope != DEFAULT) {
      if (scope != COLUMNS) {
        schemaTextOptionsBuilder.noTableColumns();
      }
      if (scope != PRIMARY_KEY) {
        schemaTextOptionsBuilder.noPrimaryKeys();
      }
      if (scope != FOREIGN_KEYS) {
        schemaTextOptionsBuilder.noForeignKeys();
        schemaTextOptionsBuilder.noWeakAssociations();
      }
      if (scope != INDEXES) {
        schemaTextOptionsBuilder.noIndexes();
      }
      if (scope != TRIGGERS) {
        schemaTextOptionsBuilder.noTriggers();
      }
    }
    schemaTextOptionsBuilder.noTableConstraints().noAlternateKeys().noInfo();
    return schemaTextOptionsBuilder.toConfig();
  }

  private SchemaCrawlerExecutable createExecutable(final TableDecriptionFunctionParameters args) {

    final DatabaseConnectionSource databaseConnectionSource = createOfflineDatasource();

    final LimitOptionsBuilder limitOptionsBuilder =
        LimitOptionsBuilder.builder()
            .includeSynonyms(new ExcludeAll())
            .includeSequences(new ExcludeAll())
            .includeRoutines(new ExcludeAll());
    final Pattern grepTablesPattern =
        Pattern.compile(String.format("(?i).*%s.*", args.getTableNameContains()));
    final GrepOptionsBuilder grepOptionsBuilder =
        GrepOptionsBuilder.builder().includeGreppedTables(grepTablesPattern);
    final SchemaCrawlerOptions options =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withGrepOptions(grepOptionsBuilder.toOptions());

    final Config config = createConfig(args);

    final String command = "schema";

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
