/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.integration.test;

import static java.nio.file.Files.move;
import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import schemacrawler.test.utility.BaseSqliteTest;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat;
import schemacrawler.tools.sqlite.SchemaCrawlerSQLiteUtility;
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
