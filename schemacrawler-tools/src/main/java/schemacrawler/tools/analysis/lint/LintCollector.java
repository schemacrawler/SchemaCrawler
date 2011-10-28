package schemacrawler.tools.analysis.lint;


import schemacrawler.schema.Column;
import schemacrawler.schema.Database;
import schemacrawler.schema.Table;

public interface LintCollector
  extends Iterable<Lint>
{

  void addLint(final Column column, final Lint lint);

  void addLint(final Database database, final Lint lint);

  void addLint(final Table table, final Lint lint);

  void clear();

  boolean isEmpty();

  int size();

  Lint[] toArray();

  Lint[] getLint(final Column column);

  Lint[] getLint(final Database database);

  Lint[] getLint(final Table table);

}
