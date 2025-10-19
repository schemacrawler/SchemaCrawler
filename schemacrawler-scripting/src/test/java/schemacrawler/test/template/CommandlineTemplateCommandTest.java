/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.template;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.ScriptTestUtility.commandLineTemplateExecution;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import org.junit.jupiter.api.Test;
import schemacrawler.test.utility.WithTestDatabase;
import schemacrawler.tools.command.template.options.TemplateLanguageType;
import us.fatehi.test.utility.DatabaseConnectionInfo;
import us.fatehi.test.utility.extensions.AssertNoSystemErrOutput;
import us.fatehi.test.utility.extensions.AssertNoSystemOutOutput;

@AssertNoSystemErrOutput
@AssertNoSystemOutOutput
@WithTestDatabase
public class CommandlineTemplateCommandTest {

  @Test
  public void commandlineFreeMarker(final DatabaseConnectionInfo connectionInfo) throws Exception {
    assertThat(
        outputOf(
            commandLineTemplateExecution(
                connectionInfo, TemplateLanguageType.freemarker, "/plaintextschema.ftl")),
        hasSameContentAs(classpathResource("executableForFreeMarker.txt")));
  }

  @Test
  public void commandlineMustache(final DatabaseConnectionInfo connectionInfo) throws Exception {
    assertThat(
        outputOf(
            commandLineTemplateExecution(
                connectionInfo, TemplateLanguageType.mustache, "/plaintextschema.mustache")),
        hasSameContentAs(classpathResource("executableForMustache.txt")));
  }

  @Test
  public void commandlineThymeleaf(final DatabaseConnectionInfo connectionInfo) throws Exception {
    assertThat(
        outputOf(
            commandLineTemplateExecution(
                connectionInfo, TemplateLanguageType.thymeleaf, "/plaintextschema.thymeleaf")),
        hasSameContentAs(classpathResource("executableForThymeleaf.txt")));
  }

  @Test
  public void commandlineVelocity(final DatabaseConnectionInfo connectionInfo) throws Exception {
    assertThat(
        outputOf(
            commandLineTemplateExecution(
                connectionInfo, TemplateLanguageType.velocity, "/plaintextschema.vm")),
        hasSameContentAs(classpathResource("executableForVelocity.txt")));
  }
}
