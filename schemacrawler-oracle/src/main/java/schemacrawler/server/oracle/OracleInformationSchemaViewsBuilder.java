package schemacrawler.server.oracle;

import static schemacrawler.schemacrawler.QueryUtility.executeForScalar;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schemacrawler.InformationSchemaViewsBuilder;
import schemacrawler.schemacrawler.Query;
import us.fatehi.utility.string.StringFormat;

class OracleInformationSchemaViewsBuilder
  implements BiConsumer<InformationSchemaViewsBuilder, Connection>
{
  

  private static final Logger LOGGER =
    Logger.getLogger(OracleDatabaseConnector.class.getName());

  private static String getCatalogScope(final Connection connection)
  {
    String catalogScope = "ALL";
    try
    {
      final Query query = new Query("Check access to DBA tables",
                                    "SELECT TABLE_NAME FROM DBA_TABLES WHERE ROWNUM = 1");
      final Object scalar = executeForScalar(query, connection);
      if (scalar != null)
      {
        catalogScope = "DBA";
      }
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.FINE, "Could not check access to DBA tables", e);
      catalogScope = "ALL";
    }

    LOGGER.log(Level.INFO,
               new StringFormat(
                 "Using Oracle data dictionary catalog scope <%s>",
                 catalogScope));
    return catalogScope;
  }

  @Override
  public void accept(final InformationSchemaViewsBuilder informationSchemaViewsBuilder,
                     final Connection connection)
  {
    if (informationSchemaViewsBuilder == null)
    {
      LOGGER.log(Level.FINE, "No information schema views builder provided");
      return;
    }

    informationSchemaViewsBuilder.fromResourceFolder(
      "/oracle.information_schema");

    try
    {
      if (connection == null)
      {
        LOGGER.log(Level.FINE, "No Oracle database connection provided");
        return;
      }
      final DatabaseMetaData dbMetaData = connection.getMetaData();
      final int oracleMajorVersion = dbMetaData.getDatabaseMajorVersion();
      if (oracleMajorVersion < 12)
      {
        informationSchemaViewsBuilder.fromResourceFolder(
          "/oracle.information_schema.old");
      }
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.FINE, e.getMessage(), e);
      return;
    }

    // Check level of access
    final String catalogScope = getCatalogScope(connection);
    informationSchemaViewsBuilder.substituteAll("catalogscope", catalogScope);
  }

}