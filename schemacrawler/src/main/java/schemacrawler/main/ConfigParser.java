/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.main;


import schemacrawler.crawl.SchemaCrawlerException;
import sf.util.CommandLineParser;
import sf.util.CommandLineParser.StringOption;

/**
 * Parses the command line.
 * 
 * @author Sualeh Fatehi
 */
public final class ConfigParser
{

  private static final String OPTION_CONFIGFILE = "configfile";
  private static final String OPTION_CONFIGOVERRIDEFILE = "configoverridefile";

  /**
   * Parses the command line.
   * 
   * @param args
   *        Command line arguments
   * @return Command line options
   * @throws SchemaCrawlerException
   */
  static Config parseCommandLine(final String[] args)
    throws SchemaCrawlerException
  {

    final CommandLineParser parser = createCommandLineParser();
    parser.parse(args);

    final String cfgFile = parser.getStringOptionValue(OPTION_CONFIGFILE);
    final String cfgOverrideFile = parser
      .getStringOptionValue(OPTION_CONFIGOVERRIDEFILE);
    final Config config = Config.load(cfgFile, cfgOverrideFile);

    return config;

  }

  private static CommandLineParser createCommandLineParser()
  {
    final CommandLineParser parser = new CommandLineParser();
    parser.addOption(new StringOption('g',
                                      OPTION_CONFIGFILE,
                                      "schemacrawler.config.properties"));
    parser
      .addOption(new StringOption('p',
                                  OPTION_CONFIGOVERRIDEFILE,
                                  "schemacrawler.config.override.properties"));

    return parser;
  }

  private ConfigParser()
  {

  }

}
