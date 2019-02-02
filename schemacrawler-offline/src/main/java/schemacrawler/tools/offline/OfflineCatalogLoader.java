package schemacrawler.tools.offline;


import java.io.FileInputStream;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.logging.Level;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.tools.catalogloader.CatalogLoader;
import schemacrawler.tools.integration.serialization.JavaSerializedCatalog;
import schemacrawler.tools.offline.jdbc.OfflineConnection;
import sf.util.SchemaCrawlerLogger;

public final class OfflineCatalogLoader
  implements CatalogLoader
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(OfflineCatalogLoader.class.getName());

  private final String databaseSystemIdentifier;
  private Connection connection;
  private Config additionalConfiguration;

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

    final Path offlineDatabasePath = ((OfflineConnection) connection)
      .getOfflineDatabasePath();
    final FileInputStream inputFileStream = new FileInputStream(offlineDatabasePath
      .toFile());
    final JavaSerializedCatalog catalog = new JavaSerializedCatalog(inputFileStream);
    return catalog;
  }

  @Override
  public void setAdditionalConfiguration(final Config additionalConfiguration)
  {
    this.additionalConfiguration = additionalConfiguration;
  }

  @Override
  public void setConnection(final Connection connection)
  {
    this.connection = connection;
  }

  @Override
  public void setSchemaCrawlerOptions(final SchemaCrawlerOptions schemaCrawlerOptions)
  {
    // No-op
  }

  @Override
  public void setSchemaRetrievalOptions(final SchemaRetrievalOptions schemaRetrievalOptions)
  {
    // No-op
  }

  private void checkConnection(final Connection connection)
  {
    if (connection == null || !(connection instanceof OfflineConnection))
    {
      LOGGER
        .log(Level.SEVERE,
             "Offline database connection not provided for the offline snapshot");
    }
  }

}
