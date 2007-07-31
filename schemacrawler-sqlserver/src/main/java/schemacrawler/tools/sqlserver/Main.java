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

package schemacrawler.tools.sqlserver;


import dbconnector.dbconnector.DatabaseConnector;
import dbconnector.dbconnector.DatabaseConnectorFactory;
import schemacrawler.main.SchemaCrawlerMain;
import schemacrawler.tools.ToolsExecutor;
import sf.util.CommandLineUtility;
import sf.util.Config;

/**
 * Main class that takes arguments for a database for crawling a schema.
 */
public final class Main
{

  /**
   * Get connection parameters, and creates a connection, and crawls the
   * schema.
   * 
   * @param args
   *        Arguments passed into the program from the command line.
   * @throws Exception
   *         On an exception
   */
  public static void main(final String[] args)
    throws Exception
  {
    CommandLineUtility
      .checkForHelp(args, "/schemacrawler-sqlserver-readme.txt");
    CommandLineUtility.setLogLevel(args);

    try
    {
      final Config driverConfiguration = Config.load(Grep.class
        .getResourceAsStream("/schemacrawler.config.properties"));
      final DatabaseConnector dataSourceParser = DatabaseConnectorFactory
        .createBundledDriverDataSourceParser(args, driverConfiguration);
      SchemaCrawlerMain.schemacrawler(args,
                                      new ToolsExecutor(),
                                      dataSourceParser);
    }
    catch (final Exception e)
    {
      e.printStackTrace();
    }
  }

  private Main()
  {
    // Prevent instantiation
  }

}
