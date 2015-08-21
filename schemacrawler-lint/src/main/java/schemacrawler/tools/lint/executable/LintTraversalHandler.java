package schemacrawler.tools.lint.executable;


import java.util.Collection;

import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.lint.LintedCatalog;
import schemacrawler.tools.traversal.TraversalHandler;

public interface LintTraversalHandler
  extends TraversalHandler
{

  void handle(final Collection<? extends Table> table)
    throws SchemaCrawlerException;

  void handle(final LintedCatalog catalog)
    throws SchemaCrawlerException;

  void handleEnd()
    throws SchemaCrawlerException;

  void handleStart()
    throws SchemaCrawlerException;

}
