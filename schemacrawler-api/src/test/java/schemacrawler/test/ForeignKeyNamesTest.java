/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static schemacrawler.crawl.ForeignKeyRetrieverTest.verifyRetrieveForeignKeys;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;

import java.io.IOException;
import java.sql.Connection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.test.utility.WithTestDatabase;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;

@ResolveTestContext
@WithTestDatabase(script = "/fk_dupe_name.sql")
public class ForeignKeyNamesTest {

  private Catalog catalog;

  @Test
  @DisplayName("Verify that foreign key names are scoped within a schema")
  public void fkNames(final TestContext testContext) throws IOException {
    verifyRetrieveForeignKeys(catalog, testContext.testMethodFullName());
  }

  @BeforeEach
  public void loadCatalog(final Connection connection) throws Exception {

    final LoadOptionsBuilder loadOptionsBuilder =
        LoadOptionsBuilder.builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum());
    final SchemaCrawlerOptions schemaCrawlerOptions =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLoadOptions(loadOptionsBuilder.toOptions());

    catalog = getCatalog(connection, schemaCrawlerOptions);
  }
}
