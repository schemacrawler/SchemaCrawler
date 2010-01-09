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

package schemacrawler.tools.text.operation;


import java.sql.Connection;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.text.base.BaseSchemaCrawlerTextExecutable;
import schemacrawler.tools.text.base.DatabaseTraversalHandler;

/**
 * Basic SchemaCrawler executor.
 * 
 * @author Sualeh Fatehi
 */
public final class OperationExecutable
  extends BaseSchemaCrawlerTextExecutable
{

  private static final long serialVersionUID = -6824567755397315920L;

  private OperationOptions operationOptions;

  public OperationExecutable(final String command)
  {
    super(command);
  }

  public final OperationOptions getOperationOptions()
  {
    final OperationOptions operationOptions;
    if (this.operationOptions == null)
    {
      operationOptions = new OperationOptions(additionalConfiguration);
    }
    else
    {
      operationOptions = this.operationOptions;
    }
    return operationOptions;
  }

  public final void setOperationOptions(final OperationOptions operationOptions)
  {
    this.operationOptions = operationOptions;
  }

  @Override
  protected DatabaseTraversalHandler getDatabaseTraversalHandler(final Connection connection)
    throws SchemaCrawlerException
  {
    final DatabaseTraversalHandler handler;

    // Determine the operation, or whether this command is a query
    Operation operation = null;
    try
    {
      operation = Operation.valueOf(command);
    }
    catch (final IllegalArgumentException e)
    {
      operation = null;
    }

    // Get the query
    final Query query;
    if (operation == null)
    {
      final String queryName = command;
      final String queryString;
      if (additionalConfiguration != null)
      {
        queryString = additionalConfiguration.get(queryName);
      }
      else
      {
        queryString = null;
      }
      query = new Query(queryName, queryString);
    }
    else
    {
      query = operation.getQuery();
    }

    final OperationOptions operationOptions = getOperationOptions();
    handler = new OperationHandler(operation,
                                   query,
                                   operationOptions,
                                   outputOptions,
                                   connection);

    return handler;
  }

}
