/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static sf.util.Utility.isBlank;

import java.util.logging.Level;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerCommandLineException;
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
    throws SchemaCrawlerCommandLineException
  {
    final ApplicationOptions options = new ApplicationOptions();

    if (config.hasValue("loglevel"))
    {
      final String logLevelString = config.getStringValue("loglevel", "OFF");
      if (!isBlank(logLevelString))
      {
        final Level applicationLogLevel;
        try
        {
          applicationLogLevel = Level.parse(logLevelString.toUpperCase());
        }
        catch (final IllegalArgumentException e)
        {
          throw new SchemaCrawlerCommandLineException(e.getMessage());
        }
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
