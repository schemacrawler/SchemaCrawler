/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.utility;


import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;
import java.util.logging.Level;

import schemacrawler.SchemaCrawlerLogger;
import schemacrawler.schemacrawler.Config;
import us.fatehi.utility.ioresource.InputResource;
import us.fatehi.utility.string.StringFormat;

public class PropertiesUtility
{

  private static final SchemaCrawlerLogger LOGGER =
    SchemaCrawlerLogger.getLogger(PropertiesUtility.class.getName());

  /**
   * Loads a properties file.
   *
   * @param inputResource
   *   Config resource.
   * @return Config
   */
  public static Config loadConfig(final InputResource inputResource)
  {
    final Properties properties = loadProperties(inputResource);
    return new Config(properties);
  }

  /**
   * Loads a properties file.
   *
   * @param inputResource
   *   Properties resource.
   * @return Properties
   */
  public static Properties loadProperties(final InputResource inputResource)
  {
    requireNonNull(inputResource, "No input resource provided");
    LOGGER.log(Level.INFO,
               new StringFormat("Loading properties from <%s>", inputResource));

    try (final Reader reader = inputResource.openNewInputReader(UTF_8);)
    {
      final Properties properties = new Properties();
      properties.load(reader);
      return properties;
    }
    catch (final IOException e)
    {
      LOGGER.log(Level.WARNING,
                 new StringFormat("Cannot load properties from <%s>",
                                  inputResource),
                 e);
      return new Properties();
    }
  }

  private PropertiesUtility()
  {
    // Prevent instantiation
  }

}
