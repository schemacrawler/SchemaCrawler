/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2014, Sualeh Fatehi.
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
package schemacrawler.tools.databaseconnector;


import static sf.util.Utility.isBlank;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.commandline.CommandLine;
import schemacrawler.tools.commandline.SchemaCrawlerCommandLine;
import schemacrawler.tools.commandline.SchemaCrawlerHelpCommandLine;
import schemacrawler.tools.options.DatabaseServerType;

public abstract class DatabaseConnector
{

  private final DatabaseServerType dbServerType;

  private final String connectionHelpResource;
  private final DatabaseSystemConnector dbSystemConnector;

  protected DatabaseConnector(final DatabaseServerType dbServerType,
                              final String connectionHelpResource,
                              final DatabaseSystemConnector dbSystemConnector)
  {
    if (dbServerType == null)
    {
      throw new IllegalArgumentException("No database server type provided");
    }
    this.dbServerType = dbServerType;

    if (isBlank(connectionHelpResource))
    {
      throw new IllegalArgumentException("No connection help resource provided");
    }
    this.connectionHelpResource = connectionHelpResource;

    this.dbSystemConnector = dbSystemConnector;
  }

  protected DatabaseConnector(final DatabaseServerType dbServerType,
                              final String connectionHelpResource,
                              final String configResource,
                              final String informationSchemaViewsResourceFolder)
  {
    if (dbServerType == null)
    {
      throw new IllegalArgumentException("No database server type provided");
    }
    this.dbServerType = dbServerType;

    this.connectionHelpResource = connectionHelpResource;

    dbSystemConnector = new DatabaseSystemConnector(configResource,
                                                    informationSchemaViewsResourceFolder);
  }

  public DatabaseServerType getDatabaseServerType()
  {
    return dbServerType;
  }

  public DatabaseSystemConnector getDatabaseSystemConnector()
  {
    return dbSystemConnector;
  }

  public CommandLine newCommandLine(final String[] args)
    throws SchemaCrawlerException
  {
    return new SchemaCrawlerCommandLine(dbSystemConnector, args);
  }

  public CommandLine newHelpCommandLine(final String[] args,
                                        final boolean showVersionOnly)
    throws SchemaCrawlerException
  {
    return new SchemaCrawlerHelpCommandLine(args,
                                            dbServerType,
                                            connectionHelpResource,
                                            showVersionOnly);
  }

}
