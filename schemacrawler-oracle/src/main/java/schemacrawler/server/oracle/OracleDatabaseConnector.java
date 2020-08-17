/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package schemacrawler.server.oracle;


import static schemacrawler.schemacrawler.MetadataRetrievalStrategy.data_dictionary_all;
import static schemacrawler.schemacrawler.QueryUtility.executeForScalar;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.foreignKeysRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.functionParametersRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.functionsRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.indexesRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.primaryKeysRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.procedureParametersRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.proceduresRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.tableColumnsRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.tablesRetrievalStrategy;
import static schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy.typeInfoRetrievalStrategy;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.regex.Pattern;

import schemacrawler.SchemaCrawlerLogger;
import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.schemacrawler.InformationSchemaViewsBuilder;
import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.executable.commandline.PluginCommand;
import us.fatehi.utility.ioresource.ClasspathInputResource;
import us.fatehi.utility.string.StringFormat;

public final class OracleDatabaseConnector
  extends DatabaseConnector
{

  static final DatabaseServerType DB_SERVER_TYPE =
    new DatabaseServerType("oracle", "Oracle");
  private static final SchemaCrawlerLogger LOGGER =
    SchemaCrawlerLogger.getLogger(OracleDatabaseConnector.class.getName());


  private static class OracleInformationSchemaViewsBuilder
    implements BiConsumer<InformationSchemaViewsBuilder, Connection>
  {

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

  public OracleDatabaseConnector()
    throws IOException
  {
    super(DB_SERVER_TYPE,
          new ClasspathInputResource("/schemacrawler-oracle.config.properties"),
          new OracleInformationSchemaViewsBuilder());

    System.setProperty("oracle.jdbc.Trace", "true");
  }

  @Override
  public SchemaRetrievalOptionsBuilder getSchemaRetrievalOptionsBuilder(final Connection connection)
  {
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder =
      super.getSchemaRetrievalOptionsBuilder(connection);
    schemaRetrievalOptionsBuilder
      .with(typeInfoRetrievalStrategy, data_dictionary_all)
      .with(tablesRetrievalStrategy, data_dictionary_all)
      .with(tableColumnsRetrievalStrategy, data_dictionary_all)
      .with(primaryKeysRetrievalStrategy, data_dictionary_all)
      .with(foreignKeysRetrievalStrategy, data_dictionary_all)
      .with(indexesRetrievalStrategy, data_dictionary_all)
      .with(proceduresRetrievalStrategy, data_dictionary_all)
      .with(procedureParametersRetrievalStrategy, data_dictionary_all)
      .with(functionsRetrievalStrategy, data_dictionary_all)
      .with(functionParametersRetrievalStrategy, data_dictionary_all);
    return schemaRetrievalOptionsBuilder;
  }

  @Override
  public PluginCommand getHelpCommand()
  {
    final PluginCommand pluginCommand = super.getHelpCommand();
    pluginCommand
      .addOption("server",
                 "--server=oracle%n" + "Loads SchemaCrawler plug-in for Oracle",
                 String.class)
      .addOption("host",
                 "Host name%n" + "Optional, defaults to localhost",
                 String.class)
      .addOption("port",
                 "Port number%n" + "Optional, defaults to 1521",
                 Integer.class)
      .addOption("database",
                 "Oracle Service Name%n"
                 + "You can use a query similar to the one below to find it.%n"
                 + "SELECT GLOBAL_NAME FROM GLOBAL_NAME",
                 String.class);
    return pluginCommand;
  }

  @Override
  protected Predicate<String> supportsUrlPredicate()
  {
    return url -> Pattern.matches("jdbc:oracle:.*", url);
  }

}
