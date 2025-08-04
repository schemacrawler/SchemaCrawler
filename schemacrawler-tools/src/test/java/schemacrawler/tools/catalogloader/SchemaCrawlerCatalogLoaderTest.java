/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.catalogloader;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;

import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import us.fatehi.test.utility.TestDatabaseDriver;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSourceUtility;

public class SchemaCrawlerCatalogLoaderTest {

  @Test
  public void connection() throws SQLException {
    final CatalogLoader catalogLoader = new SchemaCrawlerCatalogLoader();

    assertThat(catalogLoader.getDataSource(), is(nullValue()));

    final Connection connection = new TestDatabaseDriver().connect("jdbc:test-db:test", null);
    final DatabaseConnectionSource dataSource =
        DatabaseConnectionSourceUtility.newTestDatabaseConnectionSource(connection);
    catalogLoader.setDataSource(dataSource);

    assertThat(catalogLoader.getDataSource(), is(not(nullValue())));

    connection.close();
  }

  @Test
  public void schemaCrawlerOptions() {
    final CatalogLoader catalogLoader = new SchemaCrawlerCatalogLoader();

    assertThat(catalogLoader.getSchemaCrawlerOptions(), is(not(nullValue())));

    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions();
    catalogLoader.setSchemaCrawlerOptions(schemaCrawlerOptions);

    assertThat(catalogLoader.getSchemaCrawlerOptions(), is(not(nullValue())));
    assertThat(catalogLoader.getSchemaCrawlerOptions().equals(schemaCrawlerOptions), is(true));
  }

  @Test
  public void schemaRetrievalOptions() {
    final CatalogLoader catalogLoader = new SchemaCrawlerCatalogLoader();

    assertThat(catalogLoader.getSchemaRetrievalOptions(), is(not(nullValue())));

    catalogLoader.setSchemaRetrievalOptions(schemaRetrievalOptionsDefault);

    assertThat(catalogLoader.getSchemaRetrievalOptions(), is(not(nullValue())));
    assertThat(
        catalogLoader.getSchemaRetrievalOptions().equals(schemaRetrievalOptionsDefault), is(true));
  }
}
