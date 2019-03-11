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

package schemacrawler.tools.commandline;


import picocli.CommandLine;

public final class ApplicationOptionsParser
{

  private final CommandLine commandLine;

  @CommandLine.Option(names = {
    "-V", "--version" }, description = "Print SchemaCrawler version and exit")
  private boolean showVersionOnly = false;
  @CommandLine.Option(names = {
    "-loglevel", "--log-level" }, description = "Set logging level")
  private LogLevel loglevel = LogLevel.OFF;
  @CommandLine.Option(names = {
    "-h", "--help", "-?" }, description = "Show help")
  private boolean showHelp = false;

  @CommandLine.Parameters
  private String[] remainder = new String[0];

  public ApplicationOptionsParser()
  {
    commandLine = new CommandLine(this);
    commandLine.setUnmatchedOptionsArePositionalParams(true);
    commandLine.setCaseInsensitiveEnumValuesAllowed(true);
    commandLine.setTrimQuotes(true);
    commandLine.setToggleBooleanFlags(false);
  }

  public ApplicationOptions parse(final String[] args)
  {
    commandLine.parse(args);

    final ApplicationOptions options = new ApplicationOptions();
    options.setApplicationLogLevel(loglevel.getLevel());
    options.setShowHelp(showHelp);
    options.setShowVersionOnly(showVersionOnly);

    return options;
  }

  public String[] getRemainder()
  {
    return remainder;
  }

}
