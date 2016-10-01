
/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static org.junit.Assert.fail;
import static schemacrawler.test.utility.TestUtility.compareOutput;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.tools.options.TextOutputFormat;

public class ExecutableExampleTest
  extends BaseDatabaseTest
{

  @Test
  public void executableExample()
    throws Exception
  {
    // Test
    final Path tempFile = Files.createTempFile("sc", ".out").toAbsolutePath();
    ExecutableExample.main(new String[] {
                                          tempFile.toString()
    });

    final List<String> failures = compareOutput("ExecutableExample.html", tempFile,
                                                TextOutputFormat.html.name());
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

}
