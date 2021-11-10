package schemacrawler.tools.offline;

import static schemacrawler.filter.ReducerFactory.getRoutineReducer;
import static schemacrawler.filter.ReducerFactory.getSchemaReducer;
import static schemacrawler.filter.ReducerFactory.getSequenceReducer;
import static schemacrawler.filter.ReducerFactory.getSynonymReducer;
import static schemacrawler.filter.ReducerFactory.getTableReducer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Reducible;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.catalogloader.BaseCatalogLoader;
import schemacrawler.tools.executable.CommandDescription;
import schemacrawler.tools.formatter.serialize.JavaSerializedCatalog;
import schemacrawler.tools.offline.jdbc.OfflineConnection;
import us.fatehi.utility.string.StringFormat;

public final class OfflineCatalogLoader extends BaseCatalogLoader {

  private static final Logger LOGGER = Logger.getLogger(OfflineCatalogLoader.class.getName());

  public OfflineCatalogLoader() {
    super(new CommandDescription("offlineloader", "Loader for offline databases"), -1);
  }

  @Override
  public void loadCatalog() throws SchemaCrawlerException {

    if (isLoaded()) {
      return;
    }

    if (!isDatabaseSystemIdentifier(
        OfflineDatabaseConnector.DB_SERVER_TYPE.getDatabaseSystemIdentifier())) {
      return;
    }

    final Connection connection = getConnection();
    if (connection == null || !(connection instanceof OfflineConnection)) {
      return;
    }

    final Catalog catalog;
    try {
      final OfflineConnection dbConnection;
      if (connection.isWrapperFor(OfflineConnection.class)) {
        dbConnection = connection.unwrap(OfflineConnection.class);
      } else {
        dbConnection = (OfflineConnection) connection;
      }

      final Path offlineDatabasePath = dbConnection.getOfflineDatabasePath();
      LOGGER.log(
          Level.CONFIG,
          new StringFormat("Reading serialized database file <%s>", offlineDatabasePath));

      try (final InputStream inputFileStream = Files.newInputStream(offlineDatabasePath)) {
        final JavaSerializedCatalog deserializedCatalog =
            new JavaSerializedCatalog(inputFileStream);
        catalog = deserializedCatalog.getCatalog();
      }
      reduceCatalog(catalog);
    } catch (final IOException | SQLException e) {
      throw new SchemaCrawlerException("Could not load offline database", e);
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
