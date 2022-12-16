/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package schemacrawler.test;

import static schemacrawler.crawl.ForeignKeyRetrieverTest.verifyRetrieveForeignKeys;
import static schemacrawler.test.utility.DatabaseTestUtility.getCatalog;

import java.io.IOException;
import java.sql.Connection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Description;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.WithTestDatabase;

@ResolveTestContext
@WithTestDatabase(script = "fk_dupe_name.sql")
public class ForeignKeyNamesTest {

  private Catalog catalog;

  @Test
  @Description("Verify that foreign key names are scoped within a schema")
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
