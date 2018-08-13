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

import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
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
  private final SchemaRetrievalOptions schemaRetrievalOptions;
  private final TableTypes tableTypes;
  private final JavaSqlTypes javaSqlTypes;

  RetrieverConnection(final Connection connection,
                      final SchemaRetrievalOptions schemaRetrievalOptions)
    throws SQLException
  {

    this.connection = checkConnection(connection);
    metaData = connection.getMetaData();
    this.schemaRetrievalOptions = requireNonNull(schemaRetrievalOptions,
                                                 "No database specific overrides provided");

    LOGGER.log(Level.CONFIG, new StringFormat("%s", schemaRetrievalOptions));

    tableTypes = new TableTypes(connection);
    LOGGER.log(Level.CONFIG,
               new StringFormat("Supported table types are <%s>", tableTypes));

    javaSqlTypes = new JavaSqlTypes();
  }

  Connection getConnection()
  {
    return connection;
  }

  MetadataRetrievalStrategy getForeignKeyRetrievalStrategy()
  {
    return schemaRetrievalOptions.getForeignKeyRetrievalStrategy();
  }

  MetadataRetrievalStrategy getFunctionColumnRetrievalStrategy()
  {
    return schemaRetrievalOptions.getFunctionColumnRetrievalStrategy();
  }

  MetadataRetrievalStrategy getFunctionRetrievalStrategy()
  {
    return schemaRetrievalOptions.getFunctionRetrievalStrategy();
  }

  MetadataRetrievalStrategy getIndexRetrievalStrategy()
  {
    return schemaRetrievalOptions.getIndexRetrievalStrategy();
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

  JavaSqlTypes getJavaSqlTypes()
  {
    return javaSqlTypes;
  }

  DatabaseMetaData getMetaData()
  {
    return metaData;
  }

  MetadataRetrievalStrategy getPrimaryKeyRetrievalStrategy()
  {
    return schemaRetrievalOptions.getPrimaryKeyRetrievalStrategy();
  }

  MetadataRetrievalStrategy getProcedureColumnRetrievalStrategy()
  {
    return schemaRetrievalOptions.getProcedureColumnRetrievalStrategy();
  }

  MetadataRetrievalStrategy getProcedureRetrievalStrategy()
  {
    return schemaRetrievalOptions.getProcedureRetrievalStrategy();
  }

  MetadataRetrievalStrategy getTableColumnRetrievalStrategy()
  {
    return schemaRetrievalOptions.getTableColumnRetrievalStrategy();
  }

  MetadataRetrievalStrategy getTableRetrievalStrategy()
  {
    return schemaRetrievalOptions.getTableRetrievalStrategy();
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
