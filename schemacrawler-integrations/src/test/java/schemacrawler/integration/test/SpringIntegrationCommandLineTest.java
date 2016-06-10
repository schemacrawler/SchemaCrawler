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
package schemacrawler.integration.test;


import static org.junit.Assert.fail;
import static schemacrawler.test.utility.TestUtility.compareOutput;
import static schemacrawler.test.utility.TestUtility.copyResourceToTempFile;
import static schemacrawler.test.utility.TestUtility.flattenCommandlineArgs;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.tools.integration.spring.Main;
import schemacrawler.tools.options.TextOutputFormat;

public class SpringIntegrationCommandLineTest
  extends BaseDatabaseTest
{

  @Test
  public void springCommandLine()
    throws Exception
  {
    final List<String> failures = new ArrayList<>();

    final Path contextFile = copyResourceToTempFile("/context.xml");

    final String executableName = "executableForSchema";
    final Path testOutputFile = Paths.get("scOutput.txt");

    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("c", contextFile.toString());
    argsMap.put("x", executableName);

    Main.main(flattenCommandlineArgs(argsMap));

    failures
      .addAll(compareOutput(executableName + ".txt",
                            testOutputFile,
                            TextOutputFormat.text.getFormat()));
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

}
