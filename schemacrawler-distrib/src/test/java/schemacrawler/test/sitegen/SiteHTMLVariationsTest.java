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
import static schemacrawler.test.utility.DatabaseTestUtility.loadHsqldbConfig;
import static schemacrawler.test.utility.TestUtility.flattenCommandlineArgs;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import schemacrawler.Main;
import schemacrawler.schemacrawler.Config;
import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestContextParameterResolver;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import sf.util.IOUtility;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
@ExtendWith(TestContextParameterResolver.class)
public class SiteHTMLVariationsTest
{

  private Path directory;

  @BeforeEach
  public void _setupDirectory(final TestContext testContext)
    throws IOException,
    URISyntaxException
  {
    if (directory != null)
    {
      return;
    }
    directory = testContext.resolveTargetFromRootPath("html-examples");
  }

  @Test
  public void html(final TestContext testContext,
                   final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "maximum");

    final Map<String, String> config = new HashMap<>();

    run(connectionInfo, args, config,
        directory.resolve(testContext.testMethodName() + ".html"));
  }

  @Test
  public void html_2_portablenames(final TestContext testContext,
                                   final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "maximum");
    args.put("portablenames", "true");

    final Map<String, String> config = new HashMap<>();

    run(connectionInfo, args, config,
        directory.resolve(testContext.testMethodName() + ".html"));
  }

  @Test
  public void
    html_3_important_columns(final TestContext testContext,
                             final DatabaseConnectionInfo connectionInfo)
      throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "standard");
    args.put("command", "brief");
    args.put("portablenames", "true");

    final Map<String, String> config = new HashMap<>();

    run(connectionInfo, args, config,
        directory.resolve(testContext.testMethodName() + ".html"));
  }

  @Test
  public void html_4_ordinals(final TestContext testContext,
                              final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "standard");
    args.put("portablenames", "true");

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.format.show_ordinal_numbers", "true");

    run(connectionInfo, args, config,
        directory.resolve(testContext.testMethodName() + ".html"));
  }

  @Test
  public void html_5_alphabetical(final TestContext testContext,
                                  final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "standard");
    args.put("portablenames", "true");
    args.put("sortcolumns", "true");

    final Map<String, String> config = new HashMap<>();

    run(connectionInfo, args, config,
        directory.resolve(testContext.testMethodName() + ".html"));
  }

  @Test
  public void html_6_grep(final TestContext testContext,
                          final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "maximum");
    args.put("portablenames", "true");
    args.put("grepcolumns", ".*\\.BOOKS\\..*\\.ID");
    args.put("tabletypes", "TABLE");

    final Map<String, String> config = new HashMap<>();

    run(connectionInfo, args, config,
        directory.resolve(testContext.testMethodName() + ".html"));
  }

  @Test
  public void
    html_7_grep_onlymatching(final TestContext testContext,
                             final DatabaseConnectionInfo connectionInfo)
      throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("infolevel", "maximum");
    args.put("portablenames", "true");
    args.put("grepcolumns", ".*\\.BOOKS\\..*\\.ID");
    args.put("only-matching", "true");
    args.put("tabletypes", "TABLE");

    final Map<String, String> config = new HashMap<>();

    run(connectionInfo, args, config,
        directory.resolve(testContext.testMethodName() + ".html"));
  }

  private Path createConfig(final Map<String, String> config)
    throws IOException
  {
    final String prefix = SiteHTMLVariationsTest.class.getName();
    final Path configFile = IOUtility.createTempFilePath(prefix, "properties");
    final Properties configProperties = new Properties();
    configProperties.putAll(config);
    configProperties.store(newBufferedWriter(configFile, UTF_8), prefix);
    return configFile;
  }

  private void run(DatabaseConnectionInfo connectionInfo,
                   final Map<String, String> argsMap,
                   final Map<String, String> config, final Path outputFile)
    throws Exception
  {
    deleteIfExists(outputFile);

    argsMap.put("url", connectionInfo.getConnectionUrl());
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
