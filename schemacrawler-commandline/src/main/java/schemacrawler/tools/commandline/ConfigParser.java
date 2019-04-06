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


import static us.fatehi.commandlineparser.CommandLineUtility.newCommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

import picocli.CommandLine;
import schemacrawler.schemacrawler.Config;
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
  implements OptionsParser
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(ConfigParser.class.getName());

  private final CommandLine commandLine;

  @CommandLine.Option(names = {
    "-g",
    "--config-file" }, description = "SchemaCrawler configuration properties file")
  private File configFile;

  @CommandLine.Unmatched
  private String[] remainder = new String[0];

  public ConfigParser()
  {
    commandLine = newCommandLine(this);
  }

  @Override
  public void parse(final String[] args)
  {
    commandLine.parse(args);
  }

  public Config getConfig()
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

  @Override
  public String[] getRemainder()
  {
    return remainder;
  }

}
