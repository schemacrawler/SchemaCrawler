/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
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


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import sf.util.CommandLineParser;
import sf.util.GroupedProperties;
import sf.util.Prompter;
import sf.util.Utilities;
import dbconnector.datasource.PropertiesDataSource;
import dbconnector.datasource.PropertiesDataSourceException;

/**
 * Main class that reads a properties file for database connection information,
 * and tests the database connections.
 * 
 * @author Sualeh Fatehi sualeh@hotmail.com
 * @version 1.0
 */
public final class Main
{

  private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
  private static final String CONNECTION_PROPERTIES = "connection.properties";

  /**
   * Internal storage for information. Read from text file.
   */
  private static String info;

  static
  {
    // load about information
    final byte[] text = Utilities.readFully(Main.class
      .getResourceAsStream("/dbconnector-readme.txt"));
    info = new String(text);

  }

  private Main()
  {
  }

  /**
   * Get connection parameters, and creates a connection, and crawls the schema.<BR>
   * 
   * @param args
   *          Arguments passed into the program from the command line.
   * @throws PropertiesDataSourceException
   *           On an exception creating the data source
   */
  public static void main(final String[] args)
    throws PropertiesDataSourceException
  {

    if (args.length == 0)
    {
      printUsage();
      return;
    }
    final CommandLineParser parser = new CommandLineParser();

    parser
      .addOption(new CommandLineParser.StringOption('f', "connectionsfile"));

    parser.parse(args);

    String connectionsFileName = CONNECTION_PROPERTIES;
    final CommandLineParser.BaseOption connectionsFileOption = parser
      .getOption("connectionsfile");

    if (connectionsFileOption.isFound())
    {
      connectionsFileName = (String) connectionsFileOption.getValue();
    }

    final Properties config = loadConnections(connectionsFileName);
    if (createDataSource(args, config) == null)
    {
      System.exit(2);
    }

  } // end main

  /**
   * Creates a PropertiesDataSource using an argument list as passed into a main
   * program.
   * 
   * @param args
   *          List of arguments.
   * @param properties
   *          Connection properties
   * @return A PropertiesDataSource, or null on an exception.
   * @throws PropertiesDataSourceException
   *           on an exception
   */
  public static PropertiesDataSource createDataSource(
                                                      final String[] args,
                                                      final Properties properties)
    throws PropertiesDataSourceException
  {

    PropertiesDataSource dataSource = null;

    final CommandLineParser parser = new CommandLineParser();
    //
    parser.addOption(new CommandLineParser.BooleanOption('h', "?"));
    parser.addOption(new CommandLineParser.BooleanOption('a', "testall"));
    parser.addOption(new CommandLineParser.StringOption('x', "prompt"));
    parser.addOption(new CommandLineParser.BooleanOption('d', "default"));
    parser.addOption(new CommandLineParser.StringOption('c', "connection"));
    //
    parser.addOption(new CommandLineParser.StringOption("driver"));
    parser.addOption(new CommandLineParser.StringOption("url"));
    parser.addOption(new CommandLineParser.StringOption("user"));
    parser.addOption(new CommandLineParser.StringOption("password"));
    //
    parser.parse(args);

    final boolean help = parser.getOption("h").isFound();
    if (help)
    {
      printUsage();
      return null;
    }

    final boolean testAll = parser.getOption("testall").isFound();

    final CommandLineParser.BaseOption promptOption = parser
      .getOption("prompt");
    final boolean prompt = promptOption.isFound();

    boolean defaultConnection = parser.getOption("default").isFound();

    final CommandLineParser.BaseOption connectionNameOption = parser
      .getOption("connection");

    // JDBC connection information
    final CommandLineParser.BaseOption driverOption = parser.getOption("driver");
    String driver = (String) driverOption.getValue();
    final CommandLineParser.BaseOption urlOption = parser.getOption("url");
    String url = (String) urlOption.getValue();
    final CommandLineParser.BaseOption userOption = parser.getOption("user");
    String user = (String) userOption.getValue();
    final CommandLineParser.BaseOption passwordOption = parser.getOption("password");
    String password = (String) passwordOption.getValue();
    boolean useJdbcConnection = urlOption.isFound();
    
    String connectionName = null;
    if (prompt)
    {
      defaultConnection = false;
      connectionName = (String) promptOption.getValue();
    }
    else
    {
      if (connectionNameOption.isFound())
      {
        defaultConnection = false;
        connectionName = (String) connectionNameOption.getValue();
      }
      else
      {
        defaultConnection = true;
      }
    }
    if (defaultConnection)
    {
      connectionName = null;
    }

    if (testAll)
    {
      testAllConnections(properties);
    }
    else if (prompt)
    {
      final Properties connectionProperties = prompt(connectionName
                                                         + ".properties",
                                                     connectionName);
      dataSource = new PropertiesDataSource(connectionProperties,
          connectionName);
    }
    else if (useJdbcConnection) {
      dataSource = new PropertiesDataSource(driver, url, user, password);      
    }
    else
    {
      dataSource = new PropertiesDataSource(properties, connectionName);
    }

    return dataSource;

  }

