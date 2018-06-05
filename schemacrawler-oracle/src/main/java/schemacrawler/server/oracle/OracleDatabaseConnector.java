/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
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


import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.regex.Pattern;

import schemacrawler.crawl.MetadataRetrievalStrategy;
import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.schemacrawler.InformationSchemaViewsBuilder;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.iosource.ClasspathInputResource;
import sf.util.SchemaCrawlerLogger;

public final class OracleDatabaseConnector
  extends DatabaseConnector
{

  static final DatabaseServerType DB_SERVER_TYPE = new DatabaseServerType("oracle", "Oracle");

  private static class OracleInformationSchemaViewsBuilder
    implements BiConsumer<InformationSchemaViewsBuilder, Connection>
  {

    @Override
    public void accept(final InformationSchemaViewsBuilder informationSchemaViewsBuilder,
                       final Connection connection)
    {
      if (informationSchemaViewsBuilder == null)
      {
        LOGGER.log(Level.FINE, "No information schema views builder provided");
        return;
      }

      informationSchemaViewsBuilder
        .fromResourceFolder("/oracle.information_schema");

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
          informationSchemaViewsBuilder
            .fromResourceFolder("/oracle.information_schema.old");
        }
      }
      catch (final Exception e)
      {
        LOGGER.log(Level.FINE, e.getMessage(), e);
        return;
      }

    }

  }

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(OracleDatabaseConnector.class.getName());

  public OracleDatabaseConnector()
    throws IOException
  {
    super(DB_SERVER_TYPE,
          new ClasspathInputResource("/help/Connections.oracle.txt"),
          new ClasspathInputResource("/schemacrawler-oracle.config.properties"),
          new OracleInformationSchemaViewsBuilder(),
          url -> Pattern.matches("jdbc:oracle:.*", url));

    System.setProperty("oracle.jdbc.Trace", "true");
  }

  @Override
  public SchemaRetrievalOptionsBuilder getSchemaRetrievalOptionsBuilder(final Connection connection)
  {
    final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder = super.getSchemaRetrievalOptionsBuilder(connection);
    schemaRetrievalOptionsBuilder
      .withTableColumnRetrievalStrategy(MetadataRetrievalStrategy.data_dictionary_all)
      .withForeignKeyRetrievalStrategy(MetadataRetrievalStrategy.data_dictionary_all)
      .withIndexRetrievalStrategy(MetadataRetrievalStrategy.data_dictionary_all);
    return schemaRetrievalOptionsBuilder;
  }

}
