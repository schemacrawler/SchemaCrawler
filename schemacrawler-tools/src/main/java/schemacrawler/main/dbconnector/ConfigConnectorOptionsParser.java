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

package schemacrawler.main.dbconnector;


import java.util.Map;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.DatabaseConnectionOptions;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import sf.util.CommandLineParser.BooleanOption;
import sf.util.CommandLineParser.Option;
import sf.util.CommandLineParser.StringOption;

/**
 * Options for the command line.
 * 
 * @author sfatehi
 */
public final class ConfigConnectorOptionsParser
  extends BaseConnectorOptionsParser
{

  private final BooleanOption optionUseDefaultConnection = new BooleanOption('d',
                                                                             "default");
  private final StringOption optionConnection = new StringOption('c',
                                                                 "connection",
                                                                 null);

  /**
   * Parses the command line into options.
   * 
   * @param args
   */
  public ConfigConnectorOptionsParser(final String[] args,
                                           final Config config)
  {
    super(args, config);
  }

  @Override
  protected String getHelpResource()
  {
    return "/help/Commands.readme.txt";
  }

  @Override
  public DatabaseConnectionOptions getOptions()
    throws SchemaCrawlerException
  {
    parse(new Option[] {
        optionUseDefaultConnection,
        optionConnection,
        optionUser,
        optionPassword,
    });

    // Check arguments
    if (!optionPassword.isFound())
    {
      throw new SchemaCrawlerException("Please provide the password");
    }

    final String connectionName;
    if (optionUseDefaultConnection.getValue())
    {
      connectionName = config.get("defaultconnection");
    }
    else
    {
      connectionName = optionConnection.getValue();
    }

    final Map<String, String> databaseConnectionConfig = ConfigUtility
      .partition(config, connectionName);
    databaseConnectionConfig.put("user", optionUser.getValue());
    databaseConnectionConfig.put("password", optionPassword.getValue());

    ConfigUtility.substituteVariables(databaseConnectionConfig);

    return createOptionsFromConfig(databaseConnectionConfig);
  }

}
