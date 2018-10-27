/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.test;


import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.newBufferedWriter;
import static schemacrawler.test.utility.TestUtility.flattenCommandlineArgs;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import schemacrawler.Main;
import schemacrawler.schemacrawler.Config;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.test.utility.TestName;
import schemacrawler.test.utility.TestOutputStream;
import schemacrawler.test.utility.TestWriter;
import sf.util.IOUtility;

public class CommandLineNegativeTest
  extends BaseDatabaseTest
{

  private static final String COMMAND_LINE_NEGATIVE_OUTPUT = "command_line_negative_output/";

  @Rule
  public TestName testName = new TestName();

  private TestOutputStream out;
  private TestOutputStream err;

  @After
  public void cleanUpStreams()
  {
    System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
    System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
  }

  @Test
  public void commandLine_BadCommand()
    throws Exception
  {
    final Map<String, String> argsMapOverride = new HashMap<>();
    argsMapOverride.put("command", "badcommand");

    run(argsMapOverride, null);
  }

  @Before
  public void setUpStreams()
    throws Exception
  {
    out = new TestOutputStream();
    System.setOut(new PrintStream(out));

    err = new TestOutputStream();
    System.setErr(new PrintStream(err));
  }

  private Path createConfig(final Map<String, String> config)
    throws IOException
  {
    final String prefix = "SchemaCrawler.TestCommandLineConfig";
    final Path configFile = IOUtility.createTempFilePath(prefix, "properties");
    final Properties configProperties = new Properties();
    configProperties.putAll(config);
    configProperties.store(newBufferedWriter(configFile, UTF_8), prefix);
    return configFile;
  }

  private void run(final Map<String, String> argsMapOverride,
                   final Map<String, String> config)
    throws Exception
  {
    try (final TestWriter outFile = new TestWriter("text");)
    {
      final Map<String, String> argsMap = new HashMap<>();
      argsMap.put("url", "jdbc:hsqldb:hsql://localhost/schemacrawler");
      argsMap.put("user", "sa");
      argsMap.put("password", "");
      argsMap.put("noinfo", Boolean.TRUE.toString());
      argsMap.put("schemas", ".*\\.(?!FOR_LINT).*");
      argsMap.put("infolevel", "standard");
      argsMap.put("command", "brief");
      argsMap.put("tables", "");
      argsMap.put("routines", "");
      argsMap.put("outputformat", "text");
      argsMap.put("outputfile", outFile.toString());

      argsMap.putAll(argsMapOverride);

      final Config runConfig = new Config();
      final Config informationSchema = loadHsqldbConfig();
      runConfig.putAll(informationSchema);
      if (config != null)
      {
        runConfig.putAll(config);
      }

      final Path configFile = createConfig(runConfig);
      argsMap.put("g", configFile.toString());

      Main.main(flattenCommandlineArgs(argsMap));

      outFile.assertEmpty();
    }

    out.assertEmpty();
    err.assertEquals(COMMAND_LINE_NEGATIVE_OUTPUT + testName.currentMethodName()
                     + ".stderr.txt");

  }

}
