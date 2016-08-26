
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
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.tools.options.TextOutputFormat;

public class ApiExampleTest
  extends BaseDatabaseTest
{

  @Rule
  public final SystemOutRule sysOutRule = new SystemOutRule().enableLog();

  @Test
  public void apiExample()
    throws Exception
  {
    // Test
    ApiExample.main(new String[0]);

    // Write output
    final Path tempFile = Files.createTempFile("sc", ".txt");
    Files.write(tempFile,
                Arrays.asList(sysOutRule.getLogWithNormalizedLineSeparator()),
                StandardOpenOption.WRITE);

    final List<String> failures = compareOutput("ApiExample.txt",
                                                tempFile,
                                                TextOutputFormat.text.name());
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

}
