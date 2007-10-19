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

package dbconnector;


import sf.util.CommandLineParser;
import sf.util.CommandLineUtility;
import sf.util.Config;
import sf.util.CommandLineParser.StringOption;
import dbconnector.dbconnector.DatabaseConnector;
import dbconnector.dbconnector.DatabaseConnectorException;
import dbconnector.dbconnector.PropertiesDataSourceDatabaseConnector;

/**
 * Main class that reads a properties file for database connection
 * information, and tests the database connections.
 * 
 * @author Sualeh Fatehi sualeh@hotmail.com
 */
public final class Main
{

  private static final String OPTION_CONNECTIONSFILE = "connectionsfile";

  /**
   * Get connection parameters, and creates a connection, and crawls the
   * schema.
   * 
   * @param args
   *        Arguments passed into the program from the command line.
   * @throws DatabaseConnectorException
   */
  public static void main(final String[] args)
    throws DatabaseConnectorException
  {
    CommandLineUtility.checkForHelp(args, "/dbconnector-readme.txt");
    CommandLineUtility.setLogLevel(args);

    final CommandLineParser parser = new CommandLineParser();
    parser.addOption(new StringOption('f',
                                      OPTION_CONNECTIONSFILE,
                                      "connection.properties"));
    parser.parse(args);

    final String connectionsFileName = parser
      .getStringOptionValue(OPTION_CONNECTIONSFILE);
    final Config config = Config.load(connectionsFileName);

    final DatabaseConnector dataSourceParser = new PropertiesDataSourceDatabaseConnector(args,
                                                                                         config);
    if (dataSourceParser.createDataSource() == null)
    {
      System.exit(2);
    }

  }

  private Main()
  {
    // Prevent instantiation
  }

}
