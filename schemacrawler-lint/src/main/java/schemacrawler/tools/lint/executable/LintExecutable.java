package schemacrawler.tools.lint.executable;


import java.sql.Connection;

import schemacrawler.schema.Database;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.executable.BaseExecutable;
import schemacrawler.tools.lint.LintedDatabase;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.traversal.SchemaTraversalHandler;
import schemacrawler.tools.traversal.SchemaTraverser;

public class LintExecutable
  extends BaseExecutable
{

  public static final String COMMAND = "lint";

  private LintOptions lintOptions;

  public LintExecutable()
  {
    super(COMMAND);
  }

  public final LintOptions getLintOptions()
  {
    final LintOptions lintOptions;
    if (this.lintOptions == null)
    {
      lintOptions = new LintOptions(additionalConfiguration);
    }
    else
    {
      lintOptions = this.lintOptions;
    }
    return lintOptions;
  }

  public final void setLintOptions(final LintOptions lintOptions)
  {
    this.lintOptions = lintOptions;
  }

  @Override
  protected void executeOn(final Database db, final Connection connection)
    throws Exception
  {
    final LintedDatabase database = new LintedDatabase(db);

    final SchemaTraversalHandler formatter = getSchemaTraversalHandler();

    final SchemaTraverser traverser = new SchemaTraverser();
    traverser.setDatabase(database);
    traverser.setFormatter(formatter);
    traverser.traverse();

  }

  private SchemaTraversalHandler getSchemaTraversalHandler()
    throws SchemaCrawlerException
  {
    final SchemaTraversalHandler formatter;
    final LintOptions lintOptions = getLintOptions();

    final OutputFormat outputFormat = outputOptions.getOutputFormat();
    if (outputFormat == OutputFormat.json)
    {
      formatter = new LintJsonFormatter(lintOptions, outputOptions);
    }
    else
    {
      formatter = new LintTextFormatter(lintOptions, outputOptions);
    }

    return formatter;
  }

}
