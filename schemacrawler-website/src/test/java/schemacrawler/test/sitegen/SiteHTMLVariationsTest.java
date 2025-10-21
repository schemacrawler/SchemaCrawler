/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.sitegen;

import static java.nio.file.Files.move;
import static schemacrawler.test.utility.CommandlineTestUtility.commandlineExecution;
import static schemacrawler.test.utility.DatabaseTestUtility.loadHsqldbConfig;
import static us.fatehi.test.utility.TestUtility.deleteIfPossible;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.text.schema.options.PortableType;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import us.fatehi.test.utility.DatabaseConnectionInfo;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;

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
    argsMap.put("--info-level", InfoLevel.maximum.name());

    run(connectionInfo, argsMap, null, directory.resolve(testContext.testMethodName() + ".html"));
  }

  @Test
  public void html_2_portablenames(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--info-level", InfoLevel.maximum.name());
    argsMap.put("--portable", PortableType.names.name());

    run(connectionInfo, argsMap, null, directory.resolve(testContext.testMethodName() + ".html"));
  }

  @Test
  public void html_3_important_columns(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--info-level", InfoLevel.standard.name());
    argsMap.put("c", "brief");
    argsMap.put("--portable", PortableType.names.name());

    run(connectionInfo, argsMap, null, directory.resolve(testContext.testMethodName() + ".html"));
  }

  @Test
  public void html_4_ordinals(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--info-level", InfoLevel.standard.name());
    argsMap.put("--portable", PortableType.names.name());

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.format.show_ordinal_numbers", Boolean.TRUE.toString());

    run(connectionInfo, argsMap, config, directory.resolve(testContext.testMethodName() + ".html"));
  }

  @Test
  public void html_5_alphabetical(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--info-level", InfoLevel.standard.name());
    argsMap.put("--portable", PortableType.names.name());
    argsMap.put("--sort-columns", Boolean.TRUE.toString());

    run(connectionInfo, argsMap, null, directory.resolve(testContext.testMethodName() + ".html"));
  }

  @Test
  public void html_6_grep(
      final TestContext testContext, final DatabaseConnectionInfo connectionInfo) throws Exception {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--info-level", InfoLevel.maximum.name());
    argsMap.put("--portable", PortableType.names.name());
    argsMap.put("--grep-columns", ".*\\.BOOKS\\..*\\.ID");
    argsMap.put("--table-types", "TABLE");

    run(connectionInfo, argsMap, null, directory.resolve(testContext.testMethodName() + ".html"));
  }
}
