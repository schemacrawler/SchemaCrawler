package schemacrawler.test.utility;


import static org.mockito.Mockito.mock;

import java.sql.Connection;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.tools.catalogloader.CatalogLoader;
import schemacrawler.tools.options.Config;

public class TestCatalogLoader
  implements CatalogLoader
{

  private Config additionalConfiguration;
  private Connection connection;
  private SchemaCrawlerOptions schemaCrawlerOptions;
  private SchemaRetrievalOptions schemaRetrievalOptions;

  @Override
  public Config getAdditionalConfiguration()
  {
    return additionalConfiguration;
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
    return "test-db";
  }

  @Override
  public SchemaCrawlerOptions getSchemaCrawlerOptions()
  {
    return schemaCrawlerOptions;
  }

  @Override
  public void setSchemaCrawlerOptions(final SchemaCrawlerOptions schemaCrawlerOptions)
  {
    this.schemaCrawlerOptions = schemaCrawlerOptions;
  }

  @Override
  public SchemaRetrievalOptions getSchemaRetrievalOptions()
  {
    return schemaRetrievalOptions;
  }

  @Override
  public void setSchemaRetrievalOptions(final SchemaRetrievalOptions schemaRetrievalOptions)
  {
    this.schemaRetrievalOptions = schemaRetrievalOptions;
  }

  @Override
  public Catalog loadCatalog()
    throws Exception
  {
    return mock(Catalog.class);
  }

}
