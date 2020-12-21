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

import static schemacrawler.tools.commandline.utility.CommandLineConfigUtility.loadConfig;
import static us.fatehi.utility.IOUtility.isFileReadable;

import java.nio.file.Path;
import java.util.Map;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import schemacrawler.tools.commandline.state.BaseStateHolder;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.options.Config;

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

  @Option(
      names = {"-g", "--config-file"},
      description =
          "Read SchemaCrawler configuration properties from <configfile>%n"
              + "<configfile> is the full path to the configuration file%n"
              + "Optional, uses the default schemacrawler.config.properties file in the current directory, or in-built default options")
  private Path configfile;

  public ConfigFileCommand(final ShellState state) {
    super(state);
  }

  @Override
  public void run() {
    if (isFileReadable(configfile)) {
      System.setProperty("config.file", configfile.toString());
    }

    final Map<String, Object> appConfig = loadConfig();
    state.setBaseConfig(new Config(appConfig));
  }
}
