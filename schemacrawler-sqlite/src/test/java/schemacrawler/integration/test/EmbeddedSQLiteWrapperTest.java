/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.tools.command.text.schema.options.TextOutputFormat.text;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import schemacrawler.test.utility.BaseSqliteTest;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.tools.sqlite.EmbeddedSQLiteWrapper;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;
import us.fatehi.utility.IOUtility;

@DisableLogging
@ResolveTestContext
public class EmbeddedSQLiteWrapperTest extends BaseSqliteTest {

  @Test
  public void djangoExcluded(final TestContext testContext) throws Exception {
    weakAssociations(testContext, "/django_schema.sql");
  }

  private void weakAssociations(final TestContext testContext, final String databaseSqlResource)
      throws Exception {

    final String currentMethodFullName = testContext.testMethodFullName();

    // Create database from script, on disk
    final Path dbFile = IOUtility.createTempFilePath("test_sqlite_db", "");
    createDatabaseFromScript(createDataSourceFromFile(dbFile), databaseSqlResource);

    final EmbeddedSQLiteWrapper sqLiteDatabaseLoader = new EmbeddedSQLiteWrapper();
    sqLiteDatabaseLoader.setDatabasePath(dbFile);
    final Path outputFile = sqLiteDatabaseLoader.executeForOutput("Test Diagram Title", text);

    assertThat(outputOf(outputFile), hasSameContentAs(classpathResource(currentMethodFullName)));
  }
}
