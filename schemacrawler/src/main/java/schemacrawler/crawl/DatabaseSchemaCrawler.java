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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.ColumnMap;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.ProcedureColumn;
import schemacrawler.schema.ResultsColumns;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.CrawlHandler;
import schemacrawler.schemacrawler.InclusionRule;
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
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      resultColumns = null;
    }
    return resultColumns;
  }

  private final MutableDatabase database;
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

    database = new MutableDatabase("database");
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

      crawlSchemas(retrieverConnection, schemaCrawlerOptions);
      crawlDatabaseInfo(retrieverConnection, schemaCrawlerOptions);
      handler.handle(database.getSchemaCrawlerInfo());
      handler.handle(database.getDatabaseInfo());
      handler.handle(database.getJdbcDriverInfo());

      crawlColumnDataTypes(retrieverConnection, schemaCrawlerOptions);
      for (final ColumnDataType columnDataType: database
        .getSystemColumnDataTypes())
      {
        handler.handle(columnDataType);
      }
      for (final Catalog catalog: database.getCatalogs())
      {
        for (final Schema schema: catalog.getSchemas())
        {
          for (final ColumnDataType columnDataType: schema.getColumnDataTypes())
          {
            handler.handle(columnDataType);
          }
        }
      }

      crawlTables(retrieverConnection, schemaCrawlerOptions, handler);
      crawlProcedures(retrieverConnection, schemaCrawlerOptions, handler);

      handler.end();

      if (handler instanceof CachingCrawlHandler)
      {
        ((CachingCrawlHandler) handler).setDatabase(database);
      }
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Database access exception", e);
    }
  }

  private void crawlColumnDataTypes(final RetrieverConnection retrieverConnection,
                                    final SchemaCrawlerOptions options)
    throws SchemaCrawlerException
  {
    try
    {
      final SchemaInfoLevel infoLevel = options.getSchemaInfoLevel();
      final DatabaseInfoRetriever retriever = new DatabaseInfoRetriever(retrieverConnection,
                                                                        database);
      if (infoLevel.isRetrieveColumnDataTypes())
      {
        retriever.retrieveSystemColumnDataTypes();
      }
      if (infoLevel.isRetrieveUserDefinedColumnDataTypes())
      {
        for (final SchemaReference schemaNameObject: retriever.getSchemaNames())
        {
          retriever.retrieveUserDefinedColumnDataTypes(schemaNameObject
            .getSchemaName(), schemaNameObject.getSchemaName());
        }
      }
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Exception retrieving column data type information",
                                       e);
    }
  }

  private void crawlDatabaseInfo(final RetrieverConnection retrieverConnection,
                                 final SchemaCrawlerOptions options)
    throws SchemaCrawlerException
  {
    try
    {

      final SchemaInfoLevel infoLevel = options.getSchemaInfoLevel();
      final DatabaseInfoRetriever retriever = new DatabaseInfoRetriever(retrieverConnection,
                                                                        database);

      if (infoLevel.isRetrieveSchemaCrawlerInfo())
      {
        retriever.retrieveSchemaCrawlerInfo();
        if (infoLevel.isRetrieveAdditionalSchemaCrawlerInfo())
        {
          retriever.retrieveAdditionalSchemaCrawlerInfo();
        }
      }
      if (infoLevel.isRetrieveDatabaseInfo())
      {
        retriever.retrieveDatabaseInfo();
        if (infoLevel.isRetrieveAdditionalDatabaseInfo())
        {
          retriever.retrieveAdditionalDatabaseInfo();
        }
      }
      if (infoLevel.isRetrieveJdbcDriverInfo())
      {
        retriever.retrieveJdbcDriverInfo();
        if (infoLevel.isRetrieveAdditionalJdbcDriverInfo())
        {
          retriever.retrieveAdditionalJdbcDriverInfo();
        }
      }
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Exception retrieving database information",
                                       e);
    }
  }

  private void crawlProcedures(final RetrieverConnection retrieverConnection,
                               final SchemaCrawlerOptions options,
                               final CrawlHandler handler)
    throws SchemaCrawlerException
  {
    final SchemaInfoLevel infoLevel = options.getSchemaInfoLevel();
    final boolean retrieveProcedures = options.isShowStoredProcedures()
                                       && infoLevel.isRetrieveProcedures();
    if (!retrieveProcedures)
    {
      return;
    }

    final ProcedureRetriever retriever;
    final ProcedureExRetriever retrieverExtra;
    try
    {
      retriever = new ProcedureRetriever(retrieverConnection, database);
      retrieverExtra = new ProcedureExRetriever(retrieverConnection, database);
      for (final SchemaReference schemaNameObject: retriever.getSchemaNames())
      {
        retriever.retrieveProcedures(schemaNameObject.getCatalogName(),
                                     schemaNameObject.getSchemaName(),
                                     options.getProcedureInclusionRule());
      }
      final NamedObjectList<MutableProcedure> allProcedures = database
        .getAllProcedures();
      for (final MutableProcedure procedure: allProcedures)
      {
        if (infoLevel.isRetrieveProcedureColumns())
        {
          retriever.retrieveProcedureColumns(procedure, options
            .getProcedureColumnInclusionRule());
        }
        if (!grepMatch(options, procedure))
        {
          ((MutableSchema) procedure.getSchema()).removeProcedure(procedure);
          allProcedures.remove(procedure);
        }
      }

      if (infoLevel.isRetrieveProcedureInformation())
      {
        retrieverExtra.retrieveProcedureInformation();
      }

      for (final MutableProcedure procedure: allProcedures)
      {
        // Set comparators
        procedure.setColumnComparator(NamedObjectSort
          .getNamedObjectSort(options.isAlphabeticalSortForProcedureColumns()));
        // Handle procedure
        handler.handle(procedure);
      }
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Exception retrieving procedures", e);
    }
  }

  private void crawlSchemas(final RetrieverConnection retrieverConnection,
                            final SchemaCrawlerOptions options)
    throws SchemaCrawlerException
  {
    try
    {
      final SchemaRetriever retriever = new SchemaRetriever(retrieverConnection,
                                                            database);

      retriever.retrieveSchemas(options.getCatalogInclusionRule(), options
        .getSchemaInclusionRule());
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Exception retrieving database information",
                                       e);
    }
  }

  private void crawlTables(final RetrieverConnection retrieverConnection,
                           final SchemaCrawlerOptions options,
                           final CrawlHandler handler)
    throws SchemaCrawlerException
  {
    final SchemaInfoLevel infoLevel = options.getSchemaInfoLevel();
    final boolean retrieveTables = infoLevel.isRetrieveTables();
    if (!retrieveTables)
    {
      return;
    }

    final TableRetriever retriever;
    final TableExRetriever retrieverExtra;
    try
    {
      retriever = new TableRetriever(retrieverConnection, database);
      retrieverExtra = new TableExRetriever(retrieverConnection, database);

      for (final SchemaReference schemaNameObject: retriever.getSchemaNames())
      {
        retriever.retrieveTables(schemaNameObject.getCatalogName(),
                                 schemaNameObject.getSchemaName(),
                                 options.getTableTypes(),
                                 options.getTableInclusionRule());
      }

      final NamedObjectList<MutableTable> allTables = database.getAllTables();
      for (final MutableTable table: allTables)
      {
        if (infoLevel.isRetrieveTableColumns())
        {
          retriever.retrieveColumns(table, options.getColumnInclusionRule());
        }
        if (!grepMatch(options, table))
        {
          ((MutableSchema) table.getSchema()).removeTable(table);
          allTables.remove(table);
        }
      }

      if (infoLevel.isRetrieveCheckConstraintInformation())
      {
        retrieverExtra.retrieveCheckConstraintInformation();
      }
      if (infoLevel.isRetrieveTriggerInformation())
      {
        retrieverExtra.retrieveTriggerInformation();
      }
      if (infoLevel.isRetrieveViewInformation())
      {
        retrieverExtra.retrieveViewInformation();
      }
      if (infoLevel.isRetrieveTablePrivileges())
      {
        retrieverExtra.retrieveTablePrivileges();
      }
      if (infoLevel.isRetrieveTableColumnPrivileges())
      {
        retrieverExtra.retrieveTableColumnPrivileges();
      }

      final NamedObjectSort tablesSort = NamedObjectSort
        .getNamedObjectSort(options.isAlphabeticalSortForTables());
      if (tablesSort == NamedObjectSort.natural
          && !infoLevel.isRetrieveForeignKeys())
      {
        LOGGER
          .log(Level.WARNING,
               "Foreign-keys are not being retrieved, so tables cannot be sorted using the natural sort order");
      }
      for (final MutableTable table: allTables)
      {
        final boolean isView = table instanceof MutableView;
        if (!isView && infoLevel.isRetrieveTableColumns())
        {
          retriever.retrievePrimaryKey(table);
          if (infoLevel.isRetrieveIndices())
          {
            retriever.retrieveIndices(table, true);
            retriever.retrieveIndices(table, false);
            //
            table.replacePrimaryKey();
          }
          if (infoLevel.isRetrieveForeignKeys())
          {
            retriever.retrieveForeignKeys(table);
          }
        }
        // Set comparators
        ((MutableSchema) table.getSchema()).setTablesSortOrder(tablesSort);
        table.setColumnsSortOrder(NamedObjectSort.getNamedObjectSort(options
          .isAlphabeticalSortForTableColumns()));
        table.setForeignKeysSortOrder(NamedObjectSort
          .getNamedObjectSort(options.isAlphabeticalSortForForeignKeys()));
        table.setIndicesSortOrder(NamedObjectSort.getNamedObjectSort(options
          .isAlphabeticalSortForIndexes()));
      }

      // Set the sort order for tables after all the foreign keys have
      // been obtained, since the natural sort order depends on the
      // foreign keys
      allTables.setSortOrder(tablesSort);
      final TablesGraph tablesGraph = new TablesGraph(allTables);
      tablesGraph.setTablesSortIndices();

      for (final MutableTable table: allTables)
      {
        // Handle table
        handler.handle(table);
      }

      if (infoLevel.isRetrieveWeakAssociations())
      {
        final List<ColumnMap> weakAssociations = new ArrayList<ColumnMap>();
        final WeakAssociationsAnalyzer tableAnalyzer = new WeakAssociationsAnalyzer(allTables,
                                                                                    weakAssociations);
        tableAnalyzer.analyzeTables();
        handler.handle(weakAssociations.toArray(new ColumnMap[weakAssociations
          .size()]));
      }
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Exception retrieving tables", e);
    }

  }

  /**
   * Special case for "grep" like functionality. Handle procedure if a
   * procedure column inclusion rule is found, and at least one column
   * matches the rule.
   * 
   * @param options
   *        Options
   * @param procedure
   *        Procedure to check
   * @return Whether the column should be included
   */
  private boolean grepMatch(final SchemaCrawlerOptions options,
                            final Procedure procedure)
  {
    final InclusionRule grepProcedureColumnInclusionRule = options
      .getGrepProcedureColumnInclusionRule();
    final boolean invertMatch = options.isGrepInvertMatch();

    boolean include = false;
    final ProcedureColumn[] columns = procedure.getColumns();
    if (columns.length == 0)
    {
      include = true;
    }
    else
    {
      for (final ProcedureColumn column: columns)
      {
        if (grepProcedureColumnInclusionRule.include(column.getFullName()))
        {
          // We found a column that should be included,
          // so handle the procedure
          include = true;
          break;
        }
      }
    }
    if (invertMatch)
    {
      include = !include;
    }

    if (!include)
    {
      LOGGER.log(Level.FINE, "Removing procedure " + procedure
                             + " since it does not match the grep pattern");
    }

    return include;
  }

  /**
   * Special case for "grep" like functionality. Handle table if a table
   * column inclusion rule is found, and at least one column matches the
   * rule.
   * 
   * @param options
   *        Options
   * @param table
   *        Table to check
   * @return Whether the column should be included
   */
  private boolean grepMatch(final SchemaCrawlerOptions options,
                            final Table table)
  {
    final InclusionRule grepColumnInclusionRule = options
      .getGrepColumnInclusionRule();
    final boolean invertMatch = options.isGrepInvertMatch();

    boolean include = false;
    final Column[] columns = table.getColumns();
    if (columns.length == 0)
    {
      include = true;
    }
    else
    {
      for (final Column column: columns)
      {
        if (grepColumnInclusionRule.include(column.getFullName()))
        {
          // We found a column that should be included, so handle the
          // table
          include = true;
          break;
        }
      }
    }
    if (invertMatch)
    {
      include = !include;
    }

    if (!include)
    {
      LOGGER.log(Level.FINE, "Removing table " + table
                             + " since it does not match the grep pattern");
    }

    return include;
  }

}
