/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
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

import schemacrawler.schema.Column;
import schemacrawler.schema.Database;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.ProcedureColumn;
import schemacrawler.schema.ResultsColumns;
import schemacrawler.schema.Table;
import schemacrawler.schema.Trigger;
import schemacrawler.schema.View;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;

/**
 * SchemaCrawler uses database meta-data to get the details about the
 * schema.
 * 
 * @author Sualeh Fatehi
 */
public final class SchemaCrawler
{

  private static final Logger LOGGER = Logger.getLogger(SchemaCrawler.class
    .getName());

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
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      resultColumns = null;
    }
    return resultColumns;
  }

  private static void crawlColumnDataTypes(final MutableDatabase database,
                                           final RetrieverConnection retrieverConnection,
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

  private static void crawlDatabaseInfo(final MutableDatabase database,
                                        final RetrieverConnection retrieverConnection,
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

  private static void crawlProcedures(final MutableDatabase database,
                                      final RetrieverConnection retrieverConnection,
                                      final SchemaCrawlerOptions options)
    throws SchemaCrawlerException
  {
    final SchemaInfoLevel infoLevel = options.getSchemaInfoLevel();
    final boolean retrieveProcedures = infoLevel.isRetrieveProcedures();
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
      }
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Exception retrieving procedures", e);
    }
  }

  private static void crawlSchemas(final MutableDatabase database,
                                   final RetrieverConnection retrieverConnection,
                                   final SchemaCrawlerOptions options)
    throws SchemaCrawlerException
  {
    try
    {
      final SchemaRetriever retriever = new SchemaRetriever(retrieverConnection,
                                                            database);

      retriever.retrieveSchemas(options.getSchemaInclusionRule());
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Exception retrieving database information",
                                       e);
    }
  }

  private static void crawlTables(final MutableDatabase database,
                                  final RetrieverConnection retrieverConnection,
                                  final SchemaCrawlerOptions options)
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
                                 options.getTableNamePattern(),
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
  private static boolean grepMatch(final SchemaCrawlerOptions options,
                                   final Procedure procedure)
  {
    final InclusionRule grepProcedureColumnInclusionRule = options
      .getGrepProcedureColumnInclusionRule();
    final InclusionRule grepDefinitionInclusionRule = options
      .getGrepDefinitionInclusionRule();
    final boolean invertMatch = options.isGrepInvertMatch();

    final boolean checkIncludeForColumns = grepProcedureColumnInclusionRule != null;
    final boolean checkIncludeForDefinitions = grepDefinitionInclusionRule != null;

    if (!checkIncludeForColumns && !checkIncludeForDefinitions)
    {
      return true;
    }

    boolean includeForColumns = false;
    boolean includeForDefinitions = false;
    final ProcedureColumn[] columns = procedure.getColumns();
    if (columns.length == 0)
    {
      includeForColumns = true;
    }
    else
    {
      for (final ProcedureColumn column: columns)
      {
        if (checkIncludeForColumns)
        {
          if (grepProcedureColumnInclusionRule.include(column.getFullName()))
          {
            includeForColumns = true;
            break;
          }
        }
      }
    }
    // Additional include checks for definitions
    if (checkIncludeForDefinitions)
    {
      if (grepDefinitionInclusionRule.include(procedure.getRemarks()))
      {
        includeForDefinitions = true;
      }
      if (grepDefinitionInclusionRule.include(procedure.getDefinition()))
      {
        includeForDefinitions = true;
      }
    }

    boolean include = includeForColumns || includeForDefinitions;
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
  private static boolean grepMatch(final SchemaCrawlerOptions options,
                                   final Table table)
  {
    final InclusionRule grepColumnInclusionRule = options
      .getGrepColumnInclusionRule();
    final InclusionRule grepDefinitionInclusionRule = options
      .getGrepDefinitionInclusionRule();
    final boolean invertMatch = options.isGrepInvertMatch();

    final boolean checkIncludeForColumns = grepColumnInclusionRule != null;
    final boolean checkIncludeForDefinitions = grepDefinitionInclusionRule != null;

    if (!checkIncludeForColumns && !checkIncludeForDefinitions)
    {
      return true;
    }

    boolean includeForColumns = false;
    boolean includeForDefinitions = false;
    final Column[] columns = table.getColumns();
    if (columns.length == 0)
    {
      includeForColumns = true;
    }
    else
    {
      for (final Column column: columns)
      {
        if (checkIncludeForColumns)
        {
          if (grepColumnInclusionRule.include(column.getFullName()))
          {
            includeForColumns = true;
            break;
          }
        }
        if (checkIncludeForDefinitions)
        {
          if (grepDefinitionInclusionRule.include(column.getRemarks()))
          {
            includeForDefinitions = true;
            break;
          }
        }
      }
    }
    // Additional include checks for definitions
    if (checkIncludeForDefinitions)
    {
      if (grepDefinitionInclusionRule.include(table.getRemarks()))
      {
        includeForDefinitions = true;
      }
      if (table instanceof View)
      {
        if (grepDefinitionInclusionRule.include(((View) table).getDefinition()))
        {
          includeForDefinitions = true;
        }
      }
      for (Trigger trigger: table.getTriggers())
      {
        if (grepDefinitionInclusionRule.include(trigger.getActionStatement()))
        {
          includeForDefinitions = true;
          break;
        }
      }
    }

    boolean include = (checkIncludeForColumns && includeForColumns)
                      || (checkIncludeForDefinitions && includeForDefinitions);
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

  private final Connection connection;

  /**
   * Constructs a SchemaCrawler object, from a connection.
   * 
   * @param connection
   *        An database connection.
   * @throws SchemaCrawlerException
   *         On a crawler exception
   */
  public SchemaCrawler(final Connection connection)
    throws SchemaCrawlerException
  {
    if (connection == null)
    {
      throw new SchemaCrawlerException("No connection specified");
    }
    this.connection = connection;
  }

  /**
   * Crawls the database, to obtain database metadata.
   * 
   * @param options
   *        SchemaCrawler options that control what metadata is returned
   * @return Database metadata
   * @throws SchemaCrawlerException
   *         On an exception
   */
  public Database crawl(final SchemaCrawlerOptions options)
    throws SchemaCrawlerException
  {
    final MutableDatabase database = new MutableDatabase("database");

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

      crawlSchemas(database, retrieverConnection, schemaCrawlerOptions);
      crawlDatabaseInfo(database, retrieverConnection, schemaCrawlerOptions);
      crawlColumnDataTypes(database, retrieverConnection, schemaCrawlerOptions);
      crawlTables(database, retrieverConnection, schemaCrawlerOptions);
      crawlProcedures(database, retrieverConnection, schemaCrawlerOptions);

      return database;
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Database access exception", e);
    }
  }

}
