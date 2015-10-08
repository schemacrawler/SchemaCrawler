package schemacrawler.server.oracle;


import static sf.util.DatabaseUtility.executeScriptFromResource;

import java.sql.Connection;

import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptions;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.executable.BaseExecutable;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;

public class OracleExecutable
  extends BaseExecutable
{

  protected OracleExecutable(final String command)
  {
    super(command);
  }

  @Override
  public void
    execute(final Connection connection,
            final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions)
              throws Exception
  {
    executeOracleScripts(connection);

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(additionalConfiguration);
    executable.setOutputOptions(outputOptions);
    executable.execute(connection, databaseSpecificOverrideOptions);
  }

  private void executeOracleScripts(final Connection connection)
    throws SchemaCrawlerException
  {
    executeScriptFromResource(connection, "/schemacrawler-oracle.before.sql");

    final SchemaTextOptions schemaTextOptions = new SchemaTextOptionsBuilder()
      .fromConfig(additionalConfiguration).toOptions();
    if (schemaTextOptions.isShowUnqualifiedNames())
    {
      executeScriptFromResource(connection,
                                "/schemacrawler-oracle.show_unqualified_names.sql");
    }
  }

}
