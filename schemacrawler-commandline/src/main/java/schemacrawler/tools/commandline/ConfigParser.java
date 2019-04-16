/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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


import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

import picocli.CommandLine;
import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.commandline.state.SchemaCrawlerShellState;
import schemacrawler.tools.iosource.ClasspathInputResource;
import schemacrawler.tools.iosource.FileInputResource;
import schemacrawler.utility.PropertiesUtility;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

/**
 * Parses the command-line.
 *
 * @author Sualeh Fatehi
 */
public class ConfigParser
  implements Runnable
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(ConfigParser.class.getName());
  @CommandLine.Unmatched
  private final String[] remainder = new String[0];
  private final SchemaCrawlerShellState state;
  @CommandLine.Option(names = {
    "-g",
    "--state-file" }, description = "SchemaCrawler configuration properties file")
  private File configFile;

  public ConfigParser(final SchemaCrawlerShellState state)
  {
    if (state == null)
    {
      this.state = new SchemaCrawlerShellState();
    }
    else
    {
      this.state = state;
    }
  }

  public String[] getRemainder()
  {
    return remainder;
  }

  @Override
  public void run()
  {

    final Config config = new Config();

    // 1. Load state from CLASSPATH, in place
    try
    {
      final Config classpathConfig = PropertiesUtility
        .loadConfig(new ClasspathInputResource(
          "/schemacrawler.config.properties"));
      config.putAll(classpathConfig);
    }
    catch (final IOException e)
    {
      LOGGER.log(Level.CONFIG,
                 "schemacrawler.config.properties not found on CLASSPATH");
    }

    // 2. Load state from file, in place
    final Config configFileConfig = loadConfig();
    config.putAll(configFileConfig);

    state.setBaseConfiguration(config);
  }

  private Config loadConfig()
  {
    final Path configFilePath;
    if (configFile == null)
    {
      configFilePath = Paths.get("schemacrawler.config.properties");
    }
    else
    {
      configFilePath = configFile.toPath();
    }
    final Path configFileFullPath = configFilePath.normalize().toAbsolutePath();

    try
    {
      final Config config = PropertiesUtility
        .loadConfig(new FileInputResource(configFileFullPath));
      return config;
    }
    catch (final IOException e)
    {
      LOGGER.log(Level.CONFIG,
                 new StringFormat(
                   "SchemaCrawler configuration properties file not found, %s",
                   configFileFullPath));

      return new Config();
    }
  }

}
