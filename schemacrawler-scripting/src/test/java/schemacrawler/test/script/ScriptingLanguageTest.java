/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.test.script;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.ExecutableTestUtility.executableOf;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.copyResourceToTempFile;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import schemacrawler.test.utility.AssertNoSystemOutOutput;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@AssertNoSystemOutOutput
@WithTestDatabase
public class ScriptingLanguageTest {

  private static Path executableScriptFromFile(
      final DatabaseConnectionSource dataSource, final String language, final Path scriptFile)
      throws Exception {

    final Config additionalConfig = new Config();
    additionalConfig.put("script", scriptFile.toString());
    additionalConfig.put("script-language", language);

    final SchemaCrawlerExecutable executable = executableOf("script");
    executable.setAdditionalConfiguration(additionalConfig);
    executable.setSchemaRetrievalOptions(schemaRetrievalOptionsDefault);

    return executableExecution(dataSource, executable, "text");
  }

  @Test
  public void executableGroovy(final DatabaseConnectionSource dataSource) throws Exception {
    final Path scriptFile = copyResourceToTempFile("/plaintextschema.groovy");
    assertThat(
        outputOf(executableScriptFromFile(dataSource, "groovy", scriptFile)),
        hasSameContentAs(classpathResource("script_output.txt")));
  }
}
