/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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
package schemacrawler.integration.test;


import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import schemacrawler.test.TestUtility;
import schemacrawler.tools.integration.spring.Main;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.utility.TestDatabase;

public class SpringIntegrationCommandLineTest
{

  private static TestDatabase testDatabase = new TestDatabase();

  @AfterClass
  public static void afterAllTests()
  {
    testDatabase.shutdownDatabase();
  }

  @BeforeClass
  public static void beforeAllTests()
    throws Exception
  {
    TestDatabase.initializeApplicationLogging();
    testDatabase.startDatabase(true);
  }

  @Test
  public void springCommandLine()
    throws Exception
  {
    final List<String> failures = new ArrayList<String>();

    final File contextFile = TestUtility.copyResourceToTempFile("/context.xml");

    final String executableName = "executableForSchema";
    final String referenceFile = executableName + ".txt";
    final File testOutputFile = new File("scOutput.txt");
    testOutputFile.delete();

    final OutputFormat outputFormat = OutputFormat.text;
    Main.main(new String[] {
        "-c", contextFile.getAbsolutePath(), "-x=" + executableName,
    });

    failures.addAll(TestUtility.compareOutput(referenceFile,
                                              testOutputFile,
                                              outputFormat));
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }
}
