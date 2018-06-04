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

package schemacrawler.crawl;


import static java.util.Objects.requireNonNull;
import static sf.util.DatabaseUtility.checkConnection;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.logging.Level;

import schemacrawler.schemacrawler.DatabaseSpecificOptions;
import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptions;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.utility.JavaSqlTypes;
import schemacrawler.utility.TableTypes;
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

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(RetrieverConnection.class.getName());

  private final Connection connection;
  private final DatabaseMetaData metaData;
  private final DatabaseSpecificOptions databaseSpecificOptions;
  private final MetadataRetrievalStrategy tableRetrievalStrategy;
  private final MetadataRetrievalStrategy tableColumnRetrievalStrategy;
  private final MetadataRetrievalStrategy pkRetrievalStrategy;
  private final MetadataRetrievalStrategy indexRetrievalStrategy;
  private final MetadataRetrievalStrategy fkRetrievalStrategy;
  private final MetadataRetrievalStrategy procedureRetrievalStrategy;
  private final MetadataRetrievalStrategy functionRetrievalStrategy;
  private final TypeMap typeMap;
  private final InformationSchemaViews informationSchemaViews;
  private final TableTypes tableTypes;
  private final JavaSqlTypes javaSqlTypes;

  RetrieverConnection(final Connection connection,
                      final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions)
    throws SQLException
  {

    this.connection = checkConnection(connection);
    metaData = connection.getMetaData();

    requireNonNull(databaseSpecificOverrideOptions,
                   "No database specific overrides provided");

    informationSchemaViews = databaseSpecificOverrideOptions
      .getInformationSchemaViews();

    if (databaseSpecificOverrideOptions.hasOverrideForTypeMap())
    {
      typeMap = databaseSpecificOverrideOptions.getTypeMap();
    }
    else
    {
      typeMap = new TypeMap(connection);
    }

    databaseSpecificOptions = new DatabaseSpecificOptions(connection,
                                                          databaseSpecificOverrideOptions);
    LOGGER.log(Level.CONFIG, new StringFormat("%s", databaseSpecificOptions));

    tableRetrievalStrategy = databaseSpecificOverrideOptions
      .getTableRetrievalStrategy();
    tableColumnRetrievalStrategy = databaseSpecificOverrideOptions
      .getTableColumnRetrievalStrategy();
    pkRetrievalStrategy = databaseSpecificOverrideOptions
      .getPrimaryKeyRetrievalStrategy();
    indexRetrievalStrategy = databaseSpecificOverrideOptions
      .getIndexRetrievalStrategy();
    fkRetrievalStrategy = databaseSpecificOverrideOptions
      .getForeignKeyRetrievalStrategy();
    procedureRetrievalStrategy = databaseSpecificOverrideOptions
      .getProcedureRetrievalStrategy();
    functionRetrievalStrategy = databaseSpecificOverrideOptions
      .getFunctionRetrievalStrategy();

    tableTypes = new TableTypes(connection);
    LOGGER.log(Level.CONFIG,
               new StringFormat("Supported table types are <%s>", tableTypes));

    javaSqlTypes = new JavaSqlTypes();
  }

  public MetadataRetrievalStrategy getForeignKeyRetrievalStrategy()
  {
    return fkRetrievalStrategy;
  }

  public MetadataRetrievalStrategy getFunctionRetrievalStrategy()
  {
    return functionRetrievalStrategy;
  }

  public MetadataRetrievalStrategy getIndexRetrievalStrategy()
  {
    return indexRetrievalStrategy;
  }

  public MetadataRetrievalStrategy getPrimaryKeyRetrievalStrategy()
  {
    return pkRetrievalStrategy;
  }

  public MetadataRetrievalStrategy getProcedureRetrievalStrategy()
  {
    return procedureRetrievalStrategy;
  }

  public MetadataRetrievalStrategy getTableRetrievalStrategy()
  {
    return tableRetrievalStrategy;
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
    return informationSchemaViews;
  }

  JavaSqlTypes getJavaSqlTypes()
  {
    return javaSqlTypes;
  }

  DatabaseMetaData getMetaData()
  {
    return metaData;
  }

  MetadataRetrievalStrategy getTableColumnRetrievalStrategy()
  {
    return tableColumnRetrievalStrategy;
  }

  TableTypes getTableTypes()
  {
    return tableTypes;
  }

  TypeMap getTypeMap()
  {
    return typeMap;
  }

  boolean isSupportsCatalogs()
  {
    return databaseSpecificOptions.isSupportsCatalogs();
  }

  boolean isSupportsSchemas()
  {
    return databaseSpecificOptions.isSupportsSchemas();
  }

}
