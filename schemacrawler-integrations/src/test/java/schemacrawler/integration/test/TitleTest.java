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


import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.newBufferedWriter;
import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.DatabaseTestUtility.loadHsqldbConfig;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.fileResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAndTypeAs;
import static schemacrawler.test.utility.TestUtility.clean;
import static schemacrawler.test.utility.TestUtility.flattenCommandlineArgs;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import schemacrawler.Main;
import schemacrawler.schemacrawler.Config;
import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.tools.integration.graph.GraphOutputFormat;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.TextOutputFormat;
import sf.util.IOUtility;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
public class TitleTest
{

  private static final String TITLE_OUTPUT = "title_output/";

  @Test
  public void commandLineWithTitle(final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    clean(TITLE_OUTPUT);

    final OutputFormat[] outputFormats = new OutputFormat[] {
                                                              TextOutputFormat.text,
                                                              TextOutputFormat.html,
                                                              TextOutputFormat.json,
                                                              GraphOutputFormat.scdot };

    final Map<String, String> args = new HashMap<>();
    args.put("title", "Database Design for Books and Publishers");
    args.put("routines", "");
    // Testing no sequences, synonyms

    for (final String command: new String[] { "schema", "list" })
    {
      for (final OutputFormat outputFormat: outputFormats)
      {
        run(args,
            null,
            command,
            outputFormat,
            connectionInfo,
            "commandLineWithTitle_" + command + "." + outputFormat.getFormat());
      }
    }
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

  private void run(final Map<String, String> argsMap,
                   final Map<String, String> config,
                   final String command,
                   final OutputFormat outputFormat,
                   final DatabaseConnectionInfo connectionInfo,
                   final String referenceFile)
    throws Exception
  {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout;)
    {
      argsMap.put("url", connectionInfo.getConnectionUrl());
      argsMap.put("user", "sa");
      argsMap.put("password", "");
      argsMap.put("noinfo", Boolean.FALSE.toString());
      argsMap.put("infolevel", "maximum");
      argsMap.put("schemas", ".*\\.(?!FOR_LINT).*");
      argsMap.put("sorttables", "true");
      argsMap.put("command", command);
      argsMap.put("outputformat", outputFormat.getFormat());
      argsMap.put("outputfile", out.toString());

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
    }
    assertThat(fileResource(testout),
               hasSameContentAndTypeAs(classpathResource(TITLE_OUTPUT
                                                         + referenceFile),
                                       outputFormat.getFormat()));
  }

}
