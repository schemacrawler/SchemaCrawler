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

package dbconnector.dbconnector;


import javax.sql.DataSource;

import sf.util.CommandLineParser;
import sf.util.Config;
import sf.util.Utilities;
import sf.util.CommandLineParser.BooleanOption;
import sf.util.CommandLineParser.Option;
import sf.util.CommandLineParser.StringOption;
import dbconnector.datasource.PropertiesDataSource;

/**
 * Main class that reads a properties file for database connection
 * information, and tests the database connections.
 * 
 * @author Sualeh Fatehi sualeh@hotmail.com
 */
public final class PropertiesDataSourceDatabaseConnector
  implements DatabaseConnector
{

  private static final String OPTION_PASSWORD = "password";
  private static final String OPTION_USER = "user";
  private static final String OPTION_URL = "url";
  private static final String OPTION_DRIVER = "driver";

  private static final String OPTION_CONNECTION = "connection";
  private static final String OPTION_DEFAULT = "default";

  private String dataSourceName;
  private final Config config;

  private final String driver;
  private final String url;
  private final String user;
  private final String password;
  private final boolean useJdbcConnection;

  /**
   * Creates a PropertiesDataSourceParser using an argument list as
   * passed into a main program.
   * 
   * @param args
   *        List of arguments.
   * @param config
   *        Connection properties
   * @throws DatabaseConnectorException
   *         on an exception
   */
  public PropertiesDataSourceDatabaseConnector(final String[] args,
                                               final Config config)
    throws DatabaseConnectorException
  {
    this.config = config;

    final CommandLineParser parser = createCommandLineParser();
    parser.parse(args);

    // JDBC connection information
    driver = parser.getStringOptionValue(OPTION_DRIVER);
    url = parser.getStringOptionValue(OPTION_URL);
    user = parser.getStringOptionValue(OPTION_USER);
    password = parser.getStringOptionValue(OPTION_PASSWORD);
    useJdbcConnection = !Utilities.isBlank(driver) && !Utilities.isBlank(url);

    final boolean useDefaultConnection = parser.getBooleanOptionValue(OPTION_DEFAULT);
    dataSourceName = parser.getStringOptionValue(OPTION_CONNECTION);
    // Use default connection if no connection is specified
    if (useDefaultConnection || Utilities.isBlank(dataSourceName))
    {
      dataSourceName = this.config.get("defaultconnection");
    }

  }

  /**
   * {@inheritDoc}
   * 
   * @see dbconnector.dbconnector.DatabaseConnector#createDataSource()
   */
  public DataSource createDataSource()
    throws DatabaseConnectorException
  {
    PropertiesDataSource dataSource = null;
    if (useJdbcConnection)
    {
      dataSource = new PropertiesDataSource(driver, url, user, password);
    }
    else
    {
      dataSource = new PropertiesDataSource(config.toProperties(),
                                            dataSourceName);
    }
    return dataSource;
  }

  /**
   * {@inheritDoc}
   * 
   * @see dbconnector.dbconnector.DatabaseConnector#getDataSourceName()
   */
  public String getDataSourceName()
  {
    return dataSourceName;
  }

  private CommandLineParser createCommandLineParser()
  {
    final CommandLineParser parser = new CommandLineParser();

    parser.addOption(new BooleanOption('d', OPTION_DEFAULT));
    parser.addOption(new StringOption('c', OPTION_CONNECTION, null));
    //
    parser
      .addOption(new StringOption(Option.NO_SHORT_FORM, OPTION_DRIVER, null));
    parser.addOption(new StringOption(Option.NO_SHORT_FORM, OPTION_URL, null));
    parser.addOption(new StringOption(Option.NO_SHORT_FORM, OPTION_USER, null));
    parser.addOption(new StringOption(Option.NO_SHORT_FORM,
                                      OPTION_PASSWORD,
                                      null));
    return parser;
  }

}
