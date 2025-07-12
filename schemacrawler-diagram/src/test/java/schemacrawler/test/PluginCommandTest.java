/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import org.junit.jupiter.api.Test;
import schemacrawler.test.utility.PluginCommandTestUtility;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.tools.command.text.diagram.DiagramCommandProvider;
import schemacrawler.tools.command.text.embeddeddiagram.EmbeddedDiagramCommandProvider;
import schemacrawler.tools.executable.commandline.PluginCommand;

@ResolveTestContext
public class PluginCommandTest {

  @Test
  public void testDiagramCommandProviderPluginCommand(final TestContext testContext) {
    final PluginCommand pluginCommand = new DiagramCommandProvider().getCommandLineCommand();
    PluginCommandTestUtility.testPluginCommand(testContext, pluginCommand);
  }

  @Test
  public void testDiagramCommandProviderHelpCommand(final TestContext testContext) {
    final PluginCommand pluginCommand = new DiagramCommandProvider().getHelpCommand();
    PluginCommandTestUtility.testPluginCommand(testContext, pluginCommand);
  }

  @Test
  public void testEmbeddedDiagramCommandProviderPluginCommand(final TestContext testContext) {
    final PluginCommand pluginCommand =
        new EmbeddedDiagramCommandProvider().getCommandLineCommand();
    PluginCommandTestUtility.testPluginCommand(testContext, pluginCommand);
  }

  @Test
  public void testEmbeddedDiagramCommandProviderHelpCommand(final TestContext testContext) {
    final PluginCommand pluginCommand = new EmbeddedDiagramCommandProvider().getHelpCommand();
    PluginCommandTestUtility.testPluginCommand(testContext, pluginCommand);
  }
}
