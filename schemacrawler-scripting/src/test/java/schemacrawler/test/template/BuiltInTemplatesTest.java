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
import static schemacrawler.tools.command.template.options.TemplateLanguageType.velocity;
import static us.fatehi.test.utility.extensions.FileHasContent.classpathResource;
import static us.fatehi.test.utility.extensions.FileHasContent.hasSameContentAs;
import static us.fatehi.test.utility.extensions.FileHasContent.outputOf;

import org.junit.jupiter.api.Test;
import schemacrawler.test.utility.DisableLogging;
import schemacrawler.test.utility.WithTestDatabase;
import us.fatehi.test.utility.extensions.AssertNoSystemOutOutput;
import us.fatehi.utility.datasource.DatabaseConnectionSource;

@AssertNoSystemOutOutput
@WithTestDatabase
@DisableLogging
public class BuiltInTemplatesTest {

  @Test
  public void dbml(final DatabaseConnectionSource connectionSource) throws Exception {
    assertThat(
        outputOf(templateExecution(connectionSource, velocity, "/templates/dbml.vm")),
        hasSameContentAs(classpathResource("BuiltIn.dbml.txt")));
  }

  @Test
  public void markdown(final DatabaseConnectionSource connectionSource) throws Exception {
    assertThat(
        outputOf(templateExecution(connectionSource, velocity, "/templates/markdown.vm")),
        hasSameContentAs(classpathResource("BuiltIn.markdown.txt")));
  }

  @Test
  public void mermaid(final DatabaseConnectionSource connectionSource) throws Exception {
    assertThat(
        outputOf(templateExecution(connectionSource, velocity, "/templates/mermaid.vm")),
        hasSameContentAs(classpathResource("BuiltIn.mermaid.txt")));
  }

  @Test
  public void plantuml(final DatabaseConnectionSource connectionSource) throws Exception {
    assertThat(
        outputOf(templateExecution(connectionSource, velocity, "/templates/plantuml.vm")),
        hasSameContentAs(classpathResource("BuiltIn.plantuml.txt")));
  }

  @Test
  public void quickdbd(final DatabaseConnectionSource connectionSource) throws Exception {
    assertThat(
        outputOf(templateExecution(connectionSource, velocity, "/templates/quickdbd.vm")),
        hasSameContentAs(classpathResource("BuiltIn.quickdbd.txt")));
  }
}
