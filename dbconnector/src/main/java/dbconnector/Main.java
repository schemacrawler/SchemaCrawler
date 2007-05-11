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


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import sf.util.CommandLineParser;
import sf.util.CommandLineUtility;
import sf.util.GroupedProperties;
import sf.util.Prompter;
import sf.util.Prompter.InputType;
import sf.util.Utilities;
import sf.util.CommandLineParser.BooleanOption;
import sf.util.CommandLineParser.Option;
import sf.util.CommandLineParser.StringOption;
import dbconnector.datasource.PropertiesDataSource;
import dbconnector.datasource.PropertiesDataSourceException;

/**
 * Main class that reads a properties file for database connection
 * information, and tests the database connections.
 * 
 * @author Sualeh Fatehi sualeh@hotmail.com
 */
public final class Main
{

  private static final String OPTION_PASSWORD = "password";
  private static final String OPTION_USER = "user";
  private static final String OPTION_URL = "url";
  private static final String OPTION_DRIVER = "driver";

  private static final String OPTION_CONNECTION = "connection";
  private static final String OPTION_DEFAULT = "default";

  private static final String OPTION_PROMPT = "prompt";
  private static final String OPTION_TESTALL = "testall";

  private static final String OPTION_CONNECTIONSFILE = "connectionsfile";

  private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

  /**
   * Creates a PropertiesDataSource using an argument list as passed
   * into a main program.
   * 
   * @param args
   *        List of arguments.
   * @param properties
   *        Connection properties
   * @return A PropertiesDataSource, or null on an exception.
   * @throws PropertiesDataSourceException
   *         on an exception
   */
  public static PropertiesDataSource createDataSource(final String[] args,
                                                      final Properties properties)
    throws PropertiesDataSourceException
  {

    PropertiesDataSource dataSource = null;

    final CommandLineParser parser = createCommandLineParser();
    parser.parse(args);

    final boolean testAll = parser.getBooleanOptionValue(OPTION_TESTALL);
    final boolean prompt = parser.getBooleanOptionValue(OPTION_PROMPT);
    boolean defaultConnection = parser.getBooleanOptionValue(OPTION_DEFAULT);

    // JDBC connection information
    final String driver = parser.getStringOptionValue(OPTION_DRIVER);
    final String url = parser.getStringOptionValue(OPTION_URL);
    final String user = parser.getStringOptionValue(OPTION_USER);
    final String password = parser.getStringOptionValue(OPTION_PASSWORD);
    final boolean useJdbcConnection = !Utilities.isBlank(driver)
                                      && !Utilities.isBlank(url);

    String connectionName = null;
    if (prompt)
    {
      defaultConnection = false;
      connectionName = "dbconnection";
    }
    else
    {
      connectionName = parser.getStringOptionValue(OPTION_CONNECTION);
      // Use default connection if no connection is specified
      defaultConnection = Utilities.isBlank(connectionName);
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
    else if (useJdbcConnection)
    {
      dataSource = new PropertiesDataSource(driver, url, user, password);
    }
    else
    {
      dataSource = new PropertiesDataSource(properties, connectionName);
    }

    return dataSource;

  }

  /**
   * Get connection parameters, and creates a connection, and crawls the
   * schema.
   * 
   * @param args
   *        Arguments passed into the program from the command line.
   * @throws PropertiesDataSourceException
   *         On an exception creating the data source
   */
  public static void main(final String[] args)
    throws PropertiesDataSourceException
  {
    CommandLineUtility.checkForHelp(args, "/dbconnector-readme.txt");

    final CommandLineParser parser = new CommandLineParser();

    parser.addOption(new StringOption('f',
                                      OPTION_CONNECTIONSFILE,
                                      "connection.properties"));

    parser.parse(args);

    final String connectionsFileName = parser
      .getStringOptionValue(OPTION_CONNECTIONSFILE);
    final Properties config = Utilities
      .loadProperties(new File(connectionsFileName));
    if (createDataSource(args, config) == null)
    {
      System.exit(2);
    }

  }

  /**
   * Prompts for a database connection.
   * 
   * @param cxnParamsFile
   *        Connection properties file
   * @param connectionName
   *        Connection name
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
    final String driver = OPTION_DRIVER;
    param = (String) prompter.getInput(driver,
                                       cxnParams.getProperty(driver),
                                       InputType.STRING,
                                       false);
    cxnParams.setProperty(connectionName + "." + driver, param);

    // prompt for url
    final String url = OPTION_URL;
    param = (String) prompter.getInput(url,
                                       cxnParams.getProperty(url),
                                       InputType.STRING,
                                       false);
    cxnParams.setProperty(connectionName + "." + url, param);

    // prompt for schema
    final String schema = "schema";
    param = (String) prompter.getInput(schema,
                                       cxnParams.getProperty(schema),
                                       InputType.STRING,
                                       false);
    cxnParams.setProperty(connectionName + "." + schema, param);

    // prompt for user
    final String user = OPTION_USER;
    param = (String) prompter.getInput(user,
                                       cxnParams.getProperty(user),
                                       InputType.STRING,
                                       false);
    cxnParams.setProperty(connectionName + "." + user, param);

    // prompt for password
    final String password = OPTION_PASSWORD;
    param = (String) prompter.getInput(password, cxnParams
      .getProperty(password), InputType.STRING, true);
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

  private static CommandLineParser createCommandLineParser()
  {
    final CommandLineParser parser = new CommandLineParser();

    parser.addOption(new BooleanOption('a', OPTION_TESTALL));
    parser.addOption(new BooleanOption('x', OPTION_PROMPT));
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

  private static void testAllConnections(final Properties properties)
  {
    final String[] groups = new GroupedProperties(properties).groups();

    for (final String element: groups)
    {
      try
      {
        new PropertiesDataSource(properties, element);
      }
      catch (final PropertiesDataSourceException e)
      {
        LOGGER.log(Level.WARNING, "Error testing connection \"" + element
                                  + "\"", e);
      }
      System.err.println();
      System.err.println();
    }
  }

  private Main()
  {
    // Prevent instantiation
  }

}
