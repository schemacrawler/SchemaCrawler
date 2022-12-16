/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

@Command(
    name = "config-file",
    header = "** Load SchemaCrawler configuration from the classpath and file",
    description = {"", "For more information, see https://www.schemacrawler.com/config.html", ""},
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
              + "Optional")
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
