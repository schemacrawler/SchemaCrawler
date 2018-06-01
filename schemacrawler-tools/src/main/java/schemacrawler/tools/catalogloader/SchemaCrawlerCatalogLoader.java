package schemacrawler.tools.catalogloader;

import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptions;
import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.options.OutputOptions;
import sf.util.SchemaCrawlerLogger;

import java.sql.Connection;

import static java.util.Objects.requireNonNull;

public final class SchemaCrawlerCatalogLoader
    implements CatalogLoader
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
      .getLogger(SchemaCrawlerCatalogLoader.class.getName());

  private final String databaseSystemIdentifier;
  private DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions;
  private SchemaCrawlerOptions schemaCrawlerOptions;
  private OutputOptions outputOptions;
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
  public void setAdditionalConfiguration(Config additionalConfiguration)
  {
    this.additionalConfiguration = additionalConfiguration;
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
  public void setSchemaCrawlerOptions(SchemaCrawlerOptions schemaCrawlerOptions)
  {
    this.schemaCrawlerOptions = schemaCrawlerOptions;
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
  public String getDatabaseSystemIdentifier()
  {
    return databaseSystemIdentifier;
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
  public void setDatabaseSpecificOverrideOptions(DatabaseSpecificOverrideOptions
                                                     databaseSpecificOverrideOptions)
  {
    this.databaseSpecificOverrideOptions = databaseSpecificOverrideOptions;
  }

  @Override
  public OutputOptions getOutputOptions()
  {
    if (outputOptions == null)
    {
      return new OutputOptions();
    }
    else
    {
      return outputOptions;
    }
  }

  @Override
  public void setOutputOptions(OutputOptions outputOptions)
  {
    this.outputOptions = outputOptions;
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
