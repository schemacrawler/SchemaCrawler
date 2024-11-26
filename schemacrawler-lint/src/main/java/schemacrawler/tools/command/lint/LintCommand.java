/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.command.lint;

import static schemacrawler.tools.lint.config.LinterConfigUtility.readLinterConfigs;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.tools.command.lint.options.LintOptions;
import schemacrawler.tools.command.lint.options.LintReportOutputFormat;
import schemacrawler.tools.executable.BaseSchemaCrawlerCommand;
import schemacrawler.tools.lint.LintDispatch;
import schemacrawler.tools.lint.Linters;
import schemacrawler.tools.lint.config.LinterConfigs;
import schemacrawler.tools.lint.formatter.LintReportGenerator;
import schemacrawler.tools.lint.formatter.LintReportJsonGenerator;
import schemacrawler.tools.lint.formatter.LintReportTextFormatter;
import schemacrawler.tools.lint.formatter.LintReportYamlGenerator;
import schemacrawler.tools.lint.report.LintReport;
import schemacrawler.tools.lint.report.LintReportBuilder;
import us.fatehi.utility.string.ObjectToStringFormat;
import us.fatehi.utility.string.StringFormat;

public class LintCommand extends BaseSchemaCrawlerCommand<LintOptions> {

  private static final Logger LOGGER = Logger.getLogger(LintCommand.class.getName());

  public static final String COMMAND = "lint";

  public LintCommand() {
    super(COMMAND);
  }

  @Override
  public void checkAvailability() {
    // Lint is always available
  }

  @Override
  public void execute() {
    try {
      checkCatalog();

      // Lint the catalog
      final LinterConfigs linterConfigs = readLinterConfigs(commandOptions);
      LOGGER.log(Level.FINEST, new ObjectToStringFormat(linterConfigs));
      final Linters linters = new Linters(linterConfigs, commandOptions.isRunAllLinters());
      linters.lint(catalog, connection);

      // Produce the lint report
      final LintReport lintReport =
          LintReportBuilder.builder()
              .fromOptions(linters.getLintReport())
              .withOutputOptions(outputOptions)
              .build();

      // Write out the lint report
      LOGGER.log(Level.INFO, "Generating lint report");
      getLintReportBuilder().generateLintReport(lintReport);

      LOGGER.log(Level.INFO, "Dispatching lint results");
      dispatch(linters);
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not run lint command", e);
    }
  }

  @Override
  public boolean usesConnection() {
    return true;
  }

  private void dispatch(final Linters linters) {
    final boolean exceedsThreshold = linters.exceedsThreshold();

    final String lintSummary = linters.getLintSummary();
    if (!lintSummary.isEmpty()) {
      LOGGER.log(Level.INFO, new StringFormat("Lint summary:%n%s", lintSummary));
      if (exceedsThreshold) {
        System.err.println(lintSummary);
      }
    }

    if (!exceedsThreshold) {
      return;
    }

    final LintDispatch lintDispatch = commandOptions.getLintDispatch();
    lintDispatch.dispatch();
  }

  private LintReportGenerator getLintReportBuilder() {
    final LintReportOutputFormat outputFormat =
        LintReportOutputFormat.fromFormat(outputOptions.getOutputFormatValue());

    final LintReportGenerator lintReportBuilder;
    switch (outputFormat) {
      case json:
        lintReportBuilder = new LintReportJsonGenerator(outputOptions);
        break;
      case yaml:
        lintReportBuilder = new LintReportYamlGenerator(outputOptions);
        break;
      default:
        lintReportBuilder =
            new LintReportTextFormatter(catalog, commandOptions, outputOptions, identifiers);
    }

    return lintReportBuilder;
  }
}
