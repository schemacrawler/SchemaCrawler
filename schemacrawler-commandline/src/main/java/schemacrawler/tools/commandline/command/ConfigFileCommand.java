/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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
import schemacrawler.tools.options.ConfigUtility;

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
    state.setBaseConfig(ConfigUtility.fromMap(appConfig));
  }
}
