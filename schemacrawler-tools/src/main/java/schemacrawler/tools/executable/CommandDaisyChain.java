/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
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


import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.text.base.CommonTextOptionsBuilder;

/**
 * Allows chaining multiple executables together, that produce shared
 * artifacts, such as a single HTML file.
 */
public final class CommandDaisyChain
  extends BaseCommandChain
{

  public CommandDaisyChain(final String commands)
    throws SchemaCrawlerException
  {
    super(commands);
  }

  @Override
  public void execute()
    throws Exception
  {
    // Null checks are done before execution

    // Commands are processed at execution time. That is, after
    // all configuration settings are made.
    final Commands commands = new Commands(getCommand());
    if (commands.isEmpty())
    {
      throw new SchemaCrawlerException("No command specified");
    }

    for (final String command: commands)
    {
      final SchemaCrawlerCommand scCommand = addNextAndConfigureForExecution(command,
                                                                             outputOptions);
      if (scCommand == null)
      {
        continue;
      }

      final CommonTextOptionsBuilder commonTextOptions = new CommonTextOptionsBuilder();
      commonTextOptions.fromConfig(additionalConfiguration);

      if (commands.hasMultipleCommands())
      {
        if (commands.isFirstCommand(command))
        {
          // First command - no footer
          commonTextOptions.noFooter(true);
        }
        else if (commands.isLastCommand(command))
        {
          // Last command - no header, or info
          commonTextOptions.noHeader(true);
          commonTextOptions.noInfo();

          commonTextOptions.appendOutput();
        }
        else
        {
          // Middle command - no header, footer, or info
          commonTextOptions.noHeader(true);
          commonTextOptions.noInfo();
          commonTextOptions.noFooter(true);

          commonTextOptions.appendOutput();
        }
      }

      final Config commandAdditionalConfig = new Config();
      if (additionalConfiguration != null)
      {
        commandAdditionalConfig.putAll(additionalConfiguration);
      }
      commandAdditionalConfig.putAll(commonTextOptions.toConfig());
      scCommand.setAdditionalConfiguration(commandAdditionalConfig);

    }

    executeChain();

  }

}
