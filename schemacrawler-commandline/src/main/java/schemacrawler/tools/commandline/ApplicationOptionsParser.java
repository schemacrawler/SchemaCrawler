/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.tools.commandline;


import java.util.logging.Level;

import schemacrawler.tools.options.ApplicationOptions;
import sf.util.CommandLineParser;
import sf.util.CommandLineParser.BooleanOption;
import sf.util.CommandLineParser.Option;
import sf.util.CommandLineParser.StringOption;

/**
 * Parses the command line.
 * 
 * @author Sualeh Fatehi
 */
public final class ApplicationOptionsParser
  extends BaseOptionsParser<ApplicationOptions>
{

  private final StringOption optionLogLevel = new StringOption(CommandLineParser.Option.NO_SHORT_FORM,
                                                               "loglevel",
                                                               "OFF");
  private final BooleanOption optionHelp1 = new BooleanOption('?', "help");
  private final BooleanOption optionHelp2 = new BooleanOption('h', "-help");

  public ApplicationOptionsParser(final String[] args)
  {
    super(args);
  }

  @Override
  public ApplicationOptions getOptions()
  {
    parse(new Option[] {
        optionLogLevel, optionHelp1, optionHelp2
    });

    final ApplicationOptions options = new ApplicationOptions();

    final String logLevelString = optionLogLevel.getValue();
    if (!sf.util.Utility.isBlank(logLevelString))
    {
      final Level applicationLogLevel = Level.parse(logLevelString
        .toUpperCase());
      options.setApplicationLogLevel(applicationLogLevel);
    }

    if (optionHelp1.getValue() || optionHelp2.getValue())
    {
      options.setShowHelp(true);
    }

    return options;
  }

}
