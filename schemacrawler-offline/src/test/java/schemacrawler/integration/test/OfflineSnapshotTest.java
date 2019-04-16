/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.integration.test;


import static java.nio.file.Files.size;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.FileHasContent.*;
import static schemacrawler.test.utility.TestUtility.flattenCommandlineArgs;
import static schemacrawler.utility.SchemaCrawlerUtility.getCatalog;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import schemacrawler.Main;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.*;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.integration.serialization.JavaSerializedCatalog;
import schemacrawler.tools.offline.OfflineDatabaseConnector;
import schemacrawler.tools.offline.jdbc.OfflineConnection;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;
import sf.util.IOUtility;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
public class OfflineSnapshotTest
{

  private static final String OFFLINE_EXECUTABLE_OUTPUT = "offline_executable_output/";

  private Path serializedCatalogFile;

  @Test
  public void offlineSnapshotCommandLine()
    throws Exception
  {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout)
    {
      final Map<String, String> argsMap = new HashMap<>();
      argsMap.put("-server", "offline");
      argsMap.put("-database", serializedCatalogFile.toString());

      argsMap.put("-no-info", Boolean.FALSE.toString());
      argsMap.put("-info-level", "maximum");
      argsMap.put("-routines", ".*");
      argsMap.put("-command", "details");
      argsMap.put("-output-format", TextOutputFormat.text.getFormat());
      argsMap.put("-output-file", out.toString());

      Main.main(flattenCommandlineArgs(argsMap));
    }
    assertThat(outputOf(testout),
               hasSameContentAs(classpathResource(
                 OFFLINE_EXECUTABLE_OUTPUT + "details.txt")));

  }

  @Test
  public void offlineSnapshotCommandLineWithFilters()
    throws Exception
  {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout)
    {
      final Map<String, String> argsMap = new HashMap<>();
      argsMap.put("-server", "offline");
      argsMap.put("-database", serializedCatalogFile.toString());

      argsMap.put("-no-info", "true");
      argsMap.put("-info-level", "maximum");
      argsMap.put("-command", "details");
      argsMap.put("-output-format", TextOutputFormat.text.getFormat());
      argsMap.put("-routines", "");
      argsMap.put("-tables", ".*SALES");
      argsMap.put("-output-file", out.toString());

      Main.main(flattenCommandlineArgs(argsMap));
    }
    assertThat(outputOf(testout),
               hasSameContentAs(classpathResource(
                 OFFLINE_EXECUTABLE_OUTPUT + "offlineWithFilters.txt")));
  }

  @Test
  public void offlineSnapshotCommandLineWithSchemaFilters()
    throws Exception
  {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout)
    {
      final Map<String, String> argsMap = new HashMap<>();
      argsMap.put("-server", "offline");
      argsMap.put("-database", serializedCatalogFile.toString());

      argsMap.put("-no-info", "true");
      argsMap.put("-info-level", "maximum");
      argsMap.put("-routines", ".*");
      argsMap.put("-command", "list");
      argsMap.put("-output-format", TextOutputFormat.text.getFormat());
      argsMap.put("-schemas", "PUBLIC.BOOKS");
      argsMap.put("-output-file", out.toString());

      final List<String> argsList = new ArrayList<>();
      for (final Map.Entry<String, String> arg : argsMap.entrySet())
      {
        argsList.add(String.format("-%s=%s", arg.getKey(), arg.getValue()));
      }

      Main.main(flattenCommandlineArgs(argsMap));
    }
    assertThat(outputOf(testout),
               hasSameContentAs(classpathResource(
                 OFFLINE_EXECUTABLE_OUTPUT + "offlineWithSchemaFilters.txt")));
  }

  @Test
  public void offlineSnapshotExecutable()
    throws Exception
  {

    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
      .builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum())
      .includeAllRoutines();
    final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder
      .toOptions();

    final SchemaTextOptionsBuilder schemaTextOptionsBuilder = SchemaTextOptionsBuilder
      .builder();
    schemaTextOptionsBuilder.noInfo(false);

    final Connection connection = new OfflineConnection(serializedCatalogFile);

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(
      "details");
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(schemaTextOptionsBuilder.toConfig());
    executable.setConnection(connection);

    executeExecutable(executable, OFFLINE_EXECUTABLE_OUTPUT + "details.txt");
  }

  @BeforeEach
  public void serializeCatalog(final Connection connection)
    throws SchemaCrawlerException, IOException
  {

    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
      .builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum())
      .includeAllRoutines();
    final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder
      .toOptions();

    final Catalog catalog = getCatalog(connection, schemaCrawlerOptions);
    assertThat("Could not obtain catalog", catalog, notNullValue());
    assertThat("Could not find any schemas",
               catalog.getSchemas(),
               not(empty()));

    final Schema schema = catalog.lookupSchema("PUBLIC.BOOKS").orElse(null);
    assertThat("Could not obtain schema", schema, notNullValue());
    assertThat("Unexpected number of tables in the schema",
               catalog.getTables(schema),
               hasSize(10));

    serializedCatalogFile = IOUtility
      .createTempFilePath("schemacrawler", "ser");
    final JavaSerializedCatalog serializedCatalog = new JavaSerializedCatalog(
      catalog);
    serializedCatalog
      .save(new FileOutputStream(serializedCatalogFile.toFile()));
    assertThat("Database was not serialized",
               size(serializedCatalogFile),
               greaterThan(0L));

  }

  private void executeExecutable(final SchemaCrawlerExecutable executable,
                                 final String referenceFileName)
    throws Exception
  {
    final OfflineConnection connection = new OfflineConnection(
      serializedCatalogFile);

    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder = SchemaRetrievalOptionsBuilder
      .builder();
    schemaRetrievalOptionsBuilder
      .withDatabaseServerType(OfflineDatabaseConnector.DB_SERVER_TYPE);

    executable
      .setSchemaRetrievalOptions(schemaRetrievalOptionsBuilder.toOptions());

    assertThat(outputOf(executableExecution(connection, executable)),
               hasSameContentAndTypeAs(classpathResource(referenceFileName),
                                       TextOutputFormat.text.getFormat()));
  }

}
