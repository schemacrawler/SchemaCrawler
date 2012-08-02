/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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
import sf.util.clparser.BooleanOption;
import sf.util.clparser.StringOption;

/**
 * Parses the command line.
 * 
 * @author Sualeh Fatehi
 */
public final class ApplicationOptionsParser
  extends BaseOptionsParser<ApplicationOptions>
{

  public ApplicationOptionsParser()
  {
    super(new StringOption("loglevel", "OFF"),
          new BooleanOption('?', "help"),
          new BooleanOption('h', "-help"),
          new BooleanOption('V', "-version"));
  }

  @Override
  public ApplicationOptions getOptions()
  {
    final ApplicationOptions options = new ApplicationOptions();

    final String logLevelString = getStringValue("loglevel");
    if (!sf.util.Utility.isBlank(logLevelString))
    {
      final Level applicationLogLevel = Level.parse(logLevelString
        .toUpperCase());
      options.setApplicationLogLevel(applicationLogLevel);
    }

    if (getBooleanValue("?") || getBooleanValue("h"))
    {
      options.setShowHelp(true);
    }
    if (getBooleanValue("V") || getBooleanValue("-version"))
    {
      options.setShowHelp(true);
      options.setShowVersionOnly(true);
    }

    return options;
  }

}
