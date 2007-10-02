package schemacrawler.tools.integration;


import javax.sql.DataSource;

import schemacrawler.tools.ExecutionContext;
import schemacrawler.tools.Executor;
import schemacrawler.tools.schematext.SchemaTextOptions;

public class ToolsExecutorAdapter
  implements Executor
{

  private final SchemaCrawlerExecutor schemaCrawlerExecutor;

  public ToolsExecutorAdapter(SchemaCrawlerExecutor schemaCrawlerExecutor)
  {
    this.schemaCrawlerExecutor = schemaCrawlerExecutor;
  }

  public void execute(ExecutionContext executionContext, DataSource dataSource)
    throws Exception
  {
    schemaCrawlerExecutor.execute(executionContext.getSchemaCrawlerOptions(),
                                  (SchemaTextOptions) executionContext
                                    .getToolOptions(),
                                  dataSource);
  }

}
