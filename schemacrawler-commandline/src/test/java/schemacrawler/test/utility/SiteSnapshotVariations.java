/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
 * This library is free software; you can redistribute it and/or modify it under
 * the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */
package schemacrawler.test.utility;


import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.deleteIfExists;
import static java.nio.file.Files.newBufferedWriter;
import static us.fatehi.commandlineparser.CommandLineUtility.flattenCommandlineArgs;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import schemacrawler.Main;
import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.integration.graph.GraphOutputFormat;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.TextOutputFormat;

public class SiteSnapshotVariations
  extends BaseDatabaseTest
{

  private static Path directory;

  @BeforeClass
  public static void setupDirectory()
    throws IOException, URISyntaxException
  {
    final Path codePath = Paths.get(SiteSnapshotVariations.class
      .getProtectionDomain().getCodeSource().getLocation().toURI()).normalize()
      .toAbsolutePath();
    directory = codePath
      .resolve("../../../schemacrawler-site/src/site/resources/snapshot-examples")
      .normalize().toAbsolutePath();
  }

  @Rule
  public TestRule rule = new SiteVariationsGenerationRule();

  @Test
  public void snapshots()
    throws Exception
  {
    for (final OutputFormat outputFormat: new OutputFormat[] {
                                                               TextOutputFormat.csv,
                                                               TextOutputFormat.html,
                                                               TextOutputFormat.json,
                                                               TextOutputFormat.text,
                                                               GraphOutputFormat.htmlx })
    {
      final String format = outputFormat.getFormat();
      final Map<String, String> args = new HashMap<String, String>();
      args.put("infolevel", "maximum");
      args.put("outputformat", format);

      final Map<String, String> config = new HashMap<>();

      run(args, config, directory.resolve("snapshot." + format));
    }
  }

  private Path createConfig(final Map<String, String> config)
    throws IOException
  {
    final String prefix = SiteSnapshotVariations.class.getName();
    final Path configFile = createTempFile(prefix, "properties");
    final Properties configProperties = new Properties();
    configProperties.putAll(config);
    configProperties
      .store(newBufferedWriter(configFile, StandardCharsets.UTF_8), prefix);
    return configFile;
  }

  private void run(final Map<String, String> argsMap,
                   final Map<String, String> config,
                   final Path outputFile)
                     throws Exception
  {
    deleteIfExists(outputFile);

    argsMap.put("url", "jdbc:hsqldb:hsql://localhost/schemacrawler");
    argsMap.put("user", "sa");
    argsMap.put("password", "");
    argsMap.put("title", "Details of Example Database");
    argsMap.put("command", "details,count,dump");
    argsMap.put("outputfile", outputFile.toString());

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
    System.out.println(outputFile.toString());
  }

}
