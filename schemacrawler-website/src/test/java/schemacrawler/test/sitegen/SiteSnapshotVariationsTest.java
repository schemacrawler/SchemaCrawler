/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.sitegen;

import static schemacrawler.test.utility.CommandlineTestUtility.commandlineExecution;
import static us.fatehi.test.utility.TestUtility.deleteIfPossible;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.lint.options.LintReportOutputFormat;
import schemacrawler.tools.command.serialize.options.SerializationFormat;
import schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import schemacrawler.tools.options.OutputFormat;
import us.fatehi.test.utility.DatabaseConnectionInfo;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;

@WithTestDatabase
@ResolveTestContext
@EnabledIfSystemProperty(named = "distrib", matches = "^((?!(false|no)).)*$")
public class SiteSnapshotVariationsTest {

  private static Path lintReportsDirectory;

  private static Path propertiesFile;

  private static Path snapshotsDirectory;

  @BeforeAll
  public static void _saveConfigProperties() throws IOException {
    propertiesFile = DatabaseTestUtility.tempHsqldbConfig();
  }

  @BeforeAll
  public static void _setupDirectory(final TestContext testContext)
      throws IOException, URISyntaxException {
    snapshotsDirectory = testContext.resolveTargetFromRootPath("_website/snapshot-examples");
    lintReportsDirectory = testContext.resolveTargetFromRootPath("_website/lint-report-examples");
  }

  @Test
  public void lintReportExamples(final DatabaseConnectionInfo connectionInfo) throws Exception {
    for (final OutputFormat outputFormat : LintReportOutputFormat.values()) {
      final String extension = outputFormat.getFormat();
      run(
          connectionInfo,
          "lint",
          null,
          outputFormat,
          lintReportsDirectory.resolve("lint_report." + extension));
    }
  }

  @Test
  public void serializeExamples(final DatabaseConnectionInfo connectionInfo) throws Exception {
    for (final OutputFormat outputFormat :
        new OutputFormat[] {SerializationFormat.json, SerializationFormat.yaml}) {
      final String extension = outputFormat.getFormat();
      run(
          connectionInfo,
          "serialize",
          null,
          outputFormat,
          snapshotsDirectory.resolve("snapshot." + extension));
    }
  }

  @Test
  public void snapshotsExamples(final DatabaseConnectionInfo connectionInfo) throws Exception {
    for (final OutputFormat outputFormat :
        new OutputFormat[] {
          TextOutputFormat.html, TextOutputFormat.text, DiagramOutputFormat.htmlx
        }) {
      final String extension;
      if ("htmlx".equals(outputFormat.getFormat())) {
        extension = "svg.html";
      } else {
        extension = outputFormat.getFormat();
      }

      run(
          connectionInfo,
          "details",
          new HashMap<>(),
          outputFormat,
          snapshotsDirectory.resolve("snapshot." + extension));
    }
  }

  private void run(
      final DatabaseConnectionInfo connectionInfo,
      final String command,
      final Map<String, String> additionalArgsMap,
      final OutputFormat outputFormat,
      final Path outputFile)
      throws Exception {
    deleteIfPossible(outputFile);

    final Map<String, String> argsMap = new HashMap<>();
    if (additionalArgsMap != null) {
      argsMap.putAll(additionalArgsMap);
    }
    argsMap.put("--info-level", "maximum");
    argsMap.put("--title", "Details of Example Database");

    commandlineExecution(
        connectionInfo, command, argsMap, propertiesFile, outputFormat.getFormat(), outputFile);
  }
}
