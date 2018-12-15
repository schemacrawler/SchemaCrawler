package schemacrawler.server.oracle;


import static java.util.Objects.requireNonNull;
import static sf.util.DatabaseUtility.executeScriptFromResource;

import java.sql.Connection;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.catalogloader.SchemaCrawlerCatalogLoader;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;

public final class OracleCatalogLoader
  extends SchemaCrawlerCatalogLoader
{

  public OracleCatalogLoader()
  {
    super(OracleDatabaseConnector.DB_SERVER_TYPE.getDatabaseSystemIdentifier());
  }

  @Override
  public Catalog loadCatalog()
    throws Exception
  {
    final Connection connection = getConnection();
    requireNonNull(connection, "No connection provided");

    executeOracleScripts(connection);

    return super.loadCatalog();
  }

  private void executeOracleScripts(final Connection connection)
  {
    executeScriptFromResource(connection, "/schemacrawler-oracle.before.sql");

    final Config additionalConfiguration = getAdditionalConfiguration();
    final SchemaTextOptions schemaTextOptions = SchemaTextOptionsBuilder
      .builder().fromConfig(additionalConfiguration).toOptions();
    if (schemaTextOptions.isShowUnqualifiedNames())
    {
      executeScriptFromResource(connection,
                                "/schemacrawler-oracle.show_unqualified_names.sql");
    }
  }

}
