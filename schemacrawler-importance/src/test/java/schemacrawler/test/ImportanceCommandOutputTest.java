/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.ExecutableTestUtility.hasSameContentAndTypeAs;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import schemacrawler.importance.options.ImportanceReportOutputFormat;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.LimitOptions;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.ConfigUtility;
import us.fatehi.test.utility.extensions.WithSystemProperty;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@WithTestDatabase
class ImportanceCommandOutputTest {

  private static final String IMPORTANCE_REPORT_OUTPUT = "importance_report_output/";

  @ParameterizedTest
  @EnumSource(ImportanceReportOutputFormat.class)
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  void producesFilteredImportanceReport(
      final ImportanceReportOutputFormat outputFormat,
      final DatabaseConnectionSource connectionSource)
      throws Exception {

    final LimitOptions limitOptions =
        LimitOptionsBuilder.builder().includeSchemas(new IncludeAll()).toOptions();

    final Config config = ConfigUtility.newConfig();
    config.put("table-filter", "PUBLIC\\.BOOKS\\..*BOOK.*");

    final SchemaCrawlerOptions withLimitOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLoadOptions(
                LoadOptionsBuilder.builder().withInfoLevel(InfoLevel.maximum).toOptions())
            .withLimitOptions(limitOptions);

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("importance");
    executable.setSchemaCrawlerOptions(withLimitOptions);
    executable.setAdditionalConfiguration(config);

    final String referenceFile = "importance_report.%s".formatted(outputFormat.getFormat());
    assertThat(
        outputOf(executableExecution(connectionSource, executable, outputFormat)),
        hasSameContentAndTypeAs(
            classpathResource(IMPORTANCE_REPORT_OUTPUT + referenceFile), outputFormat));
  }

  @Test
  @WithSystemProperty(key = "SC_WITHOUT_DATABASE_PLUGIN", value = "hsqldb")
  void producesImportanceReport(final DatabaseConnectionSource connectionSource) throws Exception {

    final SchemaCrawlerOptions withLimitOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLoadOptions(
                LoadOptionsBuilder.builder().withInfoLevel(InfoLevel.maximum).toOptions());

    final Config config = ConfigUtility.newConfig();
    config.put("max-important-tables", 0);
    config.put("max-communities", 0);

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("importance");
    executable.setSchemaCrawlerOptions(withLimitOptions);
    executable.setAdditionalConfiguration(config);

    ImportanceReportOutputFormat outputFormat = ImportanceReportOutputFormat.yaml;
    final String referenceFile = "importance_report_all.%s".formatted(outputFormat.getFormat());
    assertThat(
        outputOf(executableExecution(connectionSource, executable, outputFormat)),
        hasSameContentAndTypeAs(
            classpathResource(IMPORTANCE_REPORT_OUTPUT + referenceFile), outputFormat));
  }
}
