package schemacrawler.test.utility;

import static org.mockito.Mockito.mock;

import java.sql.Connection;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.tools.catalogloader.CatalogLoader;

public class TestCatalogLoader implements CatalogLoader {

  private Connection connection;
  private SchemaCrawlerOptions schemaCrawlerOptions;
  private SchemaRetrievalOptions schemaRetrievalOptions;

  @Override
  public Connection getConnection() {
    return connection;
  }

  @Override
  public String getDatabaseSystemIdentifier() {
    return "test-db";
  }

  @Override
  public SchemaCrawlerOptions getSchemaCrawlerOptions() {
    return schemaCrawlerOptions;
  }

  @Override
  public SchemaRetrievalOptions getSchemaRetrievalOptions() {
    return schemaRetrievalOptions;
  }

  @Override
  public Catalog loadCatalog() throws Exception {
    return mock(Catalog.class);
  }

  @Override
  public void setConnection(final Connection connection) {
    this.connection = connection;
  }

  @Override
  public void setSchemaCrawlerOptions(final SchemaCrawlerOptions schemaCrawlerOptions) {
    this.schemaCrawlerOptions = schemaCrawlerOptions;
  }

  @Override
  public void setSchemaRetrievalOptions(final SchemaRetrievalOptions schemaRetrievalOptions) {
    this.schemaRetrievalOptions = schemaRetrievalOptions;
  }
}
