
/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.fileResource;
import static schemacrawler.test.utility.FileHasContent.hasNoContent;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.TestUtility.compareOutput;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import schemacrawler.test.utility.BaseSchemaCrawlerTest;
import schemacrawler.test.utility.TestOutputStream;
import schemacrawler.testdb.TestDatabase;
import schemacrawler.tools.options.TextOutputFormat;

public class ExampleTest
  extends BaseSchemaCrawlerTest
{

  private static TestDatabase testDatabase;

  @BeforeAll
  public static void startDatabase()
  {
    testDatabase = TestDatabase.startDefaultTestDatabase(false);
  }

  @AfterAll
  public static void stopDatabase()
  {
    testDatabase.stop();
  }

  private TestOutputStream out;
  private TestOutputStream err;

  @Test
  public void apiExample()
    throws Exception
  {
    ApiExample.main(new String[0]);

    assertThat(fileResource(out),
               hasSameContentAs(classpathResource("ApiExample.txt")));
    assertThat(fileResource(err), hasNoContent());
  }

  @AfterEach
  public void cleanUpStreams()
  {
    System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
    System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
  }

  @Test
  public void executableExample()
    throws Exception
  {
    // Test
    final Path tempFile = Files.createTempFile("sc", ".out").toAbsolutePath();
    ExecutableExample.main(new String[] { tempFile.toString() });

    final List<String> failures = compareOutput("ExecutableExample.html",
                                                tempFile,
                                                TextOutputFormat.html.name());
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

  @Test
  public void resultSetExample()
    throws Exception
  {
    ResultSetExample.main(new String[0]);

    assertThat(fileResource(out),
               hasSameContentAs(classpathResource("ResultSetExample.txt")));
    assertThat(fileResource(err), hasNoContent());
  }

  @BeforeEach
  public void setUpStreams()
    throws Exception
  {
    out = new TestOutputStream();
    System.setOut(new PrintStream(out));

    err = new TestOutputStream();
    System.setErr(new PrintStream(err));
  }

}
