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
import java.util.HashMap;
import java.util.Map;

import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;

public class ScriptTestUtility
{

  private static Map<String, String> additionalArgsMap()
  {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("-schemas", "((?!FOR_LINT).)*");
    argsMap.put("-info-level", "standard");
    return argsMap;
  }

  public static Path commandLineScriptExecution(final DatabaseConnectionInfo connectionInfo,
                                                final String script)
    throws Exception
  {
    return keyedCommandLineExecution(connectionInfo, "script", script);
  }

  public static Path commandLineTemplateExecution(final DatabaseConnectionInfo connectionInfo,
                                                  final String template)
    throws Exception
  {
    return keyedCommandLineExecution(connectionInfo, "template", template);
  }

  private static Path keyedCommandLineExecution(final DatabaseConnectionInfo connectionInfo,
                                                final String key,
                                                final String script)
    throws Exception
  {
    final Map<String, String> argsMap = additionalArgsMap();
    argsMap.put("-" + key, script);
    return commandlineExecution(connectionInfo, key, argsMap, "text");
  }

  private static Path keyedExecutableExecution(final Connection connection,
                                               final String key,
                                               final String script)
    throws Exception
  {
    final SchemaCrawlerExecutable executable = executableOf(key);
    final Config additionalConfiguration = new Config();
    additionalConfiguration.put(key, script);
    executable.setAdditionalConfiguration(additionalConfiguration);

    return executableExecution(connection, executable, "text");
  }

  public static Path scriptExecution(final Connection connection,
                                     final String script)
    throws Exception
  {
    return keyedExecutableExecution(connection, "script", script);
  }

  public static Path templateExecution(final Connection connection,
                                       final String template)
    throws Exception
  {
    return keyedExecutableExecution(connection, "template", template);
  }

}
