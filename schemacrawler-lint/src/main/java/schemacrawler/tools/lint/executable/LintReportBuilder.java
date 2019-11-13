package schemacrawler.tools.lint.executable;


import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.lint.LintedCatalog;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.traversal.TraversalHandler;

interface LintReportBuilder
{

  boolean canBuildReport(LintOptions options,
                         OutputOptions outputOptions);

  void generateLintReport(LintedCatalog catalog)
    throws SchemaCrawlerException;

}
