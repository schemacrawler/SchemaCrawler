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


import schemacrawler.schemacrawler.Config;

/**
 * Parses a command line, and creates a data-source.
 */
public final class BundledDriverDatabaseConnector
  extends BaseDatabaseConnector
{

  /**
   * Parses a command line, and creates a data-source.
   * 
   * @param args
   *        Command line arguments
   * @param providedConfig
   *        Connection properties
   * @throws DatabaseConnectorException
   *         On an exception
   */
  public BundledDriverDatabaseConnector(final String[] args,
                                        final Config providedConfig)
    throws DatabaseConnectorException
  {
    super(providedConfig);

    final BundledDriverOptions options = new BundledDriverOptionsParser(args)
      .getOptions();

    // Check arguments
    // if (!options.hasUser())
    // {
    // throw new
    // DatabaseConnectorException("Please provide the user name");
    // }
    if (!options.hasPassword())
    {
      throw new DatabaseConnectorException("Please provide the password");
    }

    if (options.hasHost())
    {
      configPut("host", options.getHost());
    }
    if (options.hasPort())
    {
      configPut("port", String.valueOf(options.getPort()));
    }
    if (options.hasDatabase())
    {
      configPut("database", options.getDatabase());
    }

    if (options.hasSchemaPattern())
    {
      configPut("schemapattern", options.getSchemapattern());
    }

    configPut("user", options.getUser());
    configPut("password", options.getPassword());

    substituteVariables();
  }

}
