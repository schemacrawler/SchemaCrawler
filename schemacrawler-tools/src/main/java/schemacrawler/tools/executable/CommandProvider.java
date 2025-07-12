/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.executable;

import java.util.Collection;

import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;
import us.fatehi.utility.property.PropertyName;

public interface CommandProvider {

  PluginCommand getCommandLineCommand();

  default PluginCommand getHelpCommand() {
    return getCommandLineCommand();
  }

  Collection<PropertyName> getSupportedCommands();

  SchemaCrawlerCommand<?> newSchemaCrawlerCommand(String command, Config config);

  boolean supportsOutputFormat(String command, OutputOptions outputOptions);

  boolean supportsSchemaCrawlerCommand(
      String command,
      SchemaCrawlerOptions schemaCrawlerOptions,
      Config additionalConfig,
      OutputOptions outputOptions);
}
