/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static us.fatehi.utility.Utility.isBlank;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.SchemaReference;
import us.fatehi.utility.database.DatabaseUtility;
import us.fatehi.utility.string.StringFormat;

public class SchemaSetter implements AutoCloseable {

  private static final Logger LOGGER = Logger.getLogger(SchemaRetriever.class.getName());

  private final Connection connection;
  private final Schema oldSchema;
  private final boolean isSupportsCatalogs;
  private final boolean isSupportsSchemas;

  public SchemaSetter(final Connection connection, final Schema schema) {
    boolean errored = false;
    errored = schema == null;
    try {
      DatabaseUtility.checkConnection(connection);
    } catch (final SQLException e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      errored = true;
    }

    if (errored) {
      oldSchema = null;
      this.connection = null;
      isSupportsCatalogs = false;
      isSupportsSchemas = false;
      return;
    }

    this.connection = connection;

    boolean isSupportsCatalogs;
    boolean isSupportsSchemas;
    try {
      final DatabaseMetaData dbMetadata = connection.getMetaData();
      isSupportsCatalogs = dbMetadata.supportsCatalogsInTableDefinitions();
      isSupportsSchemas = dbMetadata.supportsSchemasInTableDefinitions();
    } catch (final SQLException e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      isSupportsCatalogs = true;
      isSupportsSchemas = true;
    }
    this.isSupportsCatalogs = isSupportsCatalogs;
    this.isSupportsSchemas = isSupportsSchemas;

    oldSchema = setSchema(schema);
  }

  @Override
  public void close() {
    setSchema(oldSchema);
  }

  private Schema setSchema(final Schema schema) {
    Schema oldSchema = null;
    if (schema == null) {
      return oldSchema;
    }
    try {
      oldSchema = new SchemaReference(connection.getCatalog(), connection.getSchema());
    } catch (final Exception e) {
      oldSchema = null;
    }

    final String catalogName = schema.getCatalogName();
    try {
      if (isSupportsCatalogs && !isBlank(catalogName)) {
        connection.setCatalog(catalogName);
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, e,
          new StringFormat("Could not set catalog <%s> on connection", catalogName));
    }

    final String schemaName = schema.getName();
    try {
      if (isSupportsSchemas && !isBlank(schemaName)) {
        connection.setSchema(schemaName);
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, e,
          new StringFormat("Could not set schema <%s> on connection", schemaName));
    }

    return oldSchema;
  }
}
