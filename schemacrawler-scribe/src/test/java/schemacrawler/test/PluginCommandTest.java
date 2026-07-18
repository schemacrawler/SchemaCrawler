/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import org.junit.jupiter.api.Test;
import schemacrawler.scribe.command.ScribeCommandProvider;
import schemacrawler.test.utility.PluginCommandTestUtility;
import schemacrawler.tools.executable.commandline.PluginCommand;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;

@ResolveTestContext
public class PluginCommandTest {

  @Test
  public void testScribeCommandProviderHelpCommand(final TestContext testContext) {
    final PluginCommand pluginCommand = new ScribeCommandProvider().getHelpCommand();
    PluginCommandTestUtility.testPluginCommand(testContext, pluginCommand);
  }

  @Test
  public void testScribeCommandProviderPluginCommand(final TestContext testContext) {
    final PluginCommand pluginCommand = new ScribeCommandProvider().getCommandLineCommand();
    PluginCommandTestUtility.testPluginCommand(testContext, pluginCommand);
  }
}
