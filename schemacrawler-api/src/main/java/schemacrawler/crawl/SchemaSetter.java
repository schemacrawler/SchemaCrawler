/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static us.fatehi.utility.Utility.isBlank;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.SchemaReference;
import us.fatehi.utility.database.DatabaseUtility;
import us.fatehi.utility.string.StringFormat;

public class SchemaSetter implements AutoCloseable {

  private static final Logger LOGGER = Logger.getLogger(SchemaRetriever.class.getName());

  private final Connection connection;
  private final Schema oldSchema;

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
      return;
    }

    this.connection = connection;
    oldSchema = setSchema(schema);
  }

  @Override
  public void close() {
    setSchema(oldSchema);
  }

  private Schema setSchema(final Schema schema) {

    Schema oldSchema = null;

    try {
      oldSchema = new SchemaReference(connection.getCatalog(), connection.getSchema());
    } catch (final Exception e) {
      oldSchema = null;
    }

    try {
      final String catalogName = schema.getCatalogName();
      if (!isBlank(catalogName)) {
        connection.setCatalog(catalogName);
      }
      final String schemaName = schema.getName();
      if (!isBlank(schemaName)) {
        connection.setSchema(schemaName);
      }
    } catch (final Exception e) {
      LOGGER.log(
          Level.WARNING, e, new StringFormat("Could not set schema <%s> on connection", schema));
    }

    return oldSchema;
  }
}
