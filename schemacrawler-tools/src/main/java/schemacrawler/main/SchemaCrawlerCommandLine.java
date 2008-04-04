/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
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

import javax.sql.DataSource;

import schemacrawler.Config;
import schemacrawler.SchemaCrawlerException;
import schemacrawler.main.dbconnector.BundledDriverDatabaseConnector;
import schemacrawler.main.dbconnector.DatabaseConnector;
import schemacrawler.main.dbconnector.DatabaseConnectorException;
import schemacrawler.main.dbconnector.PropertiesDataSourceDatabaseConnector;
import schemacrawler.tools.Command;
import schemacrawler.tools.OutputOptions;
import sf.util.Utilities;

/**
 * Utility for parsing the SchemaCrawler command line.
 * 
 * @author Sualeh Fatehi
 */
public class SchemaCrawlerCommandLine
{

  private final List<String> args;
  //
  private final Command[] commands;
  private final Config config;
  private final OutputOptions outputOptions;
  private final DatabaseConnector databaseConnector;

  /**
   * Loads objects from command line options.
   * 
   * @param args
   *        Command line arguments.
   * @throws SchemaCrawlerException
   *         On an exception
   */
  public SchemaCrawlerCommandLine(final String[] args)
    throws SchemaCrawlerException
  {
    this(args, null);
  }

  /**
   * Loads objects from command line options. Optionally loads the
   * config from the classpath.
   * 
   * @param args
   *        Command line arguments.
   * @param configResource
   *        Config resource.
   * @throws SchemaCrawlerException
   *         On an exception
   */
  public SchemaCrawlerCommandLine(final String[] args,
                                  final String configResource)
    throws SchemaCrawlerException
  {
    this.args = Arrays.asList(args);
    if (args != null && args.length > 0)
    {
      commands = new CommandParser(args).getValue();
      outputOptions = new OutputOptionsParser(args).getValue();
    }
    else
    {
      commands = new Command[0];
      outputOptions = new OutputOptions();
    }

    try
    {
      if (!Utilities.isBlank(configResource))
      {
        config = Config.load(SchemaCrawlerCommandLine.class
          .getResourceAsStream(configResource));
        databaseConnector = new BundledDriverDatabaseConnector(args, config);
      }
      else
      {
        if (args != null && args.length > 0)
        {
          config = new ConfigParser(args).getValue();
        }
        else
        {
          config = new Config();
        }
        databaseConnector = new PropertiesDataSourceDatabaseConnector(args,
                                                                      config);
      }
    }
    catch (final DatabaseConnectorException e)
    {
      throw new SchemaCrawlerException("Cannot create a database connector", e);
    }
  }

  /**
   * Creates the datasource.
   * 
   * @return Datasource
   * @throws DatabaseConnectorException
   *         On an exception
   */
  public DataSource createDataSource()
    throws DatabaseConnectorException
  {
    return databaseConnector.createDataSource();
  }

  /**
   * Gets the commands.
   * 
   * @return Commands.
   */
  public Command[] getCommands()
  {
    return Arrays.asList(commands).toArray(new Command[commands.length]);
  }

  /**
   * Gets the config.
   * 
   * @return Config.
   */
  public Config getConfig()
  {
    return new Config(config);
  }

  /**
   * Gets the output options.
   * 
   * @return Output options.
   */
  public OutputOptions getOutputOptions()
  {
    return outputOptions.duplicate();
  }

  /**
   * Gets the partition in the config.
   * 
   * @return Partition in the config
   */
  public String getPartition()
  {
    return databaseConnector.getDataSourceName();
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return args.toString();
  }

}
