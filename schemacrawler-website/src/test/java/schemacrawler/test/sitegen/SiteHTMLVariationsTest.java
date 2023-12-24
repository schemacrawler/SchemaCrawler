/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static java.nio.file.Files.move;
import static schemacrawler.test.utility.CommandlineTestUtility.commandlineExecution;
import static schemacrawler.test.utility.DatabaseTestUtility.loadHsqldbConfig;
import static schemacrawler.test.utility.TestUtility.deleteIfPossible;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;

@WithTestDatabase
@ResolveTestContext
@EnabledIfSystemProperty(named = "distrib", matches = "^((?!(false|no)).)*$")
public class SiteHTMLVariationsTest {

  private static void run(
      final DatabaseConnectionInfo connectionInfo,
      final Map<String, String> argsMapMap,
      final Map<String, String> config,
      final Path outputFile)
      throws Exception {
    deleteIfPossible(outputFile);

    argsMapMap.put("-title", "Details of Example Database");

    final Map<String, Object> runConfig = new HashMap<>();
    final Map<String, String> informationSchema = loadHsqldbConfig();
    runConfig.putAll(informationSchema);
    if (config != null) {
      runConfig.putAll(config);
    }

    final Path htmlFile =
        commandlineExecution(
            connectionInfo, "schema", argsMapMap, runConfig, TextOutputFormat.html);
    move(htmlFile, outputFile);
  }

  private Path directory;

  @BeforeEach
  public void _setupDirectory(final TestContext testContext)
      throws IOException, URISyntaxException {
    if (directory != null) {
      return;
    }
    directory = testContext.resolveTargetFromRootPath("_website/html-examples");
  }

  @Test
  public void html(final TestContext testContext, final DatabaseConnectionInfo connectionInfo)
      throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--info-level", "maximum");

    run(connectionInfo, argsMap, null, directory.resolve(testContext.testMethodName() + ".html"));
  }

  @Test
  public void html_2_portablenames(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--info-level", "maximum");
    argsMap.put("--portable-names", "true");

    run(connectionInfo, argsMap, null, directory.resolve(testContext.testMethodName() + ".html"));
  }

  @Test
  public void html_3_important_columns(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--info-level", "standard");
    argsMap.put("c", "brief");
    argsMap.put("--portable-names", "true");

    run(connectionInfo, argsMap, null, directory.resolve(testContext.testMethodName() + ".html"));
  }

  @Test
  public void html_4_ordinals(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--info-level", "standard");
    argsMap.put("--portable-names", "true");

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.format.show_ordinal_numbers", "true");

    run(connectionInfo, argsMap, config, directory.resolve(testContext.testMethodName() + ".html"));
  }

  @Test
  public void html_5_alphabetical(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--info-level", "standard");
    argsMap.put("--portable-names", "true");
    argsMap.put("--sort-columns", "true");

    run(connectionInfo, argsMap, null, directory.resolve(testContext.testMethodName() + ".html"));
  }

  @Test
  public void html_6_grep(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--info-level", "maximum");
    argsMap.put("--portable-names", "true");
    argsMap.put("--grep-columns", ".*\\.BOOKS\\..*\\.ID");
    argsMap.put("--table-types", "TABLE");

    run(connectionInfo, argsMap, null, directory.resolve(testContext.testMethodName() + ".html"));
  }
}
