package schemacrawler.tools.lint.executable;


import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.lint.LintedDatabase;
import schemacrawler.tools.traversal.TraversalHandler;

public interface LintFormatter
  extends TraversalHandler
{

  void handle(final LintedDatabase database)
    throws SchemaCrawlerException;

  void handle(final Table table)
    throws SchemaCrawlerException;

  void handleEnd()
    throws SchemaCrawlerException;

  void handleStart()
    throws SchemaCrawlerException;

}
