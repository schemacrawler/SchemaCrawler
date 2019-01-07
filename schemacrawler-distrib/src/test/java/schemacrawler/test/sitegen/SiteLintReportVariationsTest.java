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
package schemacrawler.test.sitegen;


import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.deleteIfExists;
import static java.nio.file.Files.newBufferedWriter;
import static schemacrawler.test.utility.TestUtility.flattenCommandlineArgs;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import schemacrawler.Main;
import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.TextOutputFormat;
import sf.util.IOUtility;

public class SiteLintReportVariationsTest
  extends
  BaseSiteVariationsTest
{

  private Path directory;

  @BeforeEach
  public void _setupDirectory(final TestInfo testInfo)
    throws IOException,
    URISyntaxException
  {
    if (directory != null)
    {
      return;
    }

    directory = resolveTargetFromRootPath(testInfo, "lint-report-examples");
  }

  @Test
  public void lint_reports()
    throws Exception
  {
    for (final OutputFormat outputFormat: new OutputFormat[] {
                                                               TextOutputFormat.html,
                                                               TextOutputFormat.json,
                                                               TextOutputFormat.text,
    })
    {
      final String outputFormatValue = outputFormat.getFormat();
      final String extension;
      if (outputFormat == TextOutputFormat.text)
      {
        extension = "txt";
      }
      else
      {
        extension = outputFormatValue;
      }
      final Map<String, String> args = new HashMap<>();
      args.put("infolevel", "maximum");
      args.put("outputformat", outputFormatValue);

      final Map<String, String> config = new HashMap<>();

      run(args, config, directory.resolve("lint_report." + extension));
    }
  }

  private Path createConfig(final Map<String, String> config)
    throws IOException
  {
    final String prefix = SiteLintReportVariationsTest.class.getName();
    final Path configFile = IOUtility.createTempFilePath(prefix, "properties");
    final Properties configProperties = new Properties();
    configProperties.putAll(config);
    configProperties.store(newBufferedWriter(configFile, UTF_8), prefix);
    return configFile;
  }

  private void run(final Map<String, String> argsMap,
                   final Map<String, String> config, final Path outputFile)
    throws Exception
  {
    deleteIfExists(outputFile);

    argsMap.put("url", "jdbc:hsqldb:hsql://localhost/schemacrawler");
    argsMap.put("user", "sa");
    argsMap.put("password", "");
    argsMap.put("title", "Lint Report of Example Database");
    argsMap.put("command", "lint");
    argsMap.put("outputfile", outputFile.toString());

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

}
