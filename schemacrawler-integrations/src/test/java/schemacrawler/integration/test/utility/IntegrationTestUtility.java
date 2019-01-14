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

package schemacrawler.integration.test.utility;


import static schemacrawler.test.utility.TestUtility.flattenCommandlineArgs;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import schemacrawler.Main;
import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.TestWriter;

public final class IntegrationTestUtility
{

  public static Path commandLineExecution(final DatabaseConnectionInfo connectionInfo,
                                          final String command,
                                          final String outputFormatValue)
    throws Exception
  {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout;)
    {
      final Map<String, String> argsMap = new HashMap<>();
      argsMap.put("url", connectionInfo.getConnectionUrl());
      argsMap.put("user", "sa");
      argsMap.put("password", "");
      argsMap.put("infolevel", "standard");
      argsMap.put("command", command);
      argsMap.put("schemas", "((?!FOR_LINT).)*");
      argsMap.put("outputformat", outputFormatValue);
      argsMap.put("outputfile", out.toString());

      Main.main(flattenCommandlineArgs(argsMap));
    }
    return testout.getFilePath();
  }

  private IntegrationTestUtility()
  {
    // Prevent instantiation
  }

}