  private static Properties loadConnections(final String connectionsFileName)
  {
    final Properties properties = new Properties();

    InputStream propertiesStream = null;
    try
    {
      propertiesStream = new BufferedInputStream(new FileInputStream(
          connectionsFileName));
      properties.load(propertiesStream);
      propertiesStream.close();
    }
    catch (final IOException e)
    {
      LOGGER.log(Level.WARNING, "Error loading connection parameters", e);
    }
    finally
    {
      try
      {
        if (propertiesStream != null)
        {
          propertiesStream.close();
        }
      }
      catch (final IOException e)
      {
        LOGGER.log(Level.WARNING, "Error closing stream", e);
      }
    }
    return properties;
  }

  private static void testAllConnections(final Properties properties)
  {
    final String[] groups = new GroupedProperties(properties).groups();

    for (int i = 0; i < groups.length; i++)
    {
      try
      {
        new PropertiesDataSource(properties, groups[i]);
      }
      catch (final PropertiesDataSourceException e)
      {
        LOGGER.log(Level.WARNING, "Error testing connection \"" + groups[i]
                                  + "\"", e);
      }
      System.err.println();
      System.err.println();
    }
  }

  private static void printUsage()
  {
    System.out.println(Version.about());
    System.out.println(info);
  }

  /**
   * Prompts for a database connection.
   * 
   * @param cxnParamsFile
   *          Connection properties file
   * @param connectionName
   *          Connection name
   * @return Connection properties
   */
  public static Properties prompt(final String cxnParamsFile,
                                  final String connectionName)
  {

    final Properties defaultCxnParams = new Properties();
    final File defaultCxnProps = new File(cxnParamsFile);
    if (defaultCxnProps.exists())
    {
      try
      {
        defaultCxnParams.load(new FileInputStream(defaultCxnProps));
      }
      catch (final IOException e)
      {
        LOGGER.log(Level.WARNING, "Error loading properties", e);
      }
    }

    final Prompter prompter = new Prompter();
    final Properties cxnParams = new Properties(defaultCxnParams);
    String param;

    // set defaults
    cxnParams.setProperty("schema", "%");

    // prompt for driver
    final String driver = "driver";
    param = (String) prompter.getInput(driver,
                                       cxnParams.getProperty(driver),
                                       Prompter.STRING,
                                       false);
    cxnParams.setProperty(connectionName + "." + driver, param);

    // prompt for url
    final String url = "url";
    param = (String) prompter.getInput(url,
                                       cxnParams.getProperty(url),
                                       Prompter.STRING,
                                       false);
    cxnParams.setProperty(connectionName + "." + url, param);

    // prompt for schema
    final String schema = "schema";
    param = (String) prompter.getInput(schema,
                                       cxnParams.getProperty(schema),
                                       Prompter.STRING,
                                       false);
    cxnParams.setProperty(connectionName + "." + schema, param);

    // prompt for user
    final String user = "user";
    param = (String) prompter.getInput(user,
                                       cxnParams.getProperty(user),
                                       Prompter.STRING,
                                       false);
    cxnParams.setProperty(connectionName + "." + user, param);

    // prompt for password
    final String password = "password";
    param = (String) prompter.getInput(password, cxnParams
      .getProperty(password), Prompter.STRING, true);
    cxnParams.setProperty(connectionName + "." + password, param);

    // make the user defined connection the default
    cxnParams.setProperty("defaultconnection", connectionName);

    try
    {
      cxnParams.store(new FileOutputStream(cxnParamsFile),
                      "dbconnector connection paramaters");
    }
    catch (final IOException e)
    {
      LOGGER.log(Level.WARNING, "", e);
    }

    return cxnParams;

  }

}
