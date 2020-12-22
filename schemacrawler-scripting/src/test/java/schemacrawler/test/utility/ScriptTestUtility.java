package schemacrawler.test.utility;

/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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

import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.integration.template.TemplateLanguageType;
import schemacrawler.tools.options.Config;

public class ScriptTestUtility {

  public static Path commandLineScriptExecution(
      final DatabaseConnectionInfo connectionInfo, final String script) throws Exception {
    final Map<String, String> argsMap = additionalArgsMap();
    argsMap.put("-" + "script", script);
    return commandlineExecution(connectionInfo, "script", argsMap, "text");
  }

  public static Path commandLineTemplateExecution(
      final DatabaseConnectionInfo connectionInfo,
      final TemplateLanguageType templateLanguage,
      final String template)
      throws Exception {
    final Map<String, String> argsMap = additionalArgsMap();
    argsMap.put("-template", template);
    argsMap.put("-templating-language", templateLanguage.name());
    return commandlineExecution(connectionInfo, "template", argsMap, "text");
  }

  public static Path scriptExecution(final Connection connection, final String script)
      throws Exception {
    final SchemaCrawlerExecutable executable = executableOf("script");
    final Config additionalConfiguration = new Config();
    additionalConfiguration.put("script", script);
    executable.setAdditionalConfiguration(additionalConfiguration);

    return executableExecution(connection, executable, "text");
  }

  public static Path templateExecution(
      final Connection connection,
      final TemplateLanguageType templateLanguage,
      final String templateResource)
      throws Exception {
    final SchemaCrawlerExecutable executable = executableOf("template");
    final Config additionalConfiguration = new Config();
    additionalConfiguration.put("template", templateResource);
    additionalConfiguration.put("templating-language", templateLanguage.name());
    executable.setAdditionalConfiguration(additionalConfiguration);

    return executableExecution(connection, executable, "text");
  }

  private static Map<String, String> additionalArgsMap() {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("-schemas", "((?!FOR_LINT).)*");
    argsMap.put("-info-level", "standard");
    return argsMap;
  }
}
