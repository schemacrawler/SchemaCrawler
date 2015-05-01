/*
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
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
import static sf.util.commandlineparser.CommandLineUtility.flattenCommandlineArgs;

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

public class SiteGraphVariations
  extends BaseDatabaseTest
{

  private static Path directory;

  @BeforeClass
  public static void setupDirectory()
    throws IOException, URISyntaxException
  {
    final Path codePath = Paths
      .get(SiteGraphVariations.class.getProtectionDomain().getCodeSource()
        .getLocation().toURI()).normalize().toAbsolutePath();
    directory = codePath
      .resolve("../../../schemacrawler-site/src/site/resources/images")
      .normalize().toAbsolutePath();
  }

  @Rule
  public TestRule rule = new SiteVariationsGenerationRule();

  @Rule
  public TestName testName = new TestName();

  @Test
  public void diagram()
    throws Exception
  {
    final Map<String, String> args = new HashMap<String, String>();
    args.put("infolevel", "maximum");

    final Map<String, String> config = new HashMap<>();

    run(args, config, directory.resolve(testName.currentMethodName() + ".png"));
  }

  @Test
  public void diagram_2_portablenames()
    throws Exception
  {
    final Map<String, String> args = new HashMap<String, String>();
    args.put("infolevel", "maximum");
    args.put("portablenames", "true");

    final Map<String, String> config = new HashMap<>();

    run(args, config, directory.resolve(testName.currentMethodName() + ".png"));
  }

  @Test
  public void diagram_3_important_columns()
    throws Exception
  {
    final Map<String, String> args = new HashMap<String, String>();
    args.put("infolevel", "standard");
    args.put("command", "brief");
    args.put("portablenames", "true");

    final Map<String, String> config = new HashMap<>();

    run(args, config, directory.resolve(testName.currentMethodName() + ".png"));
  }

  @Test
  public void diagram_4_ordinals()
    throws Exception
  {
    final Map<String, String> args = new HashMap<String, String>();
    args.put("infolevel", "standard");
    args.put("portablenames", "true");

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.format.show_ordinal_numbers", "true");

    run(args, config, directory.resolve(testName.currentMethodName() + ".png"));
  }

  @Test
  public void diagram_5_alphabetical()
    throws Exception
  {
    final Map<String, String> args = new HashMap<String, String>();
    args.put("infolevel", "standard");
    args.put("portablenames", "true");
    args.put("sortcolumns", "true");

    final Map<String, String> config = new HashMap<>();

    run(args, config, directory.resolve(testName.currentMethodName() + ".png"));
  }

  @Test
  public void diagram_6_grep()
    throws Exception
  {
    final Map<String, String> args = new HashMap<String, String>();
    args.put("infolevel", "maximum");
    args.put("portablenames", "true");
    args.put("grepcolumns", ".*\\.BOOKS\\..*\\.ID");
    args.put("tabletypes", "TABLE");

    final Map<String, String> config = new HashMap<>();

    run(args, config, directory.resolve(testName.currentMethodName() + ".png"));
  }

  @Test
  public void diagram_7_grep_onlymatching()
    throws Exception
  {
    final Map<String, String> args = new HashMap<String, String>();
    args.put("infolevel", "maximum");
    args.put("portablenames", "true");
    args.put("grepcolumns", ".*\\.BOOKS\\..*\\.ID");
    args.put("only-matching", "true");
    args.put("tabletypes", "TABLE");

    final Map<String, String> config = new HashMap<>();

    run(args, config, directory.resolve(testName.currentMethodName() + ".png"));
  }

  @Test
  public void diagram_8_no_cardinality()
    throws Exception
  {
    final Map<String, String> args = new HashMap<String, String>();
    args.put("infolevel", "standard");
    args.put("portablenames", "true");

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.graph.show.primarykey.cardinality", "false");
    config.put("schemacrawler.graph.show.foreignkey.cardinality", "false");

    run(args, config, directory.resolve(testName.currentMethodName() + ".png"));
  }

  private Path createConfig(final Map<String, String> config)
    throws IOException
  {
    final String prefix = SiteGraphVariations.class.getName();
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
    argsMap.put("tables", ".*");
    argsMap.put("routines", "");
    if (!argsMap.containsKey("command"))
    {
      argsMap.put("command", "graph");
    }
    argsMap.put("outputformat", "png");
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
