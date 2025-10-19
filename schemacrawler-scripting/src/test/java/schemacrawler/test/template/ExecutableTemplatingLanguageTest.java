/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.template;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.DatabaseTestUtility.schemaRetrievalOptionsDefault;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.ExecutableTestUtility.executableOf;
import static us.fatehi.test.utility.TestUtility.copyResourceToTempFile;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import us.fatehi.test.utility.extensions.AssertNoSystemErrOutput;
import us.fatehi.test.utility.extensions.AssertNoSystemOutOutput;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@AssertNoSystemErrOutput
@AssertNoSystemOutOutput
@WithTestDatabase
public class ExecutableTemplatingLanguageTest {

  private static Path executableTemplateFromFile(
      final DatabaseConnectionSource dataSource, final String language, final Path scriptFile)
      throws Exception {

    final Config additionalConfig = new Config();
    additionalConfig.put("template", scriptFile.toString());
    additionalConfig.put("templating-language", language);

    final SchemaCrawlerExecutable executable = executableOf("template");
    executable.setAdditionalConfiguration(additionalConfig);
    executable.setSchemaRetrievalOptions(schemaRetrievalOptionsDefault);

    return executableExecution(dataSource, executable, "text");
  }

  @Test
  public void executableVelocity(final DatabaseConnectionSource dataSource) throws Exception {
    final Path scriptFile = copyResourceToTempFile("/plaintextschema.vm");
    assertThat(
        outputOf(executableTemplateFromFile(dataSource, "velocity", scriptFile)),
        hasSameContentAs(classpathResource("executableForVelocity.txt")));
  }
}
