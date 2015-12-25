/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
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


import static sf.util.Utility.isBlank;

import java.util.logging.Level;

import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.options.ApplicationOptions;

/**
 * Parses the command-line.
 *
 * @author Sualeh Fatehi
 */
public final class ApplicationOptionsParser
  extends BaseOptionsParser<ApplicationOptions>
{

  public ApplicationOptionsParser(final Config config)
  {
    super(config);
    normalizeOptionName("loglevel");
    normalizeOptionName("help", "?", "h", "-help");
    normalizeOptionName("version", "V", "-version");
  }

  @Override
  public ApplicationOptions getOptions()
  {
    final ApplicationOptions options = new ApplicationOptions();

    if (config.hasValue("loglevel"))
    {
      final String logLevelString = config.getStringValue("loglevel", "OFF");
      if (!isBlank(logLevelString))
      {
        final Level applicationLogLevel = Level
          .parse(logLevelString.toUpperCase());
        options.setApplicationLogLevel(applicationLogLevel);
      }
      consumeOption("loglevel");
    }

    if (config.hasValue("help"))
    {
      options.setShowHelp(true);
      consumeOption("help");
    }
    if (config.hasValue("version"))
    {
      options.setShowHelp(true);
      options.setShowVersionOnly(true);
      consumeOption("version");
    }

    return options;
  }

}
