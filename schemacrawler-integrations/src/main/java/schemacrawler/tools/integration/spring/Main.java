/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
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

package schemacrawler.tools.integration.spring;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import schemacrawler.tools.commandline.ApplicationOptionsParser;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.options.ApplicationOptions;
import sf.util.Utility;

/**
 * Main class that takes arguments for a database for crawling a schema.
 */
public final class Main
{

  private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

  /**
   * Get connection parameters, and creates a connection, and crawls the
   * schema.
   * 
   * @param args
   *        Arguments passed into the program from the command line.
   */
  public static void main(final String[] args)
  {
    Connection connection = null;
    try
    {
      final ApplicationOptions applicationOptions = new ApplicationOptionsParser(args)
        .getOptions();
      if (applicationOptions.isShowHelp())
      {
        final String text = Utility.readFully(Main.class
          .getResourceAsStream("/help/SchemaCrawler.spring.txt"));
        System.out.println(text);
        System.exit(0);
      }

      applicationOptions.applyApplicationLogLevel();

      final SpringOptions springOptions = new SpringOptionsParser(args)
        .getOptions();
      final ApplicationContext appContext = new FileSystemXmlApplicationContext(springOptions
        .getContextFileName());
      final Executable executable = (Executable) appContext
        .getBean(springOptions.getExecutableName());
      final DataSource dataSource = (DataSource) appContext
        .getBean(springOptions.getDataSourceName());
      connection = dataSource.getConnection();
      executable.execute(connection);
    }
    catch (final Exception e)
    {
      e.printStackTrace();
    }
    finally
    {
      try
      {
        if (connection != null)
        {
          connection.close();
          LOGGER.log(Level.INFO, "Closed database connection, " + connection);
        }
      }
      catch (final SQLException e)
      {
        final String errorMessage = e.getMessage();
        LOGGER.log(Level.WARNING, "Could not close the connection: "
                                  + errorMessage);
      }
    }
  }

  private Main()
  {
    // Prevent instantiation
  }

}
