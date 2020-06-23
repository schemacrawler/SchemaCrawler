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

package schemacrawler.crawl;


import static java.util.Objects.requireNonNull;
import static sf.util.DatabaseUtility.checkConnection;
import static sf.util.Utility.isBlank;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

import schemacrawler.plugin.EnumDataTypeHelper;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.MetadataRetrievalStrategy;
import schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.utility.JavaSqlTypes;
import schemacrawler.schema.TableTypes;
import schemacrawler.utility.TypeMap;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

/**
 * A connection for the retriever. Wraps a live database connection.
 *
 * @author Sualeh Fatehi
 */
final class RetrieverConnection
{

  private static final SchemaCrawlerLogger LOGGER =
    SchemaCrawlerLogger.getLogger(RetrieverConnection.class.getName());

  private final Connection connection;
  private final JavaSqlTypes javaSqlTypes;
  private final DatabaseMetaData metaData;
  private final SchemaRetrievalOptions schemaRetrievalOptions;
  private final TableTypes tableTypes;

  RetrieverConnection(final Connection connection,
                      final SchemaRetrievalOptions schemaRetrievalOptions)
    throws SQLException
  {

    this.connection = checkConnection(connection);
    metaData = connection.getMetaData();
    this.schemaRetrievalOptions = requireNonNull(schemaRetrievalOptions,
                                                 "No database specific overrides provided");

    tableTypes = TableTypes.from(connection);
    LOGGER.log(Level.CONFIG,
               new StringFormat("Supported table types are <%s>", tableTypes));

    javaSqlTypes = new JavaSqlTypes();
  }

  public MetadataRetrievalStrategy get(final SchemaInfoMetadataRetrievalStrategy schemaInfoMetadataRetrievalStrategy)
  {
    return schemaRetrievalOptions.get(schemaInfoMetadataRetrievalStrategy);
  }

  public Driver getDriver()
    throws SQLException
  {
    Driver jdbcDriver = null;

    final String jdbcDriverClassName = schemaRetrievalOptions
      .getDatabaseServerType()
      .getJdbcDriverClassName();
    if (!isBlank(jdbcDriverClassName))
    {
      try
      {
        final Class<? extends Driver> jdbcDriverClass =
          (Class<? extends Driver>) Class.forName(jdbcDriverClassName);
        jdbcDriver = jdbcDriverClass
          .getDeclaredConstructor()
          .newInstance();
      }
      catch (final Exception e)
      {
        LOGGER.log(Level.WARNING,
                   "No JDBC driver found for " + jdbcDriverClassName,
                   e);
      }
    }

    if (jdbcDriver == null)
    {
      jdbcDriver = DriverManager.getDriver(connection
                                             .getMetaData()
                                             .getURL());
    }

    if (jdbcDriver == null)
    {
      throw new SQLException("No JDBC driver found");
    }

    return jdbcDriver;
  }

  Connection getConnection()
  {
    return connection;
  }

  /**
   * Gets the INFORMATION_SCHEMA views select SQL statements.
   *
   * @return INFORMATION_SCHEMA views selects
   */
  InformationSchemaViews getInformationSchemaViews()
  {
    return schemaRetrievalOptions.getInformationSchemaViews();
  }

  EnumDataTypeHelper getEnumDataTypeHelper()
  {
    return schemaRetrievalOptions.getEnumDataTypeHelper();
  }

  JavaSqlTypes getJavaSqlTypes()
  {
    return javaSqlTypes;
  }

  DatabaseMetaData getMetaData()
  {
    return metaData;
  }

  TableTypes getTableTypes()
  {
    return tableTypes;
  }

  TypeMap getTypeMap()
  {
    return schemaRetrievalOptions.getTypeMap();
  }

  boolean isSupportsCatalogs()
  {
    return schemaRetrievalOptions.isSupportsCatalogs();
  }

  boolean isSupportsSchemas()
  {
    return schemaRetrievalOptions.isSupportsSchemas();
  }

}
