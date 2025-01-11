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
import schemacrawler.tools.lint.LinterRegistry;
import schemacrawler.tools.lint.Linters;
import schemacrawler.tools.lint.Lints;
import schemacrawler.tools.lint.config.LinterConfigs;
import schemacrawler.tools.lint.formatter.LintReportGenerator;
import schemacrawler.tools.lint.formatter.LintReportJsonGenerator;
import schemacrawler.tools.lint.formatter.LintReportTextFormatter;
import schemacrawler.tools.lint.formatter.LintReportTextGenerator;
import schemacrawler.tools.lint.formatter.LintReportYamlGenerator;
import us.fatehi.utility.property.PropertyName;
import us.fatehi.utility.string.ObjectToStringFormat;

public class LintCommand extends BaseSchemaCrawlerCommand<LintOptions> {

  private static final Logger LOGGER = Logger.getLogger(LintCommand.class.getName());

  static final PropertyName COMMAND =
      new PropertyName(
          "lint",
          "Find lints (non-adherence to coding standards and conventions) "
              + "in the database schema");

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
      final LinterRegistry linterRegistry = LinterRegistry.getLinterRegistry();
      linters.initialize(linterRegistry);

      linters.lint(catalog, connection);

      // Produce the lint report
      final Lints lints = linters.getLints();

      // Write out the lint report
      LOGGER.log(Level.INFO, "Generating lint report");
      getLintReportBuilder().generateLintReport(lints);

      linters.dispatch(commandOptions.getLintDispatch());

    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not run lint command", e);
    }
  }

  @Override
  public boolean usesConnection() {
    return true;
  }

  private LintReportGenerator getLintReportBuilder() {
    final LintReportOutputFormat outputFormat =
        LintReportOutputFormat.fromFormat(outputOptions.getOutputFormatValue());

    final LintReportGenerator lintReportGenerator;
    switch (outputFormat) {
      case json:
        lintReportGenerator = new LintReportJsonGenerator(outputOptions);
        break;
      case yaml:
        lintReportGenerator = new LintReportYamlGenerator(outputOptions);
        break;
      default:
        final LintReportTextFormatter textFormatter =
            new LintReportTextFormatter(commandOptions, outputOptions, identifiers);
        final LintReportTextGenerator textGenerator = new LintReportTextGenerator();
        textGenerator.setCatalog(catalog);
        textGenerator.setHandler(textFormatter);
        lintReportGenerator = textGenerator;
    }

    return lintReportGenerator;
  }
}
