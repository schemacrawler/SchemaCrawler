package schemacrawler.tools.offline;


import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.util.logging.Level;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptions;
import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.catalogloader.CatalogLoader;
import schemacrawler.tools.integration.serialization.XmlSerializedCatalog;
import schemacrawler.tools.offline.jdbc.OfflineConnection;
import schemacrawler.tools.options.OutputOptions;
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

    final OutputOptions inputOptions = new OutputOptions(additionalConfiguration);
    inputOptions.setCompressedInputFile(((OfflineConnection) connection)
      .getOfflineDatabasePath());

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
    return new SchemaCrawlerOptions();
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
  public DatabaseSpecificOverrideOptions getDatabaseSpecificOverrideOptions()
  {
    return new DatabaseSpecificOverrideOptionsBuilder()
      .withDatabaseServerType(OfflineDatabaseConnector.DB_SERVER_TYPE)
      .toOptions();
  }

  @Override
  public void setDatabaseSpecificOverrideOptions(DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions)
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
