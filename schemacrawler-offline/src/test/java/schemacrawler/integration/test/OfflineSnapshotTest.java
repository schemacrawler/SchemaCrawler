/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.size;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static schemacrawler.test.utility.TestUtility.flattenCommandlineArgs;

import java.io.IOException;
import java.io.Writer;
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
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.integration.serialization.XmlSerializedCatalog;
import schemacrawler.tools.iosource.CompressedFileOutputResource;
import schemacrawler.tools.offline.OfflineSnapshotExecutable;
import schemacrawler.tools.offline.jdbc.OfflineConnection;
import schemacrawler.tools.options.OutputOptions;
import sf.util.IOUtility;

public class OfflineSnapshotTest
  extends BaseDatabaseTest
{

  private static final String OFFLINE_EXECUTABLE_OUTPUT = "offline_executable_output/";
  private Path serializedDatabaseFile;

  @Test
  public void offlineSnapshotCommandLine()
    throws Exception
  {
    try (final TestWriter out = new TestWriter("text");)
    {
      final Map<String, String> argsMap = new HashMap<>();
      argsMap.put("server", "offline");
      argsMap.put("database", serializedDatabaseFile.toString());

      argsMap.put("infolevel", "maximum");
      argsMap.put("command", "details");
      argsMap.put("outputformat", "text");
      argsMap.put("outputfile", out.toString());

      Main.main(flattenCommandlineArgs(argsMap));

      out.assertEquals(OFFLINE_EXECUTABLE_OUTPUT + "details.txt");
    }
  }

  @Test
  public void offlineSnapshotCommandLineWithFilters()
    throws Exception
  {
    try (final TestWriter out = new TestWriter("text");)
    {
      final Map<String, String> argsMap = new HashMap<>();
      argsMap.put("server", "offline");
      argsMap.put("database", serializedDatabaseFile.toString());

      argsMap.put("noinfo", "true");
      argsMap.put("infolevel", "maximum");
      argsMap.put("command", "details");
      argsMap.put("outputformat", "text");
      argsMap.put("routines", "");
      argsMap.put("tables", ".*SALES");
      argsMap.put("outputfile", out.toString());

      Main.main(flattenCommandlineArgs(argsMap));

      out.assertEquals(OFFLINE_EXECUTABLE_OUTPUT + "offlineWithFilters.txt");
    }
  }

  @Test
  public void offlineSnapshotCommandLineWithSchemaFilters()
    throws Exception
  {
    try (final TestWriter out = new TestWriter("text");)
    {
      final Map<String, String> argsMap = new HashMap<>();
      argsMap.put("server", "offline");
      argsMap.put("database", serializedDatabaseFile.toString());

      argsMap.put("noinfo", "true");
      argsMap.put("infolevel", "maximum");
      argsMap.put("command", "list");
      argsMap.put("outputformat", "text");
      argsMap.put("schemas", "PUBLIC.BOOKS");
      argsMap.put("outputfile", out.toString());

      final List<String> argsList = new ArrayList<>();
      for (final Map.Entry<String, String> arg: argsMap.entrySet())
      {
        argsList.add(String.format("-%s=%s", arg.getKey(), arg.getValue()));
      }

      Main.main(flattenCommandlineArgs(argsMap));

      out.assertEquals(OFFLINE_EXECUTABLE_OUTPUT
                       + "offlineWithSchemaFilters.txt");
    }
  }

  @Test
  public void offlineSnapshotExecutable()
    throws Exception
  {

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());

    final OutputOptions inputOptions = new OutputOptions();
    inputOptions.setCompressedInputFile(serializedDatabaseFile);

    final OfflineSnapshotExecutable executable = new OfflineSnapshotExecutable("details");
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setInputOptions(inputOptions);

    executeExecutable(executable,
                      "text",
                      OFFLINE_EXECUTABLE_OUTPUT + "details.txt");
  }

  @Before
  public void serializeCatalog()
    throws SchemaCrawlerException, IOException
  {

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());

    final Catalog catalog = getCatalog(schemaCrawlerOptions);
    assertNotNull("Could not obtain catalog", catalog);
    assertTrue("Could not find any schemas", catalog.getSchemas().size() > 0);

    final Schema schema = catalog.lookupSchema("PUBLIC.BOOKS").orElse(null);
    assertNotNull("Could not obtain schema", schema);
    assertEquals("Unexpected number of tables in the schema",
                 6,
                 catalog.getTables(schema).size());

    serializedDatabaseFile = IOUtility.createTempFilePath("schemacrawler",
                                                          "ser");

    final XmlSerializedCatalog xmlDatabase = new XmlSerializedCatalog(catalog);
    final Writer writer = new CompressedFileOutputResource(serializedDatabaseFile,
                                                           "schemacrawler.data")
                                                             .openNewOutputWriter(UTF_8,
                                                                                  false);
    xmlDatabase.save(writer);
    writer.close();
    assertNotSame("Database was not serialized to XML",
                  0,
                  size(serializedDatabaseFile));

  }

  protected void executeExecutable(final Executable executable,
                                   final String outputFormatValue,
                                   final String referenceFileName)
    throws Exception
  {
    try (final TestWriter out = new TestWriter(outputFormatValue);)
    {
      final OutputOptions outputOptions = new OutputOptions(outputFormatValue,
                                                            out);

      executable.setOutputOptions(outputOptions);
      executable.execute(new OfflineConnection(serializedDatabaseFile));

      out.assertEquals(referenceFileName);
    }
  }

}
