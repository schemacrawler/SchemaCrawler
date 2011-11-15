package schemacrawler.tools.analysis.lint;


import schemacrawler.schema.NamedObject;

public interface LintCollector
  extends Iterable<Lint<?>>
{

  final String LINT_KEY = "schemacrawler.lints";

  void addLint(final NamedObject namedObject, final Lint<?> lint);

  void clear();

  boolean isEmpty();

  int size();

  Lint<?>[] toArray();

}
