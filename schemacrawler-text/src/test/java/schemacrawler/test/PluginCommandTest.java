/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import org.junit.jupiter.api.Test;
import schemacrawler.test.utility.PluginCommandTestUtility;
import schemacrawler.tools.command.text.schema.SchemaTextCommandProvider;
import schemacrawler.tools.executable.commandline.PluginCommand;
import us.fatehi.test.utility.extensions.ResolveTestContext;
import us.fatehi.test.utility.extensions.TestContext;

@ResolveTestContext
public class PluginCommandTest {

  @Test
  public void testSchemaTextCommandProviderPluginCommand(final TestContext testContext) {
    final PluginCommand pluginCommand = new SchemaTextCommandProvider().getCommandLineCommand();
    PluginCommandTestUtility.testPluginCommand(testContext, pluginCommand);
  }

  @Test
  public void testSchemaTextCommandProviderHelpCommand(final TestContext testContext) {
    final PluginCommand pluginCommand = new SchemaTextCommandProvider().getHelpCommand();
    PluginCommandTestUtility.testPluginCommand(testContext, pluginCommand);
  }
}
