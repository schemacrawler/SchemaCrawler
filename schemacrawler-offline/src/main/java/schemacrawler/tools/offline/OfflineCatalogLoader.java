package schemacrawler.tools.offline;


import java.io.FileInputStream;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.logging.Level;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.*;
import schemacrawler.tools.catalogloader.CatalogLoader;
import schemacrawler.tools.integration.serialize.JavaSerializedCatalog;
import schemacrawler.tools.offline.jdbc.OfflineConnection;
import sf.util.SchemaCrawlerLogger;

public final class OfflineCatalogLoader
  implements CatalogLoader
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(OfflineCatalogLoader.class.getName());

  private static void checkConnection(final Connection connection)
  {
    if (connection == null || !(connection instanceof OfflineConnection))
    {
      LOGGER.log(Level.SEVERE,
                 "Offline database connection not provided for the offline snapshot");
    }
  }
  private final String databaseSystemIdentifier;
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
  public Connection getConnection()
  {
    return connection;
  }

  @Override
  public String getDatabaseSystemIdentifier()
  {
    return databaseSystemIdentifier;
  }

  @Override
  public SchemaCrawlerOptions getSchemaCrawlerOptions()
  {
    return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
  }

  @Override
  public SchemaRetrievalOptions getSchemaRetrievalOptions()
  {
    return SchemaRetrievalOptionsBuilder.builder()
      .withDatabaseServerType(OfflineDatabaseConnector.DB_SERVER_TYPE)
      .toOptions();
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
    final FileInputStream inputFileStream = new FileInputStream(
      offlineDatabasePath.toFile());
    final JavaSerializedCatalog catalog = new JavaSerializedCatalog(
      inputFileStream);
    return catalog;
  }

  @Override
  public void setSchemaRetrievalOptions(final SchemaRetrievalOptions schemaRetrievalOptions)
  {
    // No-op
  }

  @Override
  public void setSchemaCrawlerOptions(final SchemaCrawlerOptions schemaCrawlerOptions)
  {
    // No-op
  }

  @Override
  public void setConnection(final Connection connection)
  {
    this.connection = connection;
  }

  @Override
  public void setAdditionalConfiguration(final Config additionalConfiguration)
  {
    this.additionalConfiguration = additionalConfiguration;
  }

}
