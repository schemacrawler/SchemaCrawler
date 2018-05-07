/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
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


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.iosource.FileInputResource;
import schemacrawler.utility.PropertiesUtility;
import sf.util.SchemaCrawlerLogger;

/**
 * Parses the command-line.
 *
 * @author Sualeh Fatehi
 */
public class ConfigParser
  extends BaseConfigOptionsParser
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(ConfigParser.class.getName());

  private static final String CONFIG_FILE = "configfile";

  public ConfigParser(final Config config)
  {
    super(config);
    normalizeOptionName(CONFIG_FILE, "g");
  }

  public void consumeOptions()
  {
    consumeOption(CONFIG_FILE);
  }

  @Override
  public void loadConfig()
    throws SchemaCrawlerException
  {
    try
    {
      final String configfile = config
        .getStringValue(CONFIG_FILE, "schemacrawler.config.properties");
      final Path configFilePath = Paths.get(configfile).normalize()
        .toAbsolutePath();
      config.putAll(PropertiesUtility
        .loadConfig(new FileInputResource(configFilePath)));
    }
    catch (final IOException e)
    {
      LOGGER.log(Level.CONFIG,
                 "schemacrawler.config.properties not found on CLASSPATH",
                 e);
    }
  }

}
