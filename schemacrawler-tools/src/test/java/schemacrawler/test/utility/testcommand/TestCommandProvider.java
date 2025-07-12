/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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
