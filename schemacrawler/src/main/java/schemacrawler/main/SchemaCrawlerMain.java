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


import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import schemacrawler.tools.Executable;
import sf.util.Config;
import dbconnector.Version;
import dbconnector.dbconnector.DatabaseConnector;

/**
 * Main class that takes arguments for a database for crawling a schema.
 */
public final class SchemaCrawlerMain
{

  private static final Logger LOGGER = Logger.getLogger(SchemaCrawlerMain.class
    .getName());

  /**
   * Executes with the command line, and a given executor. The executor
   * allows for the command line to be parsed independently of the
   * execution. The execution can integrate with other software, such as
   * Velocity.
   * 
   * @param args
   *        Command line arguments
   * @param config
   *        Configuration
   * @param executor
   *        Executor
   * @param dataSourceParser
   *        Datasource parser
   * @throws Exception
   *         On an exception
   */
  public static void schemacrawler(final String[] args,
                                   final Config config,
                                   final DatabaseConnector dataSourceParser)
    throws Exception
  {

    final List<Executable<?>> executables = ExecutableFactory
      .createExecutables(args, config);
    if (executables.size() > 0)
    {
      LOGGER.log(Level.CONFIG, Version.about());
      LOGGER.log(Level.CONFIG, "Commandline: " + Arrays.asList(args));
      for (final Executable<?> executable: executables)
      {
        LOGGER.log(Level.CONFIG, executable.toString());
        final DataSource dataSource = dataSourceParser.createDataSource();
        executable.execute(dataSource);
      }
    }
  }

  private SchemaCrawlerMain()
  {
    // Prevent instantiation
  }

}
