package schemacrawler.tools.offline;


import static schemacrawler.filter.ReducerFactory.getRoutineReducer;
import static schemacrawler.filter.ReducerFactory.getSchemaReducer;
import static schemacrawler.filter.ReducerFactory.getSequenceReducer;
import static schemacrawler.filter.ReducerFactory.getSynonymReducer;
import static schemacrawler.filter.ReducerFactory.getTableReducer;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.logging.Level;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Reducible;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.tools.catalogloader.CatalogLoader;
import schemacrawler.tools.integration.serialize.JavaSerializedCatalog;
import schemacrawler.tools.offline.jdbc.OfflineConnection;
import schemacrawler.SchemaCrawlerLogger;

public final class OfflineCatalogLoader
  implements CatalogLoader
{

  private static final SchemaCrawlerLogger LOGGER =
    SchemaCrawlerLogger.getLogger(OfflineCatalogLoader.class.getName());

  private static void checkConnection(final Connection connection)
  {
    if (connection == null || !(connection instanceof OfflineConnection))
    {
      LOGGER.log(Level.SEVERE,
                 "Offline database connection not provided for the offline snapshot");
    }
  }

  private final String databaseSystemIdentifier;
  private SchemaCrawlerOptions schemaCrawlerOptions;
  private Config additionalConfiguration;
  private Connection connection;

  public OfflineCatalogLoader()
  {
    databaseSystemIdentifier = "offline";
  }

  @Override
  public Config getAdditionalConfiguration()
  {
    if (additionalConfiguration == null)
    {
      return new Config();
    }
    else
    {
      return additionalConfiguration;
    }
  }

  @Override
  public void setAdditionalConfiguration(final Config additionalConfiguration)
  {
    this.additionalConfiguration = additionalConfiguration;
  }

  @Override
  public Connection getConnection()
  {
    return connection;
  }

  @Override
  public void setConnection(final Connection connection)
  {
    this.connection = connection;
  }

  @Override
  public String getDatabaseSystemIdentifier()
  {
    return databaseSystemIdentifier;
  }

  @Override
  public SchemaCrawlerOptions getSchemaCrawlerOptions()
  {
    if (schemaCrawlerOptions == null)
    {
      return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    }
    else
    {
      return schemaCrawlerOptions;
    }
  }

  @Override
  public void setSchemaCrawlerOptions(final SchemaCrawlerOptions schemaCrawlerOptions)
  {
    this.schemaCrawlerOptions = schemaCrawlerOptions;
  }

  @Override
  public SchemaRetrievalOptions getSchemaRetrievalOptions()
  {
    return SchemaRetrievalOptionsBuilder
      .builder()
      .withDatabaseServerType(OfflineDatabaseConnector.DB_SERVER_TYPE)
      .toOptions();
  }

  @Override
  public void setSchemaRetrievalOptions(final SchemaRetrievalOptions schemaRetrievalOptions)
  {
    // No-op
  }

  @Override
  public Catalog loadCatalog()
    throws Exception
  {
    checkConnection(connection);

    final OfflineConnection dbConnection;
    if (connection.isWrapperFor(OfflineConnection.class))
    {
      dbConnection = connection.unwrap(OfflineConnection.class);
    }
    else
    {
      dbConnection = (OfflineConnection) connection;
    }

    final Path offlineDatabasePath = dbConnection.getOfflineDatabasePath();
    final FileInputStream inputFileStream =
      new FileInputStream(offlineDatabasePath.toFile());

    final JavaSerializedCatalog deserializedCatalog =
      new JavaSerializedCatalog(inputFileStream);

    final Catalog catalog = deserializedCatalog.getCatalog();
    reduceCatalog(catalog);

    return catalog;
  }

  private void reduceCatalog(final Catalog catalog)
  {
    ((Reducible) catalog).reduce(Schema.class,
                                 getSchemaReducer(schemaCrawlerOptions));
    ((Reducible) catalog).reduce(Table.class,
                                 getTableReducer(schemaCrawlerOptions));
    ((Reducible) catalog).reduce(Routine.class,
                                 getRoutineReducer(schemaCrawlerOptions));
    ((Reducible) catalog).reduce(Synonym.class,
                                 getSynonymReducer(schemaCrawlerOptions));
    ((Reducible) catalog).reduce(Sequence.class,
                                 getSequenceReducer(schemaCrawlerOptions));
  }

}
