/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
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


import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import schemacrawler.schema.Schema;

/**
 * SchemaCrawler uses database metadata to get the details about the schema.
 */
public final class SchemaCrawler
{

  private static final Logger LOGGER = Logger.getLogger(SchemaCrawler.class
    .getName());

  private final RetrieverConnection retrieverConnection;
  private final CrawlHandler handler;

  /**
   * Constructs a SchemaCrawler object, from a connection.
   * 
   * @param dataSource
   *          An data source.
   * @param crawlHandler
   *          A crawl handler instance
   * @throws SchemaCrawlerException
   *           On a crawler exception
   */
  public SchemaCrawler(final DataSource dataSource,
                       final CrawlHandler crawlHandler)
    throws SchemaCrawlerException
  {

    this.retrieverConnection = new RetrieverConnection(dataSource);

    if (crawlHandler == null)
    {
      throw new SchemaCrawlerException("Error creating text formatter");
    }
    handler = crawlHandler;

  }

  /**
   * Crawls the schema for all tables and views.
   * 
   * @param options
   *          Options
   * @throws SchemaCrawlerException
   *           On an exception
   */
  public void crawl(final SchemaCrawlerOptions options)
    throws SchemaCrawlerException
  {

    try
    {

      final SchemaInfoLevel infoLevel = handler.getInfoLevelHint();
      SchemaCrawlerOptions schemaCrawlerOptions = options;
      if (schemaCrawlerOptions == null)
      {
        schemaCrawlerOptions = new SchemaCrawlerOptions();
      }

      handler.begin();

      MutableDatabaseInfo databaseInfo = crawlDatabaseInfo(infoLevel,
                                                           schemaCrawlerOptions);
      final NamedObjectList columnDataTypes = databaseInfo
        .getColumnDataTypesList();

      crawlTables(infoLevel, schemaCrawlerOptions, columnDataTypes);
      crawlProcedures(infoLevel, schemaCrawlerOptions, columnDataTypes);

      handler.end();
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Database access error", e);
    }

  }

  private MutableDatabaseInfo crawlDatabaseInfo(
                                                final SchemaInfoLevel infoLevel,
                                                final SchemaCrawlerOptions options)
    throws SQLException, SchemaCrawlerException
  {
    DatabaseInfoRetriever retriever = new DatabaseInfoRetriever(
        retrieverConnection);
    final MutableDatabaseInfo dbInfo = retriever.retrieveDatabaseInfo();
    retriever.retrieveColumnDataTypes(dbInfo);
    if (infoLevel.isGreaterThan(SchemaInfoLevel.VERBOSE))
    {
      retriever.retrieveAdditionalDatabaseInfo(dbInfo);
      retriever.retrieveUserDefinedColumnDataTypes(dbInfo);
    }
    handler.handle(dbInfo);

    return dbInfo;
  }

  private void crawlProcedures(final SchemaInfoLevel infoLevel,
                               final SchemaCrawlerOptions options,
                               final NamedObjectList columnDataTypes)
    throws SQLException, SchemaCrawlerException
  {
    final ProcedureRetriever retriever = new ProcedureRetriever(
        retrieverConnection);
    final NamedObjectList procedures = retriever.retrieveProcedures(options
      .isShowStoredProcedures(), options.getTableInclusionRule());
    for (int i = 0; i < procedures.size(); i++)
    {
      final MutableProcedure procedure = (MutableProcedure) procedures.get(i);
      if (infoLevel != SchemaInfoLevel.MINIMUM)
      {
        retriever.retrieveProcedureColumns(procedure, options
          .getColumnInclusionRule(), columnDataTypes);
      }
      // set comparators
      procedure.setColumnComparator(options.getProcedureColumnComparator());
      // handle procedure
      handler.handle(procedure);
    }
  }

  private void crawlTables(final SchemaInfoLevel infoLevel,
                           final SchemaCrawlerOptions options,
                           final NamedObjectList columnDataTypes)
    throws SQLException, SchemaCrawlerException
  {
    final TableRetriever retriever = new TableRetriever(retrieverConnection);
    final TableExRetriever retrieverExtra = new TableExRetriever(
        retrieverConnection);
    final NamedObjectList tables = retriever.retrieveTables(options
      .getTableTypes(), options.getTableInclusionRule());
    if (infoLevel == SchemaInfoLevel.MAXIMUM)
    {
      retrieverExtra.retrievePrivileges(null, tables);
    }
    for (int i = 0; i < tables.size(); i++)
    {
      final MutableTable table = (MutableTable) tables.get(i);
      if (infoLevel.isGreaterThan(SchemaInfoLevel.MINIMUM))
      {
        retriever.retrieveColumns(table,
                                  options.getColumnInclusionRule(),
                                  columnDataTypes);
        if (infoLevel == SchemaInfoLevel.MAXIMUM)
        {
          retrieverExtra.retrievePrivileges(table, table.getColumnsList());
        }
        retriever.retrievePrimaryKeys(table);
        if (!table.getType().isView()
            && infoLevel.isGreaterThanOrEqualTo(SchemaInfoLevel.VERBOSE))
        {
          retriever.retrieveForeignKeys(tables, i);
          retriever.retrieveIndices(table, true, false);
          retriever.retrieveIndices(table, false, false);
        }
      }
      // set comparators
      table.setColumnComparator(options.getTableColumnComparator());
      table.setForeignKeyComparator(options.getTableForeignKeyComparator());
      table.setIndexComparator(options.getTableIndexComparator());
      // handle table
      handler.handle(table);
    }
  }

  /**
   * Gets the entire schema.
   * 
   * @param dataSource
   *          Data source
   * @param infoLevel
   *          Schema info level
   * @param options
   *          Options
   * @return Schema
   */
  public static Schema getSchema(final DataSource dataSource,
                                 final SchemaInfoLevel infoLevel,
                                 final SchemaCrawlerOptions options)
  {

    String catalog = null;
    try
    {
      catalog = dataSource.getConnection().getCatalog();
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      // NOTE: catalog remains null, which is ok for JDBC
    }

    final CachingCrawlerHandler schemaMaker = new CachingCrawlerHandler(
        catalog, infoLevel);
    try
    {
      final SchemaCrawler crawler = new SchemaCrawler(dataSource, schemaMaker);
      crawler.crawl(options);
    }
    catch (final SchemaCrawlerException e)
    {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
    }

    return schemaMaker.getSchema();

  }

}
