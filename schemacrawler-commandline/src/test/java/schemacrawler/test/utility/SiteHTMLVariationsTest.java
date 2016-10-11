/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.test.utility;


import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.deleteIfExists;
import static java.nio.file.Files.newBufferedWriter;
import static schemacrawler.test.utility.TestUtility.flattenCommandlineArgs;

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

public class SiteHTMLVariationsTest
  extends BaseDatabaseTest
{

  private static Path directory;

  @BeforeClass
  public static void setupDirectory()
    throws IOException, URISyntaxException
  {
    final Path codePath = Paths.get(SiteHTMLVariationsTest.class
      .getProtectionDomain().getCodeSource().getLocation().toURI()).normalize()
      .toAbsolutePath();
    directory = codePath
      .resolve("../../../schemacrawler-site/src/site/resources/html-examples")
      .normalize().toAbsolutePath();
  }

  @Rule
  public TestRule rule = new CompleteBuildRule();

  @Rule
  public TestName testName = new TestName();

  @Test
  public void html()
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "maximum");

    final Map<String, String> config = new HashMap<>();

    run(args,
        config,
        directory.resolve(testName.currentMethodName() + ".html"));
  }

  @Test
  public void html_2_portablenames()
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "maximum");
    args.put("portablenames", "true");

    final Map<String, String> config = new HashMap<>();

    run(args,
        config,
        directory.resolve(testName.currentMethodName() + ".html"));
  }

  @Test
  public void html_3_important_columns()
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "standard");
    args.put("command", "brief");
    args.put("portablenames", "true");

    final Map<String, String> config = new HashMap<>();

    run(args,
        config,
        directory.resolve(testName.currentMethodName() + ".html"));
  }

  @Test
  public void html_4_ordinals()
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "standard");
    args.put("portablenames", "true");

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.format.show_ordinal_numbers", "true");

    run(args,
        config,
        directory.resolve(testName.currentMethodName() + ".html"));
  }

  @Test
  public void html_5_alphabetical()
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "standard");
    args.put("portablenames", "true");
    args.put("sortcolumns", "true");

    final Map<String, String> config = new HashMap<>();

    run(args,
        config,
        directory.resolve(testName.currentMethodName() + ".html"));
  }

  @Test
  public void html_6_grep()
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "maximum");
    args.put("portablenames", "true");
    args.put("grepcolumns", ".*\\.BOOKS\\..*\\.ID");
    args.put("tabletypes", "TABLE");

    final Map<String, String> config = new HashMap<>();

    run(args,
        config,
        directory.resolve(testName.currentMethodName() + ".html"));
  }

  @Test
  public void html_7_grep_onlymatching()
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "maximum");
    args.put("portablenames", "true");
    args.put("grepcolumns", ".*\\.BOOKS\\..*\\.ID");
    args.put("only-matching", "true");
    args.put("tabletypes", "TABLE");

    final Map<String, String> config = new HashMap<>();

    run(args,
        config,
        directory.resolve(testName.currentMethodName() + ".html"));
  }

  private Path createConfig(final Map<String, String> config)
    throws IOException
  {
    final String prefix = SiteHTMLVariationsTest.class.getName();
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
    argsMap.put("tables", ".*");
    argsMap.put("routines", "");
    if (!argsMap.containsKey("command"))
    {
      argsMap.put("command", "schema");
    }
    argsMap.put("outputformat", "html");
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
  }

}
