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

package schemacrawler.crawl;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.ResultsColumns;
import schemacrawler.schema.TableType;
import schemacrawler.schema.WeakAssociations;
import schemacrawler.schemacrawler.CrawlHandler;
import schemacrawler.schemacrawler.SchemaCrawler;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;

/**
 * SchemaCrawler uses database meta-data to get the details about the
 * schema.
 * 
 * @author Sualeh Fatehi
 */
public final class DatabaseSchemaCrawler
  implements SchemaCrawler
{

  private static final Logger LOGGER = Logger
    .getLogger(DatabaseSchemaCrawler.class.getName());

  /**
   * Gets the result set columns metadata.
   * 
   * @param resultSet
   *        Result set
   * @return Schema
   */
  public static ResultsColumns getResultColumns(final ResultSet resultSet)
  {
    ResultsColumns resultColumns = null;
    try
    {
      final ResultsRetriever resultsRetriever = new ResultsRetriever(resultSet);
      resultColumns = resultsRetriever.retrieveResults();
    }
    catch (final SchemaCrawlerException e)
    {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      resultColumns = null;
    }
    return resultColumns;
  }

  private final Connection connection;

  /**
   * Constructs a SchemaCrawler object, from a connection.
   * 
   * @param connection
   *        An database connection.
   * @throws SchemaCrawlerException
   *         On a crawler exception
   */
  public DatabaseSchemaCrawler(final Connection connection)
    throws SchemaCrawlerException
  {
    if (connection == null)
    {
      throw new SchemaCrawlerException("No connection specified");
    }
    this.connection = connection;

    // Check data source, and obtain the catalog name
    try
    {
      connection.getCatalog();
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      // NOTE: catalog remains null, which is ok for JDBC
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schemacrawler.SchemaCrawler#crawl(schemacrawler.schemacrawler.SchemaCrawlerOptions,
   *      schemacrawler.schemacrawler.CrawlHandler)
   */
  public void crawl(final SchemaCrawlerOptions options,
                    final CrawlHandler handler)
    throws SchemaCrawlerException
  {
    if (handler == null)
    {
      throw new SchemaCrawlerException("No crawl handler specified");
    }

    RetrieverConnection retrieverConnection = null;
    try
    {
      SchemaCrawlerOptions schemaCrawlerOptions = options;
      if (schemaCrawlerOptions == null)
      {
        schemaCrawlerOptions = new SchemaCrawlerOptions();
      }
      retrieverConnection = new RetrieverConnection(connection,
                                                    schemaCrawlerOptions);

      handler.begin();

      final ColumnDataTypes columnDataTypes = crawlColumnDataTypes(retrieverConnection,
                                                                   schemaCrawlerOptions);

      crawlJdbcDriverInfo(retrieverConnection, schemaCrawlerOptions, handler);

      crawlDatabaseInfo(retrieverConnection,
                        schemaCrawlerOptions,
                        handler,
                        columnDataTypes);

      for (final MutableColumnDataType columnDataType: columnDataTypes)
      {
        handler.handle(columnDataType);
      }

      crawlTables(retrieverConnection,
                  schemaCrawlerOptions,
                  handler,
                  columnDataTypes);
      crawlProcedures(retrieverConnection,
                      schemaCrawlerOptions,
                      handler,
                      columnDataTypes);

      handler.end();
    }
    catch (final SQLException e)
    {
      final String errorMessage = e.getMessage();
      LOGGER.log(Level.WARNING, "Database access error: " + errorMessage);
      throw new SchemaCrawlerException(errorMessage, e);
    }
    finally
    {
      if (retrieverConnection != null)
      {
        retrieverConnection.close();
      }
    }
  }

  private ColumnDataTypes crawlColumnDataTypes(final RetrieverConnection retrieverConnection,
                                               final SchemaCrawlerOptions options)
    throws SchemaCrawlerException
  {
    final ColumnDataTypes columnDataTypes = new ColumnDataTypes();
    try
    {
      final SchemaInfoLevel infoLevel = options.getSchemaInfoLevel();
      final DatabaseInfoRetriever retriever = new DatabaseInfoRetriever(retrieverConnection);
      if (infoLevel.isRetrieveColumnDataTypes())
      {
        retriever.retrieveSystemColumnDataTypes(columnDataTypes);
      }
      if (infoLevel.isRetrieveUserDefinedColumnDataTypes())
      {
        retriever.retrieveUserDefinedColumnDataTypes(columnDataTypes);
      }
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Exception retrieving column data type information",
                                       e);
    }

    return columnDataTypes;
  }

  private MutableDatabaseInfo crawlDatabaseInfo(final RetrieverConnection retrieverConnection,
                                                final SchemaCrawlerOptions options,
                                                final CrawlHandler handler,
                                                final ColumnDataTypes columnDataTypes)
    throws SchemaCrawlerException
  {
    MutableDatabaseInfo dbInfo;
    try
    {
      final SchemaInfoLevel infoLevel = options.getSchemaInfoLevel();
      final DatabaseInfoRetriever retriever = new DatabaseInfoRetriever(retrieverConnection);
      dbInfo = retriever.retrieveDatabaseInfo();
      if (infoLevel.isRetrieveDatabaseInfo())
      {
        if (infoLevel.isRetrieveAdditionalDatabaseInfo())
        {
          retriever.retrieveAdditionalDatabaseInfo(dbInfo);
        }
      }
      dbInfo.setSystemColumnDataTypes(columnDataTypes
        .lookupColumnDataTypes(null));
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Exception retrieving database information",
                                       e);
    }
    handler.handle(dbInfo);

    return dbInfo;
  }

  private void crawlJdbcDriverInfo(final RetrieverConnection retrieverConnection,
                                   final SchemaCrawlerOptions options,
                                   final CrawlHandler handler)
    throws SchemaCrawlerException
  {
    final SchemaInfoLevel infoLevel = options.getSchemaInfoLevel();
    if (infoLevel.isRetrieveJdbcDriverInfo())
    {
      MutableJdbcDriverInfo driverInfo;
      try
      {
        final JdbcDriverInfoRetriever retriever = new JdbcDriverInfoRetriever(retrieverConnection);
        driverInfo = retriever.retrieveJdbcDriverInfo();
      }
      catch (final SQLException e)
      {
        throw new SchemaCrawlerException("Exception retrieving JDBC driver information",
                                         e);
      }
      handler.handle(driverInfo);
    }
  }

  private void crawlProcedures(final RetrieverConnection retrieverConnection,
                               final SchemaCrawlerOptions options,
                               final CrawlHandler handler,
                               final ColumnDataTypes columnDataTypes)
    throws SchemaCrawlerException
  {
    final SchemaInfoLevel infoLevel = options.getSchemaInfoLevel();
    final boolean retrieveProcedures = options.isShowStoredProcedures()
                                       && infoLevel.isRetrieveProcedures();
    if (!retrieveProcedures)
    {
      return;
    }

    ProcedureRetriever retriever;
    NamedObjectList<MutableProcedure> procedures;
    try
    {
      retriever = new ProcedureRetriever(retrieverConnection);
      final ProcedureExRetriever retrieverExtra = new ProcedureExRetriever(retrieverConnection);
      procedures = retriever.retrieveProcedures(options
        .getProcedureInclusionRule());
      if (infoLevel.isRetrieveProcedureInformation())
      {
        retrieverExtra.retrieveProcedureInformation(procedures);
      }
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Exception retrieving procedures", e);
    }

    for (final MutableProcedure procedure: procedures)
    {
      if (infoLevel.isRetrieveProcedureColumns())
      {
        try
        {
          retriever.retrieveProcedureColumns(procedure, options
            .getProcedureColumnInclusionRule(), columnDataTypes);
        }
        catch (final SQLException e)
        {
          throw new SchemaCrawlerException("Error retrieving metadata for procedure "
                                               + procedure,
                                           e);
        }
      }
      // set comparators
      procedure.setColumnComparator(NamedObjectSort.getNamedObjectSort(options
        .isAlphabeticalSortForProcedureColumns()));
      // handle procedure
      handler.handle(procedure);
    }
  }

  private void crawlTables(final RetrieverConnection retrieverConnection,
                           final SchemaCrawlerOptions options,
                           final CrawlHandler handler,
                           final ColumnDataTypes columnDataTypes)
    throws SchemaCrawlerException
  {
    final SchemaInfoLevel infoLevel = options.getSchemaInfoLevel();
    final boolean retrieveTables = infoLevel.isRetrieveTables();
    if (!retrieveTables)
    {
      return;
    }

    TableRetriever retriever;
    TableExRetriever retrieverExtra;
    NamedObjectList<MutableTable> tables;
    try
    {
      retriever = new TableRetriever(retrieverConnection);
      retrieverExtra = new TableExRetriever(retrieverConnection);
      tables = retriever.retrieveTables(options.getTableTypes(), options
        .getTableInclusionRule());
      if (infoLevel.isRetrieveCheckConstraintInformation())
      {
        retrieverExtra.retrieveCheckConstraintInformation(tables);
      }
      if (infoLevel.isRetrieveViewInformation())
      {
        retrieverExtra.retrieveViewInformation(tables);
      }
      if (infoLevel.isRetrieveTablePrivileges())
      {
        retrieverExtra.retrieveTablePrivileges(tables);
      }
      if (infoLevel.isRetrieveTriggerInformation())
      {
        retrieverExtra.retrieveTriggerInformation(tables);
      }
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Exception retrieving tables", e);
    }

    for (final MutableTable table: tables)
    {
      if (infoLevel.isRetrieveTableColumns())
      {
        try
        {
          retriever.retrieveColumns(tables, table, options
            .getColumnInclusionRule(), columnDataTypes);
          retriever.retrievePrimaryKeys(table);
          if (table.getType() != TableType.view)
          {
            if (infoLevel.isRetrieveForeignKeys())
            {
              retriever.retrieveForeignKeys(tables, table);
            }
            if (infoLevel.isRetrieveIndices())
            {
              retriever.retrieveIndices(table, true);
              retriever.retrieveIndices(table, false);
            }
          }
        }
        catch (final SQLException e)
        {
          throw new SchemaCrawlerException("Error retrieving metadata for table "
                                               + table,
                                           e);
        }
        try
        {
          if (infoLevel.isRetrieveTableColumnPrivileges())
          {
            retrieverExtra.retrieveTableColumnPrivileges(table, table
              .getColumnsList());
          }
        }
        catch (final SQLException e)
        {
          LOGGER.log(Level.WARNING,
                     "Error retrieving column privileges for table " + table,
                     e);
        }
      }
      // set comparators
      table.setColumnComparator(NamedObjectSort.getNamedObjectSort(options
        .isAlphabeticalSortForTableColumns()));
      table.setForeignKeyComparator(NamedObjectSort.getNamedObjectSort(options
        .isAlphabeticalSortForForeignKeys()));
      table.setIndexComparator(NamedObjectSort.getNamedObjectSort(options
        .isAlphabeticalSortForIndexes()));
      // handle table
      handler.handle(table);
    }

    if (infoLevel.isRetrieveWeakAssociations())
    {
      final TableAnalyzer tableAnalyzer = new TableAnalyzer();
      final WeakAssociations weakAssociations = tableAnalyzer
        .analyzeTables(tables);
      handler.handle(weakAssociations);
    }
  }

}
