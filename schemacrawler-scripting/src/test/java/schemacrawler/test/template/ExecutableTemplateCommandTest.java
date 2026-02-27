/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.template;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.ScriptTestUtility.templateExecution;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import org.junit.jupiter.api.Test;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.template.options.TemplateLanguageType;
import us.fatehi.test.utility.extensions.AssertNoSystemErrOutput;
import us.fatehi.test.utility.extensions.AssertNoSystemOutOutput;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@AssertNoSystemErrOutput
@AssertNoSystemOutOutput
@WithTestDatabase
public class ExecutableTemplateCommandTest {

  @Test
  public void executableFreeMarker(final DatabaseConnectionSource connectionSource)
      throws Exception {
    assertThat(
        outputOf(
            templateExecution(
                connectionSource, TemplateLanguageType.freemarker, "/plaintextschema.ftl")),
        hasSameContentAs(classpathResource("executableForFreeMarker.txt")));
  }

  @Test
  public void executableMustache(final DatabaseConnectionSource connectionSource) throws Exception {
    assertThat(
        outputOf(
            templateExecution(
                connectionSource, TemplateLanguageType.mustache, "/plaintextschema.mustache")),
        hasSameContentAs(classpathResource("executableForMustache.txt")));
  }

  @Test
  public void executableThymeleaf(final DatabaseConnectionSource connectionSource)
      throws Exception {
    assertThat(
        outputOf(
            templateExecution(
                connectionSource, TemplateLanguageType.thymeleaf, "/plaintextschema.thymeleaf")),
        hasSameContentAs(classpathResource("executableForThymeleaf.txt")));
  }

  @Test
  public void executableVelocity(final DatabaseConnectionSource connectionSource) throws Exception {
    assertThat(
        outputOf(
            templateExecution(
                connectionSource, TemplateLanguageType.velocity, "/plaintextschema.vm")),
        hasSameContentAs(classpathResource("executableForVelocity.txt")));
  }
}
