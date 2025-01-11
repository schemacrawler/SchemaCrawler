package schemacrawler.tools.lint;

import java.util.Set;

public interface LinterInitializer {

  Set<String> getRegisteredLinters();

  Linter newLinter(String linterId, LintCollector lintCollector);
}
