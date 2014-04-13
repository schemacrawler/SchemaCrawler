/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2014, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */
package schemacrawler.tools.offline;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import schemacrawler.schema.Database;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.test.utility.BaseExecutableTest;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.tools.integration.serialization.XmlDatabase;
import schemacrawler.tools.options.OutputOptions;

public class OfflineSnapshotTest
  extends BaseExecutableTest
{

  private static final String OFFLINE_EXECUTABLE_OUTPUT = "offline_executable_output/";
  private File serializedDatabaseFile;

  @Test
  public void offlineSnapshotCommandLine()
    throws Exception
  {
    final File testOutputFile = File.createTempFile("schemacrawler."
                                                    + "details" + ".", ".test");
    testOutputFile.delete();

    final OfflineSnapshotCommandLine commandLine = new OfflineSnapshotCommandLine("-inputfile",
                                                                                  serializedDatabaseFile
                                                                                    .getAbsolutePath(),
                                                                                  "-command="
                                                                                      + "details",
                                                                                  "-infolevel=maximum",
                                                                                  "-outputformat="
                                                                                      + "text",
                                                                                  "-outputfile",
                                                                                  testOutputFile
                                                                                    .getAbsolutePath());
    commandLine.execute();

    final List<String> failures = TestUtility
      .compareOutput(OFFLINE_EXECUTABLE_OUTPUT + "details.txt", testOutputFile);
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  @Test
  public void offlineSnapshotExecutable()
    throws Exception
  {

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());

    final File testOutputFile = File.createTempFile("schemacrawler.", ".test");
    testOutputFile.delete();
    final OutputOptions outputOptions = new OutputOptions("text",
                                                          testOutputFile);

    final OfflineSnapshotExecutable executable = new OfflineSnapshotExecutable("details");
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setOutputOptions(outputOptions);
    executable
      .setOfflineSnapshotOptions(new OfflineSnapshotOptions(serializedDatabaseFile));

    executeExecutableAndCheckForOutputFile(executable,
                                           "text",
                                           OFFLINE_EXECUTABLE_OUTPUT
                                               + "details.txt");
  }

  @Before
  public void serializeDatabase()
    throws SchemaCrawlerException, IOException
  {

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());

    final Database database = getDatabase(schemaCrawlerOptions);
    assertNotNull("Could not obtain database", database);
    assertTrue("Could not find any schemas", database.getSchemas().size() > 0);

    final Schema schema = database.getSchema("PUBLIC.BOOKS");
    assertNotNull("Could not obtain schema", schema);
    assertEquals("Unexpected number of tables in the schema", 6, database
      .getTables(schema).size());

    serializedDatabaseFile = File.createTempFile("schemacrawler.", ".ser");

    final XmlDatabase xmlDatabase = new XmlDatabase(database);
    final Writer writer = new FileWriter(serializedDatabaseFile);
    xmlDatabase.save(writer);
    writer.close();
    assertNotSame("Database was not serialized to XML",
                  0,
                  serializedDatabaseFile.length());

  }

}
