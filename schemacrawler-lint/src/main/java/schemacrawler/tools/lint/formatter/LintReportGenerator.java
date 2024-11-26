package schemacrawler.tools.lint.formatter;

import schemacrawler.tools.lint.report.LintReport;

public interface LintReportGenerator {

  void generateLintReport(LintReport report);
}
