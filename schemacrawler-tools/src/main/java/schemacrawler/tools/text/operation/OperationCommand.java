/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.tools.text.operation;


import static java.util.Objects.requireNonNull;
import static schemacrawler.schemacrawler.QueryUtility.executeAgainstTable;
import static sf.util.DatabaseUtility.createStatement;
import static sf.util.DatabaseUtility.executeSql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.Identifiers;
import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.executable.BaseSchemaCrawlerCommand;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.traversal.DataTraversalHandler;
import schemacrawler.utility.NamedObjectSort;
import schemacrawler.SchemaCrawlerLogger;
import sf.util.StringFormat;

/**
 * Basic SchemaCrawler executor.
 *
 * @author Sualeh Fatehi
 */
public final class OperationCommand
  extends BaseSchemaCrawlerCommand
{
  private static final SchemaCrawlerLogger LOGGER =
    SchemaCrawlerLogger.getLogger(OperationCommand.class.getName());

  private OperationOptions operationOptions;

  public OperationCommand(final String command)
  {
    super(command);
  }

  @Override
  public void checkAvailability()
    throws Exception
  {
    // Operations are always available
  }

  @Override
  public void initialize()
    throws Exception
  {
    super.initialize();
    loadOperationOptions();
  }

  @Override
  public void execute()
    throws Exception
  {
    checkCatalog();

    if (!isOutputFormatSupported())
    {
      LOGGER.log(Level.INFO,
                 new StringFormat(
                   "Output format <%s> not supported for command <%s>",
                   outputOptions.getOutputFormatValue(),
                   getCommand()));
      return;
    }

    final DataTraversalHandler handler = getDataTraversalHandler();
    final Query query = getQuery();

    handler.begin();

    handler.handleInfoStart();
    handler.handle(catalog.getDatabaseInfo());
    handler.handle(catalog.getJdbcDriverInfo());
    handler.handleInfoEnd();

    if (query.isQueryOver())
    {
      // This is a special instance of identifiers that does not use
      // the configuration from the SchemaCrawler configuration
      // properties file, since the database always needs identifiers
      // to be quoted in SQL queries if they contain spaces in the
      // name
      final String identifierQuoteString =
        identifiers.getIdentifierQuoteString();
      final Identifiers identifiers = Identifiers
        .identifiers()
        .withIdentifierQuoteString(identifierQuoteString)
        .build();

      try (final Statement statement = createStatement(connection))
      {
        for (final Table table : getSortedTables(catalog))
        {
          final boolean isAlphabeticalSortForTableColumns =
            operationOptions.isAlphabeticalSortForTableColumns();
          try (
            final ResultSet results = executeAgainstTable(query,
                                                          statement,
                                                          table,
                                                          isAlphabeticalSortForTableColumns,
                                                          identifiers)
          )
          {
            handler.handleData(table, results);
          }
          catch (final SQLException e)
          {
            LOGGER.log(Level.WARNING,
                       new StringFormat("Bad operation for table <%s>", table),
                       e);
          }
        }
      }
    }
    else
    {
      final String sql = query.getQuery();
      try (
        final Statement statement = createStatement(connection);
        final ResultSet results = executeSql(statement, sql)
      )
      {
        handler.handleData(query, results);
      }
    }

    handler.end();

  }

  @Override
  public boolean usesConnection()
  {
    return true;
  }

  public OperationOptions getOperationOptions()
  {
    return operationOptions;
  }

  public final void setOperationOptions(final OperationOptions operationOptions)
  {
    this.operationOptions =
      requireNonNull(operationOptions, "No operation options provided");
  }

  private DataTraversalHandler getDataTraversalHandler()
    throws SchemaCrawlerException
  {
    final Operation operation = getOperation();

    final TextOutputFormat outputFormat =
      TextOutputFormat.fromFormat(outputOptions.getOutputFormatValue());
    final String identifierQuoteString = identifiers.getIdentifierQuoteString();

    final DataTraversalHandler formatter = new DataTextFormatter(operation,
                                                                 operationOptions,
                                                                 outputOptions,
                                                                 identifierQuoteString);
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
    catch (final IllegalArgumentException | NullPointerException e)
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
      final String queryString = additionalConfiguration.get(queryName);
      query = new Query(queryName, queryString);
    }
    else
    {
      query = operation.getQuery();
    }

    return query;
  }

  private List<? extends Table> getSortedTables(final Catalog catalog)
  {
    final List<? extends Table> tables = new ArrayList<>(catalog.getTables());
    tables.sort(NamedObjectSort.getNamedObjectSort(operationOptions.isAlphabeticalSortForTables()));
    return tables;
  }

  private boolean isOutputFormatSupported()
  {
    final String outputFormatValue = outputOptions.getOutputFormatValue();
    final boolean isOutputFormatSupported =
      TextOutputFormat.isSupportedFormat(outputFormatValue);
    return isOutputFormatSupported;
  }

  private void loadOperationOptions()
  {
    if (operationOptions == null)
    {
      operationOptions = OperationOptionsBuilder
        .builder()
        .fromConfig(additionalConfiguration)
        .toOptions();
    }
  }

}
