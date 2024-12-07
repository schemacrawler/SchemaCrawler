package schemacrawler.tools.lint.formatter;

import schemacrawler.tools.lint.Lints;

public interface LintReportGenerator {

  void generateLintReport(Lints report);
}
