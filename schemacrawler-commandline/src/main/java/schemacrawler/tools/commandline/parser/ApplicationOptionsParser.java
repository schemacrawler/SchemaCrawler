/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.commandline.parser;


import static us.fatehi.commandlineparser.CommandLineUtility.newCommandLine;

import java.util.logging.Level;

import picocli.CommandLine;
import schemacrawler.tools.commandline.ApplicationOptions;
import schemacrawler.tools.commandline.LogLevel;

public final class ApplicationOptionsParser
  implements OptionsParser
{

  private final CommandLine commandLine;
  @CommandLine.Option(names = {
    "--log-level" }, description = "Set logging level")
  private LogLevel loglevel;
  @CommandLine.Unmatched
  private final String[] remainder = new String[0];
  @CommandLine.Option(names = {
    "-h", "--help", "-?" }, description = "Show help")
  private boolean showHelp;
  @CommandLine.Option(names = {
    "-V", "--version" }, description = "Print SchemaCrawler version and exit")
  private boolean showVersionOnly;

  public ApplicationOptionsParser()
  {
    commandLine = newCommandLine(this);
  }

  @Override
  public void parse(final String[] args)
  {
    commandLine.parse(args);
  }

  @Override
  public String[] getRemainder()
  {
    return remainder;
  }

  public ApplicationOptions getApplicationOptions()
  {
    if (loglevel == null)
    {
      loglevel = LogLevel.OFF;
    }
    final Level level = loglevel.getLevel();
    final ApplicationOptions options = new ApplicationOptions(level,
                                                              showHelp,
                                                              showVersionOnly);
    return options;
  }

}
