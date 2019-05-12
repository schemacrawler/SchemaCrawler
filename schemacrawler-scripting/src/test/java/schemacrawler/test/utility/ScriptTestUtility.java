package schemacrawler.test.utility;


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

import static schemacrawler.test.utility.CommandlineTestUtility.commandlineExecution;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.ExecutableTestUtility.executableOf;

import java.nio.file.Path;
import java.sql.Connection;
import java.util.Map;

import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;

public class ScriptTestUtility
{

  public static Path executableScript(final Connection connection,
                                      final String scriptResource)
    throws Exception
  {
    final SchemaCrawlerExecutable executable = executableOf("script");
    final Config additionalConfiguration = new Config();
    additionalConfiguration.put("script:resource", scriptResource);
    executable.setAdditionalConfiguration(additionalConfiguration);

    return executableExecution(connection, executable, "text");
  }

  public static Path executeScriptCommandLine(final DatabaseConnectionInfo connectionInfo,
                                              final Map<String, String> argsMap,
                                              final String scriptResource)
    throws Exception
  {
    argsMap.put("-script:resource", scriptResource);
    return commandlineExecution(connectionInfo, "script", argsMap, "text");
  }

}
