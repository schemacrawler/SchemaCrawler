package schemacrawler.tools.offline;


import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.logging.Level;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.tools.catalogloader.CatalogLoader;
import schemacrawler.tools.integration.serialization.XmlSerializedCatalog;
import schemacrawler.tools.offline.jdbc.OfflineConnection;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
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
  public void setAdditionalConfiguration(Config additionalConfiguration)
  {
    this.additionalConfiguration = additionalConfiguration;
  }

  @Override
  public Catalog loadCatalog()
    throws Exception
  {
    checkConnection(connection);

    final Path offlineDatabasePath = ((OfflineConnection) connection)
      .getOfflineDatabasePath();
    final OutputOptions inputOptions = new OutputOptionsBuilder()
      .fromConfig(additionalConfiguration)
      .withCompressedInputFile(offlineDatabasePath).toOptions();

    final Reader snapshotReader;
    try
    {
      snapshotReader = inputOptions.openNewInputReader();
    }
    catch (final IOException e)
    {
      throw new SchemaCrawlerException("Cannot open input reader", e);
    }

    final XmlSerializedCatalog catalog = new XmlSerializedCatalog(snapshotReader);
    return catalog;
  }

  @Override
  public SchemaCrawlerOptions getSchemaCrawlerOptions()
  {
    return SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
  }

  @Override
  public void setSchemaCrawlerOptions(SchemaCrawlerOptions schemaCrawlerOptions)
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

  @Override
  public String getDatabaseSystemIdentifier()
  {
    return databaseSystemIdentifier;
  }

  @Override
  public SchemaRetrievalOptions getSchemaRetrievalOptions()
  {
    return new SchemaRetrievalOptionsBuilder()
      .withDatabaseServerType(OfflineDatabaseConnector.DB_SERVER_TYPE)
      .toOptions();
  }

  @Override
  public void setSchemaRetrievalOptions(SchemaRetrievalOptions schemaRetrievalOptions)
  {
    // No-op
  }

  @Override
  public Connection getConnection()
  {
    return connection;
  }

  @Override
  public void setConnection(Connection connection)
  {
    this.connection = connection;
  }

}
