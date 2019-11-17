package schemacrawler.tools.lint.executable;


import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.lint.LintReport;
import schemacrawler.tools.options.OutputOptions;

interface LintReportBuilder
{

  void generateLintReport(LintReport report)
    throws SchemaCrawlerException;

}
