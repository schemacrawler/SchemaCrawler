/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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


import static sf.util.DatabaseUtility.createStatement;
import static sf.util.DatabaseUtility.executeSql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.executable.BaseStagedExecutable;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.traversal.DataTraversalHandler;
import schemacrawler.utility.NamedObjectSort;
import schemacrawler.utility.Query;

/**
 * Basic SchemaCrawler executor.
 *
 * @author Sualeh Fatehi
 */
public final class OperationExecutable
  extends BaseStagedExecutable
{

  private static final Logger LOGGER = Logger
    .getLogger(OperationExecutable.class.getName());

  private OperationOptions operationOptions;

  public OperationExecutable(final String command)
  {
    super(command);
  }

  @Override
  public void executeOn(final Catalog catalog, final Connection connection)
    throws Exception
  {
    loadOperationOptions();

    final DataTraversalHandler handler = getDataTraversalHandler();
    final Query query = getQuery();

    try (final Statement statement = createStatement(connection);)
    {

      handler.begin();

      handler.handleInfoStart();
      handler.handle(catalog.getSchemaCrawlerInfo());
      handler.handle(catalog.getDatabaseInfo());
      handler.handle(catalog.getJdbcDriverInfo());
      handler.handleInfoEnd();

      if (query.isQueryOver())
      {
        for (Table table: getSortedTables(catalog))
        {
          final String sql = query
            .getQuery(table,
                      operationOptions.isAlphabeticalSortForTableColumns());

          LOGGER.log(Level.FINE,
                     String.format("Executing query for table %s: %s",
                                   table.getFullName(), sql));
          try (final ResultSet results = executeSql(statement, sql);)
          {
            handler.handleData(table, results);
          }
        }
      }
      else
      {
        final String sql = query.getQuery();
        try (final ResultSet results = executeSql(statement, sql);)
        {
          handler.handleData(query, results);
        }
      }

      handler.end();

    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Cannot perform operation", e);
    }
  }

  private List<? extends Table> getSortedTables(final Catalog catalog)
  {
    final List<? extends Table> tables = new ArrayList<>(catalog.getTables());
    Collections.sort(tables, NamedObjectSort
      .getNamedObjectSort(getOperationOptions().isAlphabeticalSortForTables()));
    return tables;
  }

  public final OperationOptions getOperationOptions()
  {
    loadOperationOptions();
    return operationOptions;
  }

  public final void setOperationOptions(final OperationOptions operationOptions)
  {
    this.operationOptions = operationOptions;
  }

  private DataTraversalHandler getDataTraversalHandler()
    throws SchemaCrawlerException
  {
    final Operation operation = getOperation();

    final OperationOptions operationOptions = getOperationOptions();
    final DataTraversalHandler formatter;
    final TextOutputFormat outputFormat = TextOutputFormat
      .fromFormat(outputOptions.getOutputFormatValue());
    if (outputFormat == TextOutputFormat.json)
    {
      formatter = new DataJsonFormatter(operation, operationOptions,
                                        outputOptions);
    }
    else
    {
      formatter = new DataTextFormatter(operation, operationOptions,
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

  private void loadOperationOptions()
  {
    if (operationOptions == null)
    {
      operationOptions = new OperationOptionsBuilder()
        .fromConfig(additionalConfiguration).toOptions();
    }
  }

}
