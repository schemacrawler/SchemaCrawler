/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2014, Sualeh Fatehi.
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


import java.io.IOException;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;

/**
 * Parses the command-line.
 *
 * @author Sualeh Fatehi
 */
public class ConfigParser
  extends BaseConfigOptionsParser
{

  public ConfigParser(final Config config)
  {
    super(config);
    normalizeOptionName("configfile", "g");
    normalizeOptionName("additionalconfigfile", "p");
  }

  @Override
  public void loadConfig()
    throws SchemaCrawlerException
  {
    try
    {
      final String configfile = config
        .getStringValue("configfile", "schemacrawler.config.properties");
      final String additionalconfigfile = config
        .getStringValue("additionalconfigfile",
                        "schemacrawler.additional.config.properties");
      config.putAll(Config.load(configfile, additionalconfigfile));

      consumeOption("configfile");
      consumeOption("additionalconfigfile");
    }
    catch (final IOException e)
    {
      throw new SchemaCrawlerException("Could not load config files", e);
    }
  }

}
