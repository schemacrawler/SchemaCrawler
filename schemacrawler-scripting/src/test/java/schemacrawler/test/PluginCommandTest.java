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
import schemacrawler.tools.command.script.ScriptCommandProvider;
import schemacrawler.tools.command.serialize.SerializationCommandProvider;
import schemacrawler.tools.command.template.TemplateCommandProvider;
import schemacrawler.tools.executable.commandline.PluginCommand;

@ResolveTestContext
public class PluginCommandTest {

  @Test
  public void testScriptCommandProviderPluginCommand(final TestContext testContext) {
    final PluginCommand pluginCommand = new ScriptCommandProvider().getCommandLineCommand();
    PluginCommandTestUtility.testPluginCommand(testContext, pluginCommand);
  }

  @Test
  public void testScriptCommandProviderHelpCommand(final TestContext testContext) {
    final PluginCommand pluginCommand = new ScriptCommandProvider().getHelpCommand();
    PluginCommandTestUtility.testPluginCommand(testContext, pluginCommand);
  }

  @Test
  public void testSerializationCommandProviderPluginCommand(final TestContext testContext) {
    final PluginCommand pluginCommand = new SerializationCommandProvider().getCommandLineCommand();
    PluginCommandTestUtility.testPluginCommand(testContext, pluginCommand);
  }

  @Test
  public void testSerializationCommandProviderHelpCommand(final TestContext testContext) {
    final PluginCommand pluginCommand = new SerializationCommandProvider().getHelpCommand();
    PluginCommandTestUtility.testPluginCommand(testContext, pluginCommand);
  }

  @Test
  public void testTemplateCommandProviderPluginCommand(final TestContext testContext) {
    final PluginCommand pluginCommand = new TemplateCommandProvider().getCommandLineCommand();
    PluginCommandTestUtility.testPluginCommand(testContext, pluginCommand);
  }

  @Test
  public void testTemplateCommandProviderHelpCommand(final TestContext testContext) {
    final PluginCommand pluginCommand = new TemplateCommandProvider().getHelpCommand();
    PluginCommandTestUtility.testPluginCommand(testContext, pluginCommand);
  }
}
