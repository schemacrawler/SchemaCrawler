package schemacrawler.tools.lint.executable;


import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.lint.LintReport;
import schemacrawler.tools.options.OutputOptions;

interface LintReportBuilder
{

  boolean canBuildReport(LintOptions options,
                         OutputOptions outputOptions);

  void generateLintReport(LintReport report)
    throws SchemaCrawlerException;

}
