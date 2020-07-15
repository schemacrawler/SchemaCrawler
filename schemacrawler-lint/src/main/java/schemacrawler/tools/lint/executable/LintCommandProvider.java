/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.lint.executable;


import static schemacrawler.tools.executable.commandline.PluginCommand.newPluginCommand;
import static sf.util.Utility.isBlank;

import java.nio.file.Path;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.executable.BaseCommandProvider;
import schemacrawler.tools.executable.CommandDescription;
import schemacrawler.tools.executable.SchemaCrawlerCommand;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.lint.LintDispatch;
import schemacrawler.tools.options.OutputOptions;

public class LintCommandProvider
  extends BaseCommandProvider
{

  public static final String DESCRIPTION_HEADER =
    "Find lints (non-adherence to coding standards and conventions) "
    + "in the database schema";

  public LintCommandProvider()
  {
    super(new CommandDescription(LintCommand.COMMAND, DESCRIPTION_HEADER));
  }

  @Override
  public SchemaCrawlerCommand newSchemaCrawlerCommand(final String command)
  {
    return new LintCommand();
  }

  @Override
  public boolean supportsSchemaCrawlerCommand(final String command,
                                              final SchemaCrawlerOptions schemaCrawlerOptions,
                                              final Config additionalConfiguration,
                                              final OutputOptions outputOptions)
  {
    return supportsCommand(command);
  }

  @Override
  public boolean supportsOutputFormat(final String command,
                                      final OutputOptions outputOptions)
  {
    if (outputOptions == null)
    {
      return false;
    }
    final String format = outputOptions.getOutputFormatValue();
    if (isBlank(format))
    {
      return false;
    }
    final boolean supportsOutputFormat =
      LintReportOutputFormat.isSupportedFormat(format);
    return supportsOutputFormat;
  }

  @Override
  public PluginCommand getCommandLineCommand()
  {
    final PluginCommand pluginCommand = newPluginCommand("lint",
                                                          "** "
                                                          + DESCRIPTION_HEADER,
                                                          "For more information, see https://www.schemacrawler.com/lint.html %n");
    pluginCommand
      .addOption("linter-configs",
                 "Path to the SchemaCrawler lint XML configuration file",
                 Path.class)
      .addOption("lint-dispatch",
                 "Specifies how to fail if a linter threshold is exceeded%n"
                 + "Optional, defaults to none%n"
                 + "Corresponds to the configuration file setting: schemacrawler.lint.lintdispatch",
                 LintDispatch.class)
      .addOption("run-all-linters",
                 "Whether to run all linters, including running the ones "
                 + "that are not explicitly configured with their default settings%n"
                 + "Optional, defaults to true%n"
                 + "Corresponds to the configuration file setting: schemacrawler.lint.runalllinters",
                 boolean.class);
    return pluginCommand;
  }

}
