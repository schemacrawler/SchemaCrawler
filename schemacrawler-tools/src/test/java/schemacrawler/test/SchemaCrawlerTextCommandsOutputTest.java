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


import org.custommonkey.xmlunit.XMLUnit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.operation.Operation;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import schemacrawler.utility.TestDatabase;
import sf.util.TestUtility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;

public class SchemaCrawlerTextCommandsOutputTest {

  private static class LocalEntityResolver
    implements EntityResolver {

    public InputSource resolveEntity(final String publicId,
                                     final String systemId)
      throws SAXException, IOException {
      final String localResource = "/xhtml1"
        + systemId.substring(systemId
        .lastIndexOf('/'));
      final InputStream entityStream = LocalEntityResolver.class
        .getResourceAsStream(localResource);
      if (entityStream == null) {
        throw new IOException("Could not load " + localResource);
      }
      return new InputSource(entityStream);
    }

  }

  private static TestDatabase testUtility = new TestDatabase();

  @AfterClass
  public static void afterAllTests() {
    testUtility.shutdownDatabase();
  }

  @BeforeClass
  public static void beforeAllTests()
    throws Exception {
    TestDatabase.initializeApplicationLogging();
    testUtility.createMemoryDatabase();
    XMLUnit.setControlEntityResolver(new LocalEntityResolver());
  }

  @Test
  public void countOutput()
    throws Exception {
    textOutputTest(Operation.count.name(), new Config());
  }

  @Test
  public void dumpOutput()
    throws Exception {
    textOutputTest(Operation.dump.name(), new Config());
  }

  @Test
  public void queryOutput()
    throws Exception {
    final String queryCommand = "all_tables";
    final Config config = new Config();
    config.put(queryCommand, "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES");

    textOutputTest(queryCommand, config);
  }

  @Test
  public void queryOverOutput()
    throws Exception {
    final String queryCommand = "dump_tables";
    final Config config = new Config();
    config.put(queryCommand,
               "SELECT ${columns} FROM ${table} ORDER BY ${columns}");

    textOutputTest(queryCommand, config);
  }

  @Test
  public void schemaOutput()
    throws Exception {
    textOutputTest(SchemaTextDetailType.list_objects.name(), new Config());
  }

  private void textOutputTest(final String command, final Config config)
    throws Exception {
    final String referenceFile = command + ".txt";
    final File testOutputFile = File.createTempFile("schemacrawler.test.",
                                                    "." + referenceFile);

    final OutputOptions outputOptions = new OutputOptions(OutputFormat.text,
                                                          testOutputFile
                                                            .getAbsolutePath());

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
    executable.setAdditionalConfiguration(config);
    executable.setOutputOptions(outputOptions);
    executable.execute(testUtility.getConnection());

    final List<String> failures = new ArrayList<String>();
    TestUtility.compareOutput("command_output/" + referenceFile,
                              testOutputFile,
                              failures);
    if (failures.size() > 0) {
      fail(failures.toString());
    }
  }

}
