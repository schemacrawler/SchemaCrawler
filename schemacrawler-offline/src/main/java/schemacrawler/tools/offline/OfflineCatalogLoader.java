package schemacrawler.tools.offline;

import static java.nio.file.Files.newInputStream;
import static schemacrawler.utility.MetaDataUtility.reduceCatalog;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.zip.GZIPInputStream;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.exceptions.DatabaseAccessException;
import schemacrawler.schemacrawler.exceptions.IORuntimeException;
import schemacrawler.tools.catalogloader.BaseCatalogLoader;
import schemacrawler.tools.formatter.serialize.JavaSerializedCatalog;
import schemacrawler.tools.offline.jdbc.OfflineConnection;
import us.fatehi.utility.property.PropertyName;

public final class OfflineCatalogLoader extends BaseCatalogLoader {

  public OfflineCatalogLoader() {
    super(new PropertyName("offlineloader", "Loader for offline databases"), -1);
  }

  @Override
  public void loadCatalog() {

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
      try (final InputStream inputFileStream =
          new GZIPInputStream(newInputStream(offlineDatabasePath)); ) {
        final JavaSerializedCatalog deserializedCatalog =
            new JavaSerializedCatalog(inputFileStream);
        catalog = deserializedCatalog.getCatalog();
      }

      final SchemaCrawlerOptions schemaCrawlerOptions = getSchemaCrawlerOptions();
      reduceCatalog(catalog, schemaCrawlerOptions);

    } catch (final IOException e) {
      throw new IORuntimeException("Could not load offline database", e);
    } catch (final SQLException e) {
      throw new DatabaseAccessException("Could not load offline database", e);
    }

    setCatalog(catalog);
  }
}
