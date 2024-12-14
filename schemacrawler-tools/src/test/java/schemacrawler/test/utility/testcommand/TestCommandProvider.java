/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.test.utility.testcommand;

import static schemacrawler.test.utility.testcommand.TestCommand.COMMAND;
import static schemacrawler.tools.executable.commandline.PluginCommand.newPluginCommand;
import schemacrawler.tools.executable.BaseCommandProvider;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;

public class TestCommandProvider extends BaseCommandProvider {

  public TestCommandProvider() {
    super(COMMAND);
    forceInstantiationFailureIfConfigured();
  }

  @Override
  public PluginCommand getCommandLineCommand() {
    final PluginCommand pluginCommand = newPluginCommand(COMMAND);
    pluginCommand.addOption("test-command-parameter", String.class, "Parameter for test command");
    return pluginCommand;
  }

  @Override
  public TestCommand newSchemaCrawlerCommand(final String command, final Config config) {
    final String testCommandParameter = config.getStringValue("test-command-parameter", "");

    final boolean throwRuntimeException = config.getBooleanValue("throw-runtime-exception");
    final boolean returnNull = config.getBooleanValue("return-null");
    final boolean usesConnection = config.getBooleanValue("uses-connection", true);

    if (returnNull) {
      return null;
    }
    if (throwRuntimeException) {
      throw new RuntimeException("Request throw during command initialization");
    }

    final TestOptions commandOptions = new TestOptions(usesConnection, testCommandParameter);
    final TestCommand testCommand = new TestCommand();
    testCommand.configure(commandOptions);
    return testCommand;
  }

  @Override
  public boolean supportsOutputFormat(final String command, final OutputOptions outputOptions) {
    if (outputOptions == null) {
      return true;
    }
    final String outputFormatValue = outputOptions.getOutputFormatValue();
    return "text".equals(outputFormatValue) || "txt".equals(outputFormatValue);
  }

  private void forceInstantiationFailureIfConfigured() {
    final String propertyValue =
        System.getProperty(this.getClass().getName() + ".force-instantiation-failure");
    if (propertyValue != null) {
      throw new RuntimeException("Forced instantiation error");
    }
  }
}
