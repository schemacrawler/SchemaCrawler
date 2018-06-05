package schemacrawler.tools.catalogloader;


import static java.util.Objects.requireNonNull;

import java.sql.Connection;

import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;

public class SchemaCrawlerCatalogLoader
  implements CatalogLoader
{

  private final String databaseSystemIdentifier;
  private SchemaRetrievalOptions schemaRetrievalOptions;
  private SchemaCrawlerOptions schemaCrawlerOptions;
  private Connection connection;
  private Config additionalConfiguration;

  public SchemaCrawlerCatalogLoader()
  {
    databaseSystemIdentifier = null;
  }

  protected SchemaCrawlerCatalogLoader(final String databaseSystemIdentifier)
  {
    this.databaseSystemIdentifier = requireNonNull(databaseSystemIdentifier,
                                                   "No database system identifier provided");
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
  public SchemaRetrievalOptions getSchemaRetrievalOptions()
  {
    if (schemaRetrievalOptions == null)
    {
      return new SchemaRetrievalOptionsBuilder().toOptions();
    }
    else
    {
      return schemaRetrievalOptions;
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
    requireNonNull(schemaRetrievalOptions,
                   "No database specific overrides provided");

    final SchemaCrawler schemaCrawler = new SchemaCrawler(connection,
                                                          schemaRetrievalOptions,
                                                          schemaCrawlerOptions);
    final Catalog catalog = schemaCrawler.crawl();

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
  public void setSchemaRetrievalOptions(final SchemaRetrievalOptions schemaRetrievalOptions)
  {
    this.schemaRetrievalOptions = schemaRetrievalOptions;
  }

  @Override
  public void setSchemaCrawlerOptions(final SchemaCrawlerOptions schemaCrawlerOptions)
  {
    this.schemaCrawlerOptions = schemaCrawlerOptions;
  }
}
