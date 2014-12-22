/*
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2014, Sualeh Fatehi.
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
import static sf.util.Utility.UTF8;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.JUnitCore;

import schemacrawler.Main;
import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.integration.graph.GraphOutputFormat;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.TextOutputFormat;

@Ignore
public class SiteSnapshotVariations
  extends BaseDatabaseTest
{

  public static void main(final String[] args)
  {
    JUnitCore.main(SiteSnapshotVariations.class.getCanonicalName());
  }

  @BeforeClass
  public static void setupDirectory()
    throws IOException, URISyntaxException
  {
    final Path codePath = Paths
      .get(SiteSnapshotVariations.class.getProtectionDomain().getCodeSource()
        .getLocation().toURI()).normalize().toAbsolutePath();
    directory = codePath
      .resolve("../../../schemacrawler-site/src/site/resources/snapshot-examples")
      .normalize().toAbsolutePath();
  }

  private static Path directory;

  @Test
  public void snapshots()
    throws Exception
  {
    for (OutputFormat outputFormat: new OutputFormat[] {
        TextOutputFormat.csv,
        TextOutputFormat.html,
        TextOutputFormat.json,
        TextOutputFormat.text,
        GraphOutputFormat.htmlx
    })
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
    configProperties.store(newBufferedWriter(configFile, UTF8), prefix);
    return configFile;
  }

  private void run(final Map<String, String> args,
                   final Map<String, String> config,
                   final Path outputFile)
    throws Exception
  {
    deleteIfExists(outputFile);

    args.put("driver", "org.hsqldb.jdbc.JDBCDriver");
    args.put("url", "jdbc:hsqldb:hsql://localhost/schemacrawler");
    args.put("user", "sa");
    args.put("password", "");
    args.put("command", "details,count,dump");
    args.put("outputfile", outputFile.toString());

    final Config runConfig = new Config();
    final Config informationSchema = Config
      .loadResource("/hsqldb.INFORMATION_SCHEMA.config.properties");
    runConfig.putAll(informationSchema);
    if (config != null)
    {
      runConfig.putAll(config);
    }

    final Path configFile = createConfig(runConfig);
    args.put("g", configFile.toString());

    final List<String> argsList = new ArrayList<>();
    for (final Map.Entry<String, String> arg: args.entrySet())
    {
      argsList.add(String.format("-%s=%s", arg.getKey(), arg.getValue()));
    }

    Main.main(argsList.toArray(new String[argsList.size()]));
    System.out.println(outputFile.toString());
  }

}
