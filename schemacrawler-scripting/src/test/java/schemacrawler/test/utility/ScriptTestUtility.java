/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.utility;

import static schemacrawler.test.utility.CommandlineTestUtility.commandlineExecution;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.ExecutableTestUtility.executableOf;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.tools.command.template.options.TemplateLanguageType;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.ConfigUtility;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import us.fatehi.test.utility.DatabaseConnectionInfo;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

public class ScriptTestUtility {

  public static Path commandLineScriptExecution(
      final DatabaseConnectionInfo connectionInfo, final String script) throws Exception {
    final Map<String, String> argsMap = additionalArgsMap();
    argsMap.put("--script", script);
    // argsMap.put("--log-level", "ALL");
    return commandlineExecution(connectionInfo, "script", argsMap, "text");
  }

  public static Path commandLineTemplateExecution(
      final DatabaseConnectionInfo connectionInfo,
      final TemplateLanguageType templateLanguage,
      final String template)
      throws Exception {
    final Map<String, String> argsMap = additionalArgsMap();
    argsMap.put("--template", template);
    argsMap.put("--templating-language", templateLanguage.name());
    return commandlineExecution(connectionInfo, "template", argsMap, "text");
  }

  public static Path scriptExecution(final DatabaseConnectionSource dataSource, final String script)
      throws Exception {

    final SchemaRetrievalOptions schemaRetrievalOptions =
        SchemaRetrievalOptionsBuilder.newSchemaRetrievalOptions();

    final OutputOptions outputOptions =
        OutputOptionsBuilder.builder().title("FROM TEST: Database Schema Diagram").toOptions();

    final Config additionalConfig = ConfigUtility.newConfig();
    additionalConfig.put("script", script);

    final SchemaCrawlerExecutable executable = executableOf("script");
    executable.setAdditionalConfiguration(additionalConfig);
    executable.setSchemaRetrievalOptions(schemaRetrievalOptions);
    executable.setOutputOptions(outputOptions);

    return executableExecution(dataSource, executable, "text");
  }

  public static Path templateExecution(
      final DatabaseConnectionSource dataSource,
      final TemplateLanguageType templateLanguage,
      final String templateResource)
      throws Exception {

    final Config additionalConfig = ConfigUtility.newConfig();
    additionalConfig.put("template", templateResource);
    additionalConfig.put("templating-language", templateLanguage.name());

    final SchemaCrawlerExecutable executable = executableOf("template");
    executable.setAdditionalConfiguration(additionalConfig);
    executable.setSchemaRetrievalOptions(schemaRetrievalOptionsDefault);

    return executableExecution(dataSource, executable, "text");
  }

  private static Map<String, String> additionalArgsMap() {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("--schemas", "((?!FOR_LINT).)*");
    argsMap.put("--info-level", "standard");
    return argsMap;
  }
}
