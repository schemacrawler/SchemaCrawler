/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
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


import static sf.util.Utility.isBlank;

import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.IndexColumnSortSequence;
import schemacrawler.schema.IndexType;
import schemacrawler.schema.View;
import schemacrawler.schemacrawler.SchemaCrawlerSQLException;
import sf.util.StringFormat;

/**
 * A retriever uses database metadata to get the details about the
 * database tables.
 *
 * @author Sualeh Fatehi
 */
final class IndexRetriever
  extends AbstractRetriever
{

  private static final Logger LOGGER = Logger
    .getLogger(IndexRetriever.class.getName());

  IndexRetriever(final RetrieverConnection retrieverConnection,
                 final MutableCatalog catalog)
                   throws SQLException
  {
    super(retrieverConnection, catalog);
  }

  void retrieveIndexes(final NamedObjectList<MutableTable> allTables)
    throws SQLException
  {
    for (final MutableTable table: allTables)
    {
      if (table instanceof View)
      {
        continue;
      }
      retrieveIndexes(table, false);
      retrieveIndexes(table, true);
    }
  }

  void retrievePrimaryKey(final MutableTable table)
    throws SQLException
  {

    try (final MetadataResultSet results = new MetadataResultSet(getMetaData()
      .getPrimaryKeys(unquotedName(table.getSchema().getCatalogName()),
                      unquotedName(table.getSchema().getName()),
                      unquotedName(table.getName())));)
    {

      MutablePrimaryKey primaryKey;
      while (results.next())
      {
        // "TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME"
        final String columnName = quotedName(results.getString("COLUMN_NAME"));
        final String primaryKeyName = quotedName(results.getString("PK_NAME"));
        final int keySequence = Integer.parseInt(results.getString("KEY_SEQ"));

        primaryKey = table.getPrimaryKey();
        if (primaryKey == null)
        {
          primaryKey = new MutablePrimaryKey(table, primaryKeyName);
        }

        // Register primary key information
        final Optional<MutableColumn> columnOptional = table
          .lookupColumn(columnName);
        if (columnOptional.isPresent())
        {
          final MutableColumn column = columnOptional.get();
          column.markAsPartOfPrimaryKey();
          final MutableIndexColumn indexColumn = new MutableIndexColumn(primaryKey,
                                                                        column);
          indexColumn.setSortSequence(IndexColumnSortSequence.ascending);
          indexColumn.setIndexOrdinalPosition(keySequence);
          //
          primaryKey.addColumn(indexColumn);
        }

        table.setPrimaryKey(primaryKey);
      }
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerSQLException("Could not retrieve primary keys for table "
                                          + table, e);
    }

  }

  private void createIndexes(final MutableTable table,
                             final MetadataResultSet results)
                               throws SQLException
  {
    try
    {
      while (results.next())
      {
        // "TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME"
        String indexName = quotedName(results.getString("INDEX_NAME"));
        LOGGER.log(Level.FINER,
                   new StringFormat("Retrieving index: %s.%s",
                                    table.getFullName(),
                                    indexName));

        // Work-around PostgreSQL JDBC driver bugs by unquoting column
        // names first
        // #3480 -
        // http://www.postgresql.org/message-id/200707231358.l6NDwlWh026230@wwwmaster.postgresql.org
        // #6253 -
        // http://www.postgresql.org/message-id/201110121403.p9CE3fsx039675@wwwmaster.postgresql.org
        final String columnName = quotedName(unquotedName(results
          .getString("COLUMN_NAME")));
        if (isBlank(columnName))
        {
          continue;
        }

        final boolean uniqueIndex = !results.getBoolean("NON_UNIQUE");
        final int type = results.getInt("TYPE", IndexType.unknown.getId());
        final int ordinalPosition = results.getInt("ORDINAL_POSITION", 0);
        final IndexColumnSortSequence sortSequence = IndexColumnSortSequence
          .valueOfFromCode(results.getString("ASC_OR_DESC"));
        final int cardinality = results.getInt("CARDINALITY", 0);
        final int pages = results.getInt("PAGES", 0);

        final Optional<MutableColumn> columnOptional = table
          .lookupColumn(columnName);
        if (columnOptional.isPresent())
        {
          final MutableColumn column = columnOptional.get();
          if (isBlank(indexName))
          {
            indexName = String
              .format("SC_%s",
                      Integer.toHexString(column.getFullName().hashCode())
                        .toUpperCase());
          }

          final Optional<MutableIndex> indexOptional = table
            .lookupIndex(indexName);
          final MutableIndex index;
          if (indexOptional.isPresent())
          {
            index = indexOptional.get();
          }
          else
          {
            index = new MutableIndex(table, indexName);
            table.addIndex(index);
          }

          column.markAsPartOfIndex();
          if (uniqueIndex)
          {
            column.markAsPartOfUniqueIndex();
          }

          final MutableIndexColumn indexColumn = new MutableIndexColumn(index,
                                                                        column);
          indexColumn.setIndexOrdinalPosition(ordinalPosition);
          indexColumn.setSortSequence(sortSequence);
          //
          index.addColumn(indexColumn);
          index.setUnique(uniqueIndex);
          index.setIndexType(IndexType.valueOf(type));
          index.setCardinality(cardinality);
          index.setPages(pages);
          index.addAttributes(results.getAttributes());
        }
      }
    }
    finally
    {
      results.close();
    }
  }

  private void retrieveIndexes(final MutableTable table, final boolean unique)
    throws SQLException
  {

    SQLException sqlEx = null;
    try
    {
      retrieveIndexes1(table, unique);
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING,
                 e.getCause(),
                 new StringFormat("Could not retrieve %sindexes for table %s, trying again",
                                  unique? "unique ": "",
                                  table));
      sqlEx = e;
    }

    if (sqlEx != null)
    {
      try
      {
        sqlEx = null;
        retrieveIndexes2(table, unique);
      }
      catch (final SQLException e)
      {
        sqlEx = e;
      }
    }

    if (sqlEx != null)
    {
      throw sqlEx;
    }
  }

  private void retrieveIndexes1(final MutableTable table, final boolean unique)
    throws SQLException
  {

    try (final MetadataResultSet results = new MetadataResultSet(getMetaData()
      .getIndexInfo(unquotedName(table.getSchema().getCatalogName()),
                    unquotedName(table.getSchema().getName()),
                    unquotedName(table.getName()),
                    unique,
                    true/* approximate */));)
    {
      createIndexes(table, results);
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerSQLException("Could not retrieve indexes for table "
                                          + table, e);
    }

  }

  private void retrieveIndexes2(final MutableTable table, final boolean unique)
    throws SQLException
  {

    try (final MetadataResultSet results = new MetadataResultSet(getMetaData()
      .getIndexInfo(null,
                    null,
                    table.getName(),
                    unique,
                    true/* approximate */));)
    {
      createIndexes(table, results);
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerSQLException("Could not retrieve indexes for table "
                                          + table, e);
    }

  }

}
