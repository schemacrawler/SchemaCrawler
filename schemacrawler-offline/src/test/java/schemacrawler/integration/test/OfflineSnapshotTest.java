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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.fileResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAndTypeAs;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.TestUtility.flattenCommandlineArgs;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import schemacrawler.Main;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.integration.serialization.XmlSerializedCatalog;
import schemacrawler.tools.offline.OfflineDatabaseConnector;
import schemacrawler.tools.offline.jdbc.OfflineConnection;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;
import sf.util.IOUtility;

public class OfflineSnapshotTest
  extends BaseDatabaseTest
{

  private static final String OFFLINE_EXECUTABLE_OUTPUT = "offline_executable_output/";
  private Path serializedCatalogFile;

  @Test
  public void offlineSnapshotCommandLine()
    throws Exception
  {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout;)
    {
      final Map<String, String> argsMap = new HashMap<>();
      argsMap.put("server", "offline");
      argsMap.put("database", serializedCatalogFile.toString());

      argsMap.put("noinfo", Boolean.FALSE.toString());
      argsMap.put("infolevel", "maximum");
      argsMap.put("routines", ".*");
      argsMap.put("command", "details");
      argsMap.put("outputformat", TextOutputFormat.text.getFormat());
      argsMap.put("outputfile", out.toString());

      Main.main(flattenCommandlineArgs(argsMap));
    }
    assertThat(fileResource(testout),
               hasSameContentAs(classpathResource(OFFLINE_EXECUTABLE_OUTPUT
                                                  + "details.txt")));

  }

  @Test
  public void offlineSnapshotCommandLineWithFilters()
    throws Exception
  {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout;)
    {
      final Map<String, String> argsMap = new HashMap<>();
      argsMap.put("server", "offline");
      argsMap.put("database", serializedCatalogFile.toString());

      argsMap.put("noinfo", "true");
      argsMap.put("infolevel", "maximum");
      argsMap.put("routines", ".*");
      argsMap.put("command", "details");
      argsMap.put("outputformat", TextOutputFormat.text.getFormat());
      argsMap.put("routines", "");
      argsMap.put("tables", ".*SALES");
      argsMap.put("outputfile", out.toString());

      Main.main(flattenCommandlineArgs(argsMap));
    }
    assertThat(fileResource(testout),
               hasSameContentAs(classpathResource(OFFLINE_EXECUTABLE_OUTPUT
                                                  + "offlineWithFilters.txt")));
  }

  @Test
  public void offlineSnapshotCommandLineWithSchemaFilters()
    throws Exception
  {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout;)
    {
      final Map<String, String> argsMap = new HashMap<>();
      argsMap.put("server", "offline");
      argsMap.put("database", serializedCatalogFile.toString());

      argsMap.put("noinfo", "true");
      argsMap.put("infolevel", "maximum");
      argsMap.put("routines", ".*");
      argsMap.put("command", "list");
      argsMap.put("outputformat", TextOutputFormat.text.getFormat());
      argsMap.put("schemas", "PUBLIC.BOOKS");
      argsMap.put("outputfile", out.toString());

      final List<String> argsList = new ArrayList<>();
      for (final Map.Entry<String, String> arg: argsMap.entrySet())
      {
        argsList.add(String.format("-%s=%s", arg.getKey(), arg.getValue()));
      }

      Main.main(flattenCommandlineArgs(argsMap));
    }
    assertThat(fileResource(testout),
               hasSameContentAs(classpathResource(OFFLINE_EXECUTABLE_OUTPUT
                                                  + "offlineWithSchemaFilters.txt")));
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

    final OutputOptions inputOptions = OutputOptionsBuilder.builder()
      .withCompressedInputFile(serializedCatalogFile).toOptions();

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("details");
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setOutputOptions(inputOptions);
    executable.setAdditionalConfiguration(schemaTextOptionsBuilder.toConfig());

    executeExecutable(executable, OFFLINE_EXECUTABLE_OUTPUT + "details.txt");
  }

  @Before
  public void serializeCatalog()
    throws SchemaCrawlerException, IOException
  {

    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
      .builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum())
      .includeAllRoutines();
    final SchemaCrawlerOptions schemaCrawlerOptions = schemaCrawlerOptionsBuilder
      .toOptions();

    final Catalog catalog = getCatalog(schemaCrawlerOptions);
    assertNotNull("Could not obtain catalog", catalog);
    assertTrue("Could not find any schemas", catalog.getSchemas().size() > 0);

    final Schema schema = catalog.lookupSchema("PUBLIC.BOOKS").orElse(null);
    assertNotNull("Could not obtain schema", schema);
    assertEquals("Unexpected number of tables in the schema",
                 10,
                 catalog.getTables(schema).size());

    serializedCatalogFile = IOUtility.createTempFilePath("schemacrawler",
                                                         "ser");
    final XmlSerializedCatalog serializedCatalog = new XmlSerializedCatalog(catalog);
    serializedCatalog
      .save(new FileOutputStream(serializedCatalogFile.toFile()));
    assertNotSame("Database was not serialized",
                  0,
                  size(serializedCatalogFile));

  }

  protected void executeExecutable(final SchemaCrawlerExecutable executable,
                                   final String referenceFileName)
    throws Exception
  {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout;)
    {
      final OutputOptions outputOptions = OutputOptionsBuilder
        .newOutputOptions(TextOutputFormat.text, out);
      final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder = SchemaRetrievalOptionsBuilder
        .builder();
      schemaRetrievalOptionsBuilder
        .withDatabaseServerType(OfflineDatabaseConnector.DB_SERVER_TYPE);

      executable.setOutputOptions(outputOptions);
      executable.setConnection(new OfflineConnection(serializedCatalogFile));
      executable
        .setSchemaRetrievalOptions(schemaRetrievalOptionsBuilder.toOptions());
      executable.execute();
    }
    assertThat(fileResource(testout),
               hasSameContentAndTypeAs(classpathResource(referenceFileName),
                                       TextOutputFormat.text.getFormat()));
  }

}
