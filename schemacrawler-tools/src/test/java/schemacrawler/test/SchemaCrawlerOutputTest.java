/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
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

package schemacrawler.test;


import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.DatabaseConnectionOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.tools.commandline.InfoLevel;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.operation.Operation;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import schemacrawler.utility.TestDatabase;
import sf.util.TestUtility;

public class SchemaCrawlerOutputTest
{

  private static class LocalEntityResolver
    implements EntityResolver
  {

    public InputSource resolveEntity(final String publicId,
                                     final String systemId)
      throws SAXException, IOException
    {
      final String localResource = "/xhtml1"
                                   + systemId.substring(systemId
                                     .lastIndexOf('/'));
      final InputStream entityStream = LocalEntityResolver.class
        .getResourceAsStream(localResource);
      if (entityStream == null)
      {
        throw new IOException("Could not load " + localResource);
      }
      return new InputSource(entityStream);
    }

  }

  private static TestDatabase testUtility = new TestDatabase();

  @AfterClass
  public static void afterAllTests()
  {
    testUtility.shutdownDatabase();
  }

  @BeforeClass
  public static void beforeAllTests()
    throws Exception
  {
    TestDatabase.initializeApplicationLogging();
    testUtility.createMemoryDatabase();
    XMLUnit.setControlEntityResolver(new LocalEntityResolver());
  }

  @Test
  public void compareCompositeOutput()
    throws Exception
  {
    final String queryCommand1 = "all_tables";
    final Config queriesConfig = new Config();
    queriesConfig.put(queryCommand1,
                      "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES");
    final String queryCommand2 = "dump_tables";
    queriesConfig.put(queryCommand2,
                      "SELECT ${columns} FROM ${table} ORDER BY ${columns}");

    final String[] commands = new String[] {
        SchemaTextDetailType.verbose_schema + "," + Operation.count + ","
            + Operation.dump,
        SchemaTextDetailType.list_objects + "," + Operation.count,
        queryCommand1 + "," + queryCommand2 + "," + Operation.count + ","
            + SchemaTextDetailType.list_objects,
    };

    final List<String> failures = new ArrayList<String>();
    for (final OutputFormat outputFormat: OutputFormat.values())
    {
      for (final String command: commands)
      {
        final String referenceFile = command + "." + outputFormat.name();

        final File testOutputFile = File.createTempFile("schemacrawler."
                                                            + referenceFile
                                                            + ".",
                                                        ".test");
        testOutputFile.delete();

        final OutputOptions outputOptions = new OutputOptions(outputFormat,
                                                              testOutputFile
                                                                .getAbsolutePath());
        outputOptions.setNoInfo(false);
        outputOptions.setNoHeader(false);
        outputOptions.setNoFooter(false);

        final Config config = Config.load(SchemaCrawlerOutputTest.class
          .getResourceAsStream("/hsqldb.INFORMATION_SCHEMA.config.properties"));
        final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions(config);
        schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());

        final DatabaseConnectionOptions connectionOptions = testUtility
          .getDatabaseConnectionOptions();

        final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
        executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
        executable.setOutputOptions(outputOptions);
        executable.setAdditionalConfiguration(queriesConfig);
        executable.execute(connectionOptions.createConnection());

        TestUtility.compareOutput("composite_output/" + referenceFile,
                                  testOutputFile,
                                  outputFormat,
                                  failures);
      }
    }

    if (failures.size() > 0)
    {
      fail(failures.toString());
    }

  }

  @Test
  public void compareInfoLevelOutput()
    throws Exception
  {

    final List<String> failures = new ArrayList<String>();
    for (final InfoLevel infoLevel: InfoLevel.values())
    {
      for (final SchemaTextDetailType schemaTextDetailType: SchemaTextDetailType
        .values())
      {
        final String referenceFile = schemaTextDetailType + "_" + infoLevel
                                     + ".txt";

        final File testOutputFile = File.createTempFile("schemacrawler."
                                                            + referenceFile
                                                            + ".",
                                                        ".test");
        testOutputFile.delete();

        final OutputOptions outputOptions = new OutputOptions(OutputFormat.text,
                                                              testOutputFile
                                                                .getAbsolutePath());
        outputOptions.setNoInfo(false);
        outputOptions.setNoHeader(false);
        outputOptions.setNoFooter(false);

        final Config config = Config.load(SchemaCrawlerOutputTest.class
          .getResourceAsStream("/hsqldb.INFORMATION_SCHEMA.config.properties"));
        final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions(config);
        schemaCrawlerOptions.setSchemaInfoLevel(infoLevel.getSchemaInfoLevel());

        final DatabaseConnectionOptions connectionOptions = testUtility
          .getDatabaseConnectionOptions();

        final Executable executable = new SchemaCrawlerExecutable(schemaTextDetailType
          .name());
        executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
        executable.setOutputOptions(outputOptions);
        executable.execute(connectionOptions.createConnection());

        TestUtility.compareOutput("info_level_output/" + referenceFile,
                                  testOutputFile,
                                  outputOptions.getOutputFormat(),
                                  failures);
      }
    }

    if (failures.size() > 0)
    {
      fail(failures.toString());
    }

  }

}
