package schemacrawler.tools.offline;

import static java.nio.file.Files.newInputStream;
import static schemacrawler.filter.ReducerFactory.getRoutineReducer;
import static schemacrawler.filter.ReducerFactory.getSchemaReducer;
import static schemacrawler.filter.ReducerFactory.getSequenceReducer;
import static schemacrawler.filter.ReducerFactory.getSynonymReducer;
import static schemacrawler.filter.ReducerFactory.getTableReducer;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.zip.GZIPInputStream;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Reducible;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.exceptions.DatabaseAccessException;
import schemacrawler.schemacrawler.exceptions.IORuntimeException;
import schemacrawler.tools.catalogloader.BaseCatalogLoader;
import schemacrawler.tools.executable.CommandDescription;
import schemacrawler.tools.formatter.serialize.JavaSerializedCatalog;
import schemacrawler.tools.offline.jdbc.OfflineConnection;

public final class OfflineCatalogLoader extends BaseCatalogLoader {

  public OfflineCatalogLoader() {
    super(new CommandDescription("offlineloader", "Loader for offline databases"), -1);
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
      reduceCatalog(catalog);
    } catch (final IOException e) {
      throw new IORuntimeException("Could not load offline database", e);
    } catch (final SQLException e) {
      throw new DatabaseAccessException("Could not load offline database", e);
    }

    setCatalog(catalog);
  }

  private void reduceCatalog(final Catalog catalog) {
    final SchemaCrawlerOptions schemaCrawlerOptions = getSchemaCrawlerOptions();
    ((Reducible) catalog).reduce(Schema.class, getSchemaReducer(schemaCrawlerOptions));
    ((Reducible) catalog).reduce(Table.class, getTableReducer(schemaCrawlerOptions));
    ((Reducible) catalog).reduce(Routine.class, getRoutineReducer(schemaCrawlerOptions));
    ((Reducible) catalog).reduce(Synonym.class, getSynonymReducer(schemaCrawlerOptions));
    ((Reducible) catalog).reduce(Sequence.class, getSequenceReducer(schemaCrawlerOptions));
  }
}
