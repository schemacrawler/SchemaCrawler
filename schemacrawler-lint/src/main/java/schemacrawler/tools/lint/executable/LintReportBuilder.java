package schemacrawler.tools.lint.executable;


import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.lint.LintReport;

interface LintReportBuilder
{

  void generateLintReport(LintReport report)
    throws SchemaCrawlerException;

}
