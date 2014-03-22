package schemacrawler.tools.oracle;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.executable.BaseExecutable;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import sf.util.Utility;

public class OraclePreExecutable
  extends BaseExecutable
{

  private static final Logger LOGGER = Logger
    .getLogger(OraclePreExecutable.class.getName());

  protected OraclePreExecutable()
  {
    super(OraclePreExecutable.class.getSimpleName());
  }

  @Override
  public void execute(final Connection connection)
    throws Exception
  {
    final SchemaTextOptions schemaTextOptions = new SchemaTextOptions(additionalConfiguration);
    if (schemaTextOptions.isShowUnqualifiedNames())
    {
      executeScript("/schemacrawler-oracle.show_unqualified_names.sql",
                    connection);
    }
  }

  private void executeScript(final String scriptResource,
                             final Connection connection)
    throws SchemaCrawlerException
  {
    try (final Statement statement = connection.createStatement();)
    {
      final String sqlScript = Utility.readResourceFully(scriptResource);
      if (!Utility.isBlank(sqlScript))
      {
        for (final String sql: sqlScript.split(";"))
        {
          if (!Utility.isBlank(sql))
          {
            statement.executeUpdate(sql);
          }
        }
      }
    }
    catch (final SQLException e)
    {
      System.err.println(e.getMessage());
      LOGGER.log(Level.WARNING, e.getMessage(), e);
    }
  }

}
