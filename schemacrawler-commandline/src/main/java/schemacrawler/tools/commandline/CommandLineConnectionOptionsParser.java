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

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerCommandLineException;
import schemacrawler.schemacrawler.SchemaCrawlerException;

/**
 * Options for the command-line.
 */
final class CommandLineConnectionOptionsParser
  extends BaseDatabaseConnectionOptionsParser
{

  private static final String URL = "url";

  CommandLineConnectionOptionsParser(final Config config)
  {
    super(config);
  }

  @Override
  public void loadConfig()
    throws SchemaCrawlerException
  {
    super.loadConfig();

    if (config.hasValue(URL))
    {
      config.put(URL, config.getStringValue(URL, null));
    }
    final String connectionUrl = config.getStringValue(URL, null);
    if (isBlank(connectionUrl))
    {
      throw new SchemaCrawlerCommandLineException("No database connection URL provided");
    }
  }

}
