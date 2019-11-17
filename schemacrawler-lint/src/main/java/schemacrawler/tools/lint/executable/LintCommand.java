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
package schemacrawler.tools.lint.executable;


import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.lint.LintUtility.readLinterConfigs;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.executable.BaseSchemaCrawlerCommand;
import schemacrawler.tools.lint.LintDispatch;
import schemacrawler.tools.lint.LintReport;
import schemacrawler.tools.lint.LinterConfigs;
import schemacrawler.tools.lint.Linters;

public class LintCommand
  extends BaseSchemaCrawlerCommand
{

  public static final String COMMAND = "lint";

  private LintOptions lintOptions;

  public LintCommand()
  {
    super(COMMAND);
  }

  @Override
  public void checkAvailability()
    throws Exception
  {
    // Lint is always available
  }

  @Override
  public void initialize()
    throws Exception
  {
    super.initialize();
    loadLintOptions();
  }

  @Override
  public void execute()
    throws Exception
  {
    checkCatalog();

    // Lint the catalog
    final LinterConfigs linterConfigs = readLinterConfigs(lintOptions,
                                                          additionalConfiguration);
    final Linters linters = new Linters(linterConfigs,
                                        lintOptions.isRunAllLinters());
    linters.lint(catalog, connection);

    // Produce the lint report
    final LintReport lintReport = new LintReport(catalog,
                                                 linters.getCollector());

    // Write out the lint report
    getLintReportBuilder().generateLintReport(lintReport);

    dispatch(linters);
  }

  @Override
  public boolean usesConnection()
  {
    return false;
  }

  public final void setLintOptions(final LintOptions lintOptions)
  {
    this.lintOptions = requireNonNull(lintOptions, "No lint options provided");
  }

  private void dispatch(final Linters linters)
  {
    if (!linters.exceedsThreshold())
    {
      return;
    }

    final String lintSummary = linters.getLintSummary();
    if (!lintSummary.isEmpty())
    {
      System.err.println(lintSummary);
    }

    final LintDispatch lintDispatch = lintOptions.getLintDispatch();
    lintDispatch.dispatch();
  }

  private LintReportBuilder getLintReportBuilder()
    throws SchemaCrawlerException
  {
    final String outputFormatValue = outputOptions.getOutputFormatValue();
    final LintReportOutputFormat outputFormat = LintReportOutputFormat
      .fromFormat(outputFormatValue);

    final LintReportBuilder lintReportBuilder;
    switch (outputFormat)
    {
      case json:
        lintReportBuilder = new LintReportJsonBuilder(outputOptions);
        break;
      case yaml:
        lintReportBuilder = new LintReportYamlBuilder(outputOptions);
        break;
      default:
        lintReportBuilder = new LintReportTextFormatter(catalog,
                                                        lintOptions,
                                                        outputOptions,
                                                        identifiers
                                                          .getIdentifierQuoteString());
    }

    return lintReportBuilder;
  }

  private void loadLintOptions()
  {
    if (lintOptions == null)
    {
      lintOptions = LintOptionsBuilder.builder()
        .fromConfig(additionalConfiguration).toOptions();
    }
  }

}
