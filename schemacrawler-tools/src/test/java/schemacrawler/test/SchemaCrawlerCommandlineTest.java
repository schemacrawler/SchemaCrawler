/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.custommonkey.xmlunit.Validator;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.OutputFormat;
import schemacrawler.tools.OutputOptions;
import schemacrawler.tools.main.Commands;
import schemacrawler.tools.main.SchemaCrawlerCommandLine;
import schemacrawler.utility.TestDatabase;
import sf.util.TestUtility;

public class SchemaCrawlerCommandlineTest
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
    final String[][] commandSets = new String[][] {
        {
            "maximum_schema", "count", "dump",
        }, {
            "brief_schema", "count",
        }
    };

    final List<String> failures = new ArrayList<String>();
    for (final OutputFormat outputFormat: OutputFormat.values())
    {
      if (outputFormat == OutputFormat.dot)
      {
        continue;
      }
      for (final String[] commandSet: commandSets)
      {
        final String referenceFile = commandSet[0].toString() + "."
                                     + outputFormat.name();

        final File testOutputFile = File.createTempFile("schemacrawler.test.",
                                                        referenceFile);

        final OutputOptions outputOptions = new OutputOptions(outputFormat,
                                                              testOutputFile
                                                                .getAbsolutePath());
        outputOptions.setNoInfo(false);
        outputOptions.setNoHeader(false);
        outputOptions.setNoFooter(false);

        final Commands commands = new Commands();
        for (final String command: commandSet)
        {
          commands.add(command);
        }

        final SchemaCrawlerCommandLine commandLine = new SchemaCrawlerCommandLine(commands,
                                                                                  new Config(),
                                                                                  testUtility
                                                                                    .getDatabaseConnectionOptions(),
                                                                                  outputOptions);
        commandLine.execute();

        if (outputFormat == OutputFormat.html)
        {
          final Validator validator = new Validator(new FileReader(testOutputFile));
          if (!validator.isValid())
          {
            failures.add(validator.toString());
          }
        }

        TestUtility.compareOutput(referenceFile, testOutputFile, failures);
      }
    }

    if (failures.size() > 0)
    {
      fail(failures.toString());
    }

  }
}
