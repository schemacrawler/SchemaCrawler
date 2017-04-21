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
package schemacrawler.test;


import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.newBufferedWriter;
import static org.junit.Assert.fail;
import static schemacrawler.test.utility.TestUtility.flattenCommandlineArgs;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;

import schemacrawler.Main;
import schemacrawler.schemacrawler.Config;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.tools.integration.graph.GraphOutputFormat;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.TextOutputFormat;
import sf.util.IOUtility;

public class TitleTest
  extends BaseDatabaseTest
{

  private static final String TITLE_OUTPUT = "title_output/";

  @Test
  public void commandLineWithTitle()
    throws Exception
  {
    final OutputFormat[] outputFormats = new OutputFormat[] {
                                                              TextOutputFormat.text,
                                                              TextOutputFormat.html,
                                                              TextOutputFormat.json,
                                                              GraphOutputFormat.scdot };

    final Map<String, String> args = new HashMap<>();
    args.put("title", "Database Design for Books and Publishers");
    args.put("routines", "");
    // Testing no sequences, synonyms

    final List<String> failures = new ArrayList<>();
    for (final String command: new String[] { "schema", "list" })
    {
      for (final OutputFormat outputFormat: outputFormats)
      {
        run(args,
            null,
            command,
            outputFormat,
            "commandLineWithTitle_" + command + "." + outputFormat.getFormat(),
            failures);
      }
    }

    if (!failures.isEmpty())
    {
      fail(failures.toString());
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
                   final String referenceFile,
                   final List<String> allFailures)
    throws Exception
  {

    try (final TestWriter out = new TestWriter(outputFormat.getFormat());)
    {
      argsMap.put("url", "jdbc:hsqldb:hsql://localhost/schemacrawler");
      argsMap.put("user", "sa");
      argsMap.put("password", "");
      argsMap.put("infolevel", "maximum");
      argsMap.put("schemas", ".*\\.(?!FOR_LINT).*");
      argsMap.put("sorttables", "true");
      argsMap.put("command", command);
      argsMap.put("outputformat", outputFormat.getFormat());
      argsMap.put("outputfile", out.toString());

      final Config runConfig = new Config();
      final Config informationSchema = Config
        .loadResource("/hsqldb.INFORMATION_SCHEMA.config.properties");
      runConfig.putAll(informationSchema);
      if (config != null)
      {
        runConfig.putAll(config);
      }

      final Path configFile = createConfig(runConfig);
      argsMap.put("g", configFile.toString());

      Main.main(flattenCommandlineArgs(argsMap));

      final List<String> failures = out
        .collectFailures(TITLE_OUTPUT + referenceFile);
      allFailures.addAll(failures);
    }
  }

}
