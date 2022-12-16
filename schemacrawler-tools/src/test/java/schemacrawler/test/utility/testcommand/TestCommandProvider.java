/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static schemacrawler.tools.executable.commandline.PluginCommand.newPluginCommand;

import schemacrawler.tools.executable.BaseCommandProvider;
import schemacrawler.tools.executable.CommandDescription;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;

public class TestCommandProvider extends BaseCommandProvider {

  public static final String DESCRIPTION_HEADER =
      "Test command which is not deployed with the release";

  public TestCommandProvider() {
    super(new CommandDescription(TestCommand.COMMAND, DESCRIPTION_HEADER));
  }

  @Override
  public PluginCommand getCommandLineCommand() {
    final PluginCommand pluginCommand =
        newPluginCommand(TestCommand.COMMAND, "** " + DESCRIPTION_HEADER);
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
    testCommand.setCommandOptions(commandOptions);
    return testCommand;
  }

  @Override
  public boolean supportsOutputFormat(final String command, final OutputOptions outputOptions) {
    if (outputOptions == null) {
      return true;
    }
    final String outputFormatValue = outputOptions.getOutputFormatValue();
    return outputFormatValue.equals("text") || outputFormatValue.equals("txt");
  }
}
