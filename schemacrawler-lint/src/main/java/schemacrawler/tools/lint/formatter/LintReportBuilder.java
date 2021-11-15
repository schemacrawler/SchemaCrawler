package schemacrawler.tools.lint.formatter;

import schemacrawler.tools.lint.LintReport;

public interface LintReportBuilder {

  void generateLintReport(LintReport report);
}
