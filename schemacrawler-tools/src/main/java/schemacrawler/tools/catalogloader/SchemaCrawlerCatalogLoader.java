package schemacrawler.tools.catalogloader;


import static java.util.Objects.requireNonNull;

import java.sql.Connection;

import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptions;
import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.options.OutputOptions;

public final class SchemaCrawlerCatalogLoader
  implements CatalogLoader
{

  private final String databaseSystemIdentifier;
  private DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions;
  private SchemaCrawlerOptions schemaCrawlerOptions;
  private Connection connection;
  private Config additionalConfiguration;

  public SchemaCrawlerCatalogLoader()
  {
    databaseSystemIdentifier = "default";
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
  public DatabaseSpecificOverrideOptions getDatabaseSpecificOverrideOptions()
  {
    if (databaseSpecificOverrideOptions == null)
    {
      return new DatabaseSpecificOverrideOptionsBuilder().toOptions();
    }
    else
    {
      return databaseSpecificOverrideOptions;
    }
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
      return new SchemaCrawlerOptions();
    }
    else
    {
      return schemaCrawlerOptions;
    }
  }

  @Override
  public Catalog loadCatalog()
    throws Exception
  {
    requireNonNull(connection, "No connection provided");
    requireNonNull(databaseSpecificOverrideOptions,
                   "No database specific overrides provided");

    final SchemaCrawler schemaCrawler = new SchemaCrawler(connection,
                                                          databaseSpecificOverrideOptions);
    final Catalog catalog = schemaCrawler.crawl(schemaCrawlerOptions);

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
  public void setDatabaseSpecificOverrideOptions(final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions)
  {
    this.databaseSpecificOverrideOptions = databaseSpecificOverrideOptions;
  }

  @Override
  public void setSchemaCrawlerOptions(final SchemaCrawlerOptions schemaCrawlerOptions)
  {
    this.schemaCrawlerOptions = schemaCrawlerOptions;
  }
}
