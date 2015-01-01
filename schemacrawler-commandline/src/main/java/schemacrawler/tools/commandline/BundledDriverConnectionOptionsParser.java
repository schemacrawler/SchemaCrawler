/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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


import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;

/**
 * Options for the command-line.
 *
 * @author sfatehi
 */
final class BundledDriverConnectionOptionsParser
  extends BaseDatabaseConnectionOptionsParser
{

  private static final String URLX = "urlx";
  private static final String DATABASE = "database";
  private static final String PORT = "port";
  private static final String HOST = "host";

  BundledDriverConnectionOptionsParser(final Config config)
  {
    super(config);
  }

  @Override
  public void loadConfig()
    throws SchemaCrawlerException
  {
    super.loadConfig();

    if (config.hasValue(HOST))
    {
      config.put(HOST, config.getStringValue(HOST, ""));
    }
    if (config.hasValue(PORT))
    {
      config.put(PORT, String.valueOf(config.getIntegerValue(PORT, 0)));
    }
    if (config.hasValue(DATABASE))
    {
      config.put(DATABASE, config.getStringValue(DATABASE, ""));
    }
    if (config.hasValue(URLX))
    {
      config.put(URLX, config.getStringValue(URLX, ""));
    }
  }

}
