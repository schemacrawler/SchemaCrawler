/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test;

import static java.nio.file.Files.move;
import static org.hamcrest.MatcherAssert.assertThat;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import schemacrawler.test.utility.BaseSqliteTest;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat;
import schemacrawler.tools.sqlite.SchemaCrawlerSQLiteUtility;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;
import us.fatehi.utility.IOUtility;

@DisableLogging
@ResolveTestContext
public class SQLiteDiagramTest extends BaseSqliteTest {

  @Test
  public void utility(final TestContext testContext) throws Exception {
    final Path sqliteDbFile = createTestDatabase();
    final Path sqliteDiagramTempFile =
        IOUtility.createTempFilePath("sc", ".scdot").normalize().toAbsolutePath();

    final Path schemaCrawlerDiagramFile =
        SchemaCrawlerSQLiteUtility.executeForOutput(
            sqliteDbFile, "Diagram Title", DiagramOutputFormat.scdot);
    move(schemaCrawlerDiagramFile, sqliteDiagramTempFile);

    assertThat(
        outputOf(sqliteDiagramTempFile),
        hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }
}
