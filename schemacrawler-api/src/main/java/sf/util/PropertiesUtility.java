/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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
package sf.util;


import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.isReadable;
import static java.nio.file.Files.isRegularFile;
import static java.nio.file.Files.newBufferedReader;
import static java.util.Objects.requireNonNull;
import static sf.util.Utility.isBlank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.util.Properties;
import java.util.logging.Level;

import schemacrawler.schemacrawler.Config;

public class PropertiesUtility
{
  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(PropertiesUtility.class.getName());

  /**
   * Loads a properties file.
   *
   * @param properties
   *        Properties object.
   * @param propertiesFile
   *        Properties file.
   * @return Properties
   * @throws IOException
   */
  public static Properties loadProperties(final Path propertiesFile)
  {
    if (propertiesFile == null || !isRegularFile(propertiesFile)
        || !isReadable(propertiesFile))
    {
      LOGGER.log(Level.CONFIG,
                 new StringFormat("Cannot load properties from file <%s>",
                                  propertiesFile));
      return new Properties();
    }

    LOGGER.log(Level.INFO,
               new StringFormat("Loading properties from file <%s>",
                                propertiesFile));
    BufferedReader reader;
    try
    {
      reader = newBufferedReader(propertiesFile, UTF_8);
      final Properties properties = loadProperties(reader);
      return properties;
    }
    catch (final IOException e)
    {
      LOGGER.log(Level.WARNING,
                 new StringFormat("Cannot load properties from file <%s>",
                                  propertiesFile),
                 e);
      return new Properties();
    }
  }

  /**
   * Loads a properties file from a CLASSPATH resource.
   *
   * @param resource
   *        A CLASSPATH resource.
   * @return Properties
   * @throws IOException
   */
  public static Properties loadProperties(final String resource)
  {
    if (isBlank(resource))
    {
      LOGGER.log(Level.CONFIG,
                 new StringFormat("Cannot load properties from resource <%s>",
                                  resource));
      return new Properties();
    }

    final InputStream stream = Config.class.getResourceAsStream(resource);
    final Properties properties;
    if (stream != null)
    {
      properties = loadProperties(new InputStreamReader(stream, UTF_8));
    }
    else
    {
      properties = new Properties();
    }
    return properties;
  }

  /**
   * Loads a properties file.
   *
   * @param reader
   *        Properties data stream.
   * @return Properties
   */
  private static Properties loadProperties(final Reader reader)
  {
    requireNonNull(reader);
    final Properties properties = new Properties();
    try (final BufferedReader bufferedReader = new BufferedReader(reader);)
    {
      properties.load(bufferedReader);
    }
    catch (final IOException e)
    {
      LOGGER.log(Level.WARNING, "Error loading properties", e);
    }
    return properties;
  }

}
