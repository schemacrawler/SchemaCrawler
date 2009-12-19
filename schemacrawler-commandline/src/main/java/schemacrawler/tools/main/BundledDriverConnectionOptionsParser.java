/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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

package schemacrawler.tools.main;


import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.ConnectionOptions;
import schemacrawler.schemacrawler.DatabaseConfigConnectionOptions;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import sf.util.CommandLineParser.NumberOption;
import sf.util.CommandLineParser.Option;
import sf.util.CommandLineParser.StringOption;

/**
 * Options for the command line.
 * 
 * @author sfatehi
 */
public final class BundledDriverConnectionOptionsParser
  extends BaseDatabaseConnectionOptionsParser
{

  private final StringOption optionHost = new StringOption(Option.NO_SHORT_FORM,
                                                           "host",
                                                           null);
  private final NumberOption optionPort = new NumberOption(Option.NO_SHORT_FORM,
                                                           "port",
                                                           0);
  private final StringOption optionDatabase = new StringOption(Option.NO_SHORT_FORM,
                                                               "database",
                                                               "");

  /**
   * Parses the command line into options.
   * 
   * @param args
   */
  public BundledDriverConnectionOptionsParser(final String[] args,
                                              final Config config)
  {
    super(args, config);
  }

  @Override
  public ConnectionOptions getOptions()
    throws SchemaCrawlerException
  {
    parse(new Option[] {
        optionHost, optionPort, optionDatabase, optionUser, optionPassword,
    });

    final DatabaseConfigConnectionOptions connectionOptions = new DatabaseConfigConnectionOptions(config);

    if (optionHost.isFound())
    {
      connectionOptions.setHost(optionHost.getValue());
    }
    if (optionPort.isFound())
    {
      connectionOptions.setPort(optionPort.getValue().intValue());
    }
    if (optionDatabase.isFound())
    {
      connectionOptions.setDatabase(optionDatabase.getValue());
    }

    connectionOptions.setUser(optionUser.getValue());
    connectionOptions.setPassword(optionPassword.getValue());

    return connectionOptions;
  }

  @Override
  protected String getHelpResource()
  {
    return "/help/Commands.readme.txt";
  }

}
