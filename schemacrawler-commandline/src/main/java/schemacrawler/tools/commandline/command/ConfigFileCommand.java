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

package schemacrawler.tools.commandline.command;


import static java.util.Objects.requireNonNull;

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
@CommandLine.Command(name = "config-file",
                     description = "Load SchemaCrawler configuration from the classpath and file")
public class ConfigFileCommand
  implements Runnable
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger.getLogger(
    ConfigFileCommand.class.getName());

  private final SchemaCrawlerShellState state;

  @CommandLine.Option(names = {
    "-g", "--state-file"
  },
                      description = "SchemaCrawler configuration properties file")
  private Path configFile;

  public ConfigFileCommand(final SchemaCrawlerShellState state)
  {
    this.state = requireNonNull(state, "No state provided");
  }

  @Override
  public void run()
  {

    final Config config = new Config();

    // 1. Load state from CLASSPATH, in place
    try
    {
      final Config classpathConfig = PropertiesUtility.loadConfig(new ClasspathInputResource(
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
    if (configFile == null)
    {
      configFile = Paths.get("schemacrawler.config.properties");
    }
    configFile = configFile.normalize().toAbsolutePath();

    try
    {
      final Config config = PropertiesUtility.loadConfig(new FileInputResource(
        configFile));
      return config;
    }
    catch (final IOException e)
    {
      LOGGER.log(Level.CONFIG,
                 new StringFormat(
                   "SchemaCrawler configuration properties file not found, %s",
                   configFile));

      return new Config();
    }
  }

}
