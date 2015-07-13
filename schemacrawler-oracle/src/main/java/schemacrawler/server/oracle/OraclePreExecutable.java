package schemacrawler.server.oracle;


import static sf.util.DatabaseUtility.executeScriptFromResource;

import java.sql.Connection;

import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptions;
import schemacrawler.tools.executable.BaseExecutable;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;

public class OraclePreExecutable
  extends BaseExecutable
{

  protected OraclePreExecutable()
  {
    super(OraclePreExecutable.class.getSimpleName());
  }

  @Override
  public void execute(final Connection connection)
    throws Exception
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

  @Override
  public void
    execute(Connection connection,
            DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions)
              throws Exception
  {
    execute(connection);
  }

}
