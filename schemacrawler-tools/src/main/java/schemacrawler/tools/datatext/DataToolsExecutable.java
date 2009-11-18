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

package schemacrawler.tools.datatext;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.Executable;

/**
 * Basic SchemaCrawler executor.
 * 
 * @author Sualeh Fatehi
 */
public class DataToolsExecutable
  extends Executable<DataTextFormatOptions>
{

  private static final Logger LOGGER = Logger
    .getLogger(DataToolsExecutable.class.getName());

  public DataToolsExecutable()
  {
    this(null);
  }

  public DataToolsExecutable(final String name)
  {
    super(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.Executable#execute(javax.sql.DataSource)
   */
  @Override
  public void execute(final Connection connection)
    throws Exception
  {
    if (connection == null)
    {
      throw new IllegalArgumentException("No connection provided");
    }
    initialize();

    final DataTextFormatter dataFormatter = new DataTextFormatter(toolOptions);
    final Query query = toolOptions.getQuery();

    Statement statement = null;
    ResultSet resultSet = null;
    try
    {
      LOGGER.fine("Executing: " + query);
      statement = connection.createStatement();
      resultSet = statement.executeQuery(query.getQuery());

      dataFormatter.begin();
      dataFormatter.handleData(query.getName(), resultSet);
      dataFormatter.end();
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException(e.getMessage() + " - when executing - "
                                       + query, e);
    }
    finally
    {
      try
      {
        if (statement != null)
        {
          statement.close();
        }
        if (resultSet != null)
        {
          resultSet.close();
        }
      }
      catch (final SQLException e)
      {
        LOGGER.log(Level.WARNING,
                   "Connection resources could not be released",
                   e);
      }
    }
  }

}
