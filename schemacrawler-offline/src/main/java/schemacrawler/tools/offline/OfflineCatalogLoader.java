/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.offline;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.formatter.serialize.CatalogSerializationUtility.deserializeCatalog;
import static schemacrawler.utility.MetaDataUtility.reduceCatalog;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.exceptions.DatabaseAccessException;
import schemacrawler.schemacrawler.exceptions.IORuntimeException;
import schemacrawler.tools.catalogloader.BaseCatalogLoader;
import schemacrawler.tools.executable.CommandOptions;
import schemacrawler.tools.offline.OfflineCatalogLoader.OfflineCatalogLoaderOptions;
import schemacrawler.tools.offline.jdbc.OfflineConnection;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.property.PropertyName;

public final class OfflineCatalogLoader extends BaseCatalogLoader<OfflineCatalogLoaderOptions> {

  static record OfflineCatalogLoaderOptions() implements CommandOptions {}

  private static final Logger LOGGER = Logger.getLogger(OfflineCatalogLoader.class.getName());

  public OfflineCatalogLoader() {
    super(new PropertyName("offlineloader", "Loader for offline databases"), -1);
  }

  @Override
  public void execute() {

    if (isLoaded()) {
      return;
    }

    final Catalog catalog;
    try (final Connection connection = getDataSource().get(); ) {
      if (connection == null) {
        return;
      }

      final boolean isOfflineConnection =
          connection instanceof OfflineConnection
              || connection.isWrapperFor(OfflineConnection.class);
      if (!isOfflineConnection) {
        return;
      }

      final OfflineConnection dbConnection;
      if (connection.isWrapperFor(OfflineConnection.class)) {
        dbConnection = connection.unwrap(OfflineConnection.class);
      } else {
        dbConnection = (OfflineConnection) connection;
      }

      final Path offlineDatabasePath = dbConnection.getOfflineDatabasePath();
      LOGGER.log(Level.FINE, "Derserializing from path: " + offlineDatabasePath);
      catalog = deserializeCatalog(offlineDatabasePath);

      final SchemaCrawlerOptions schemaCrawlerOptions = getSchemaCrawlerOptions();
      reduceCatalog(catalog, schemaCrawlerOptions);

    } catch (final IOException e) {
      throw new IORuntimeException("Could not load offline database", e);
    } catch (final SQLException e) {
      throw new DatabaseAccessException("Could not load offline database", e);
    }

    setCatalog(catalog);
  }

  @Override
  public void setAdditionalConfiguration(final Config additionalConfig) {
    requireNonNull(additionalConfig, "No config provided");
    setCommandOptions(new OfflineCatalogLoaderOptions());
  }
}
