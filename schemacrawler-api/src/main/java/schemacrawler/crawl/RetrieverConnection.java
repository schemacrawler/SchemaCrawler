/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;
import schemacrawler.plugin.EnumDataTypeHelper;
import schemacrawler.schema.TableTypes;
import schemacrawler.schemacrawler.Identifiers;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.MetadataRetrievalStrategy;
import schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.utility.JavaSqlTypes;
import schemacrawler.utility.TypeMap;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

/** A connection for the retriever. Wraps a live database connection. */
final class RetrieverConnection {

  private static final Logger LOGGER = Logger.getLogger(RetrieverConnection.class.getName());

  private final DatabaseConnectionSource dataSource;
  private final JavaSqlTypes javaSqlTypes;
  private final SchemaRetrievalOptions schemaRetrievalOptions;

  RetrieverConnection(
      final DatabaseConnectionSource dataSource,
      final SchemaRetrievalOptions schemaRetrievalOptions)
      throws SQLException {

    this.dataSource = requireNonNull(dataSource, "Database connection source not provided");

    this.schemaRetrievalOptions =
        requireNonNull(schemaRetrievalOptions, "No database specific overrides provided");

    javaSqlTypes = new JavaSqlTypes();
  }

  public MetadataRetrievalStrategy get(
      final SchemaInfoMetadataRetrievalStrategy schemaInfoMetadataRetrievalStrategy) {
    return schemaRetrievalOptions.get(schemaInfoMetadataRetrievalStrategy);
  }

  Connection getConnection(final String reason) {
    LOGGER.log(
        Level.INFO,
        () -> {
          if (!isBlank(reason)) {
            return String.format("Getting database connnection for %s", reason);
          }
          return "Getting database connnection";
        });
    return dataSource.get();
  }

  EnumDataTypeHelper getEnumDataTypeHelper() {
    return schemaRetrievalOptions.getEnumDataTypeHelper();
  }

  Identifiers getIdentifiers() {
    return schemaRetrievalOptions.getIdentifiers();
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

  TableTypes getTableTypes() {
    return schemaRetrievalOptions.getTableTypes();
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
