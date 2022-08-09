/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static us.fatehi.utility.database.DatabaseUtility.checkConnection;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.plugin.EnumDataTypeHelper;
import schemacrawler.schema.ConnectionInfo;
import schemacrawler.schema.TableTypes;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.MetadataRetrievalStrategy;
import schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.exceptions.DatabaseAccessException;
import schemacrawler.utility.JavaSqlTypes;
import schemacrawler.utility.TypeMap;
import us.fatehi.utility.string.StringFormat;

/** A connection for the retriever. Wraps a live database connection. */
final class RetrieverConnection {

  private static final Logger LOGGER = Logger.getLogger(RetrieverConnection.class.getName());

  private final Connection connection;
  private final JavaSqlTypes javaSqlTypes;
  private final SchemaRetrievalOptions schemaRetrievalOptions;
  private final TableTypes tableTypes;
  private final ConnectionInfo connectionInfo;

  RetrieverConnection(
      final Connection connection, final SchemaRetrievalOptions schemaRetrievalOptions)
      throws SQLException {

    this.connection = checkConnection(connection);
    this.schemaRetrievalOptions =
        requireNonNull(schemaRetrievalOptions, "No database specific overrides provided");
    connectionInfo = ConnectionInfoBuilder.builder(connection).build();

    tableTypes = TableTypes.from(connection);
    LOGGER.log(Level.CONFIG, new StringFormat("Supported table types are <%s>", tableTypes));

    javaSqlTypes = new JavaSqlTypes();
  }

  public MetadataRetrievalStrategy get(
      final SchemaInfoMetadataRetrievalStrategy schemaInfoMetadataRetrievalStrategy) {
    return schemaRetrievalOptions.get(schemaInfoMetadataRetrievalStrategy);
  }

  public ConnectionInfo getConnectionInfo() {
    return connectionInfo;
  }

  Connection getConnection() {
    return connection;
  }

  EnumDataTypeHelper getEnumDataTypeHelper() {
    return schemaRetrievalOptions.getEnumDataTypeHelper();
  }

  /**
   * Gets the INFORMATION_SCHEMA views select SQL statements.
   *
   * @return INFORMATION_SCHEMA views selects
   */
  InformationSchemaViews getInformationSchemaViews() {
    return schemaRetrievalOptions.getInformationSchemaViews();
  }

  JavaSqlTypes getJavaSqlTypes() {
    return javaSqlTypes;
  }

  DatabaseMetaData getMetaData() {
    try {
      final DatabaseMetaData metaData = connection.getMetaData();
      if (metaData == null) {
        throw new NullPointerException("No database metadata obtained");
      }
      return metaData;
    } catch (final SQLException e) {
      throw new DatabaseAccessException("No database metadata obtained", e);
    }
  }

  TableTypes getTableTypes() {
    return tableTypes;
  }

  TypeMap getTypeMap() {
    return schemaRetrievalOptions.getTypeMap();
  }

  boolean isSupportsCatalogs() {
    return schemaRetrievalOptions.isSupportsCatalogs();
  }

  boolean isSupportsSchemas() {
    return schemaRetrievalOptions.isSupportsSchemas();
  }
}
