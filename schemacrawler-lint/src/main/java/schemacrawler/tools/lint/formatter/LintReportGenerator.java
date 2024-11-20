package schemacrawler.tools.lint.formatter;

import schemacrawler.tools.lint.LintReport;

public interface LintReportGenerator {

  void generateLintReport(LintReport report);
}
