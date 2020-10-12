/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static java.nio.file.Files.deleteIfExists;
import static java.nio.file.Files.move;
import static schemacrawler.test.utility.CommandlineTestUtility.commandlineExecution;
import static schemacrawler.test.utility.DatabaseTestUtility.loadHsqldbConfig;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;

import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.TestAssertNoSystemErrOutput;
import schemacrawler.test.utility.TestAssertNoSystemOutOutput;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestContextParameterResolver;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.tools.options.TextOutputFormat;

@ExtendWith(TestAssertNoSystemErrOutput.class)
@ExtendWith(TestAssertNoSystemOutOutput.class)
@ExtendWith(TestDatabaseConnectionParameterResolver.class)
@ExtendWith(TestContextParameterResolver.class)
@EnabledIfSystemProperty(named = "distrib", matches = "^((?!(false|no)).)*$")
public class SiteHTMLVariationsTest {

  private static void run(
      final DatabaseConnectionInfo connectionInfo,
      final Map<String, String> argsMap,
      final Map<String, String> config,
      final Path outputFile)
      throws Exception {
    deleteIfExists(outputFile);

    argsMap.put("-title", "Details of Example Database");

    final Map<String, Object> runConfig = new HashMap<>();
    final Map<String, String> informationSchema = loadHsqldbConfig();
    runConfig.putAll(informationSchema);
    if (config != null) {
      runConfig.putAll(config);
    }

    final Path htmlFile =
        commandlineExecution(connectionInfo, "schema", argsMap, runConfig, TextOutputFormat.html);
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
    final Map<String, String> args = new HashMap<>();
    args.put("-info-level", "maximum");

    final Map<String, String> config = new HashMap<>();

    run(connectionInfo, args, config, directory.resolve(testContext.testMethodName() + ".html"));
  }

  @Test
  public void html_2_portablenames(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> args = new HashMap<>();
    args.put("-info-level", "maximum");
    args.put("-portable-names", "true");

    final Map<String, String> config = new HashMap<>();

    run(connectionInfo, args, config, directory.resolve(testContext.testMethodName() + ".html"));
  }

  @Test
  public void html_3_important_columns(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> args = new HashMap<>();
    args.put("-info-level", "standard");
    args.put("c", "brief");
    args.put("-portable-names", "true");

    final Map<String, String> config = new HashMap<>();

    run(connectionInfo, args, config, directory.resolve(testContext.testMethodName() + ".html"));
  }

  @Test
  public void html_4_ordinals(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> args = new HashMap<>();
    args.put("-info-level", "standard");
    args.put("-portable-names", "true");

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.format.show_ordinal_numbers", "true");

    run(connectionInfo, args, config, directory.resolve(testContext.testMethodName() + ".html"));
  }

  @Test
  public void html_5_alphabetical(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> args = new HashMap<>();
    args.put("-info-level", "standard");
    args.put("-portable-names", "true");
    args.put("-sort-columns", "true");

    final Map<String, String> config = new HashMap<>();

    run(connectionInfo, args, config, directory.resolve(testContext.testMethodName() + ".html"));
  }

  @Test
  public void html_6_grep(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> args = new HashMap<>();
    args.put("-info-level", "maximum");
    args.put("-portable-names", "true");
    args.put("-grep-columns", ".*\\.BOOKS\\..*\\.ID");
    args.put("-table-types", "TABLE");

    final Map<String, String> config = new HashMap<>();

    run(connectionInfo, args, config, directory.resolve(testContext.testMethodName() + ".html"));
  }

  @Test
  public void html_7_grep_onlymatching(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> args = new HashMap<>();
    args.put("-info-level", "maximum");
    args.put("-portable-names", "true");
    args.put("-grep-columns", ".*\\.BOOKS\\..*\\.ID");
    args.put("-only-matching", "true");
    args.put("-table-types", "TABLE");

    final Map<String, String> config = new HashMap<>();

    run(connectionInfo, args, config, directory.resolve(testContext.testMethodName() + ".html"));
  }
}
