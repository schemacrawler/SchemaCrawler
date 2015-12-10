package schemacrawler.tools.lint.executable;


import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.lint.LintedCatalog;
import schemacrawler.tools.traversal.TraversalHandler;

interface LintTraversalHandler
  extends TraversalHandler
{

  void handle(final LintedCatalog catalog)
    throws SchemaCrawlerException;

  void handle(final Table table)
    throws SchemaCrawlerException;

  void handleEnd()
    throws SchemaCrawlerException;

  void handleStart()
    throws SchemaCrawlerException;

}
