/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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

import schemacrawler.schema.Database;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.executable.BaseExecutable;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.traversal.DataTraversalHandler;

/**
 * Basic SchemaCrawler executor.
 * 
 * @author Sualeh Fatehi
 */
public final class OperationExecutable
  extends BaseExecutable
{

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
  protected void executeOn(final Database database, final Connection connection)
    throws Exception
  {
    final DataTraversalHandler formatter = getDataTraversalHandler();
    final Query query = getQuery();

    final OperationHandler handler = new OperationHandler();
    handler.setDatabase(database);
    handler.setConnection(connection);
    handler.setFormatter(formatter);
    handler.setQuery(query);

    handler.traverse();
  }

  private DataTraversalHandler getDataTraversalHandler()
    throws SchemaCrawlerException
  {
    final Operation operation = getOperation();

    final OperationOptions operationOptions = getOperationOptions();
    final DataTraversalHandler formatter;
    final OutputFormat outputFormat = outputOptions.getOutputFormat();
    if (outputFormat == OutputFormat.json)
    {
      formatter = new DataJsonFormatter(operation,
                                        operationOptions,
                                        outputOptions);
    }
    else
    {
      formatter = new DataTextFormatter(operation,
                                        operationOptions,
                                        outputOptions);
    }
    return formatter;
  }

  /**
   * Determine the operation, or whether this command is a query.
   */
  private Operation getOperation()
  {
    Operation operation = null;
    try
    {
      operation = Operation.valueOf(command);
    }
    catch (final IllegalArgumentException e)
    {
      operation = null;
    }
    return operation;
  }

  private Query getQuery()
  {
    final Operation operation = getOperation();
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

    return query;
  }

}
