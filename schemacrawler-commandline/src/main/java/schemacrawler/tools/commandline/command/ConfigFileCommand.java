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

package schemacrawler.tools.commandline.command;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import schemacrawler.SchemaCrawlerLogger;
import schemacrawler.tools.commandline.state.BaseStateHolder;
import schemacrawler.tools.commandline.state.SchemaCrawlerShellState;
import schemacrawler.tools.options.Config;
import schemacrawler.utility.PropertiesUtility;
import us.fatehi.utility.ioresource.ClasspathInputResource;
import us.fatehi.utility.ioresource.FileInputResource;
import us.fatehi.utility.string.StringFormat;

/**
 * Parses the command-line.
 *
 * @author Sualeh Fatehi
 */
@Command(
    name = "config-file",
    header = "** Load SchemaCrawler configuration from the classpath and file",
    description = {
      "",
      "SchemaCrawler configuration reads a resource called schemacrawler.config.properties "
          + "from the CLASSPATH, which includes the lib/ folder. "
          + "You can modify the default settings in this file.",
      "",
      "The order of loading configuration settings is:",
      "1. From a CLASSPATH resource called schemacrawler.config.properties",
      "2. Which can be overridden by settings in a configuration file (see below)",
      "3. Which can be overridden by other command-line options",
      "",
      "Command-line options will override configuration file options.",
      ""
    },
    headerHeading = "",
    synopsisHeading = "Shell Command:%n",
    customSynopsis = {"config-file"},
    optionListHeading = "Options:%n")
public class ConfigFileCommand extends BaseStateHolder implements Runnable {

  private static final SchemaCrawlerLogger LOGGER =
      SchemaCrawlerLogger.getLogger(ConfigFileCommand.class.getName());

  @Option(
      names = {"-g", "--config-file"},
      description =
          "Read SchemaCrawler configuration properties from <configfile>%n"
              + "<configfile> is the full path to the configuration file%n"
              + "Optional, uses the default schemacrawler.config.properties file in the current directory, or in-built default options")
  private Path configfile;

  public ConfigFileCommand(final SchemaCrawlerShellState state) {
    super(state);
  }

  @Override
  public void run() {

    final Config config = new Config();

    // 1. Load state from CLASSPATH, in place
    try {
      final Properties properties =
          PropertiesUtility.loadProperties(
              new ClasspathInputResource("/schemacrawler.config.properties"));
      final Config classpathConfig = new Config(properties);
      config.putAll(classpathConfig);
    } catch (final IOException e) {
      LOGGER.log(Level.CONFIG, "schemacrawler.config.properties not found on CLASSPATH");
    }

    // 2. Load state from file, in place
    final Config configFileConfig = loadConfig();
    config.putAll(configFileConfig);

    state.setBaseConfiguration(config);
    state.addAdditionalConfiguration(config);
  }

  private Config loadConfig() {
    if (configfile == null) {
      configfile = Paths.get("schemacrawler.config.properties");
    }
    configfile = configfile.normalize().toAbsolutePath();

    try {
      final Properties properties =
          PropertiesUtility.loadProperties(new FileInputResource(configfile));
      final Config config = new Config(properties);
      return config;
    } catch (final IOException e) {
      LOGGER.log(
          Level.CONFIG,
          new StringFormat(
              "SchemaCrawler configuration properties file not found, %s", configfile));

      return new Config();
    }
  }
}
