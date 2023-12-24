/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.executable;

import java.util.Collection;

import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;

public interface CommandProvider {

  PluginCommand getCommandLineCommand();

  default PluginCommand getHelpCommand() {
    return getCommandLineCommand();
  }

  Collection<CommandDescription> getSupportedCommands();

  SchemaCrawlerCommand<?> newSchemaCrawlerCommand(String command, Config config);

  boolean supportsOutputFormat(String command, OutputOptions outputOptions);

  boolean supportsSchemaCrawlerCommand(
      String command,
      SchemaCrawlerOptions schemaCrawlerOptions,
      Config additionalConfig,
      OutputOptions outputOptions);
}
