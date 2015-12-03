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

package schemacrawler.crawl;


import static java.util.Objects.requireNonNull;
import static sf.util.DatabaseUtility.executeSql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Column;
import schemacrawler.schema.ForeignKeyDeferrability;
import schemacrawler.schema.ForeignKeyUpdateRule;
import schemacrawler.schema.SchemaReference;
import schemacrawler.schema.Table;
import schemacrawler.schema.View;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.SchemaCrawlerSQLException;
import schemacrawler.utility.MetaDataUtility;
import sf.util.Utility;

/**
 * A retriever uses database metadata to get the details about the
 * database forign keys.
 *
 * @author Sualeh Fatehi
 */
final class ForeignKeyRetriever
  extends AbstractRetriever
{

  private static final Logger LOGGER = Logger
    .getLogger(ForeignKeyRetriever.class.getName());

  ForeignKeyRetriever(final RetrieverConnection retrieverConnection,
                      final MutableCatalog catalog)
                        throws SQLException
  {
    super(retrieverConnection, catalog);
  }

  void retrieveForeignKeys(final NamedObjectList<MutableTable> allTables)
    throws SchemaCrawlerSQLException
  {
    requireNonNull(allTables);

    final InformationSchemaViews informationSchemaViews = getRetrieverConnection()
      .getInformationSchemaViews();

    if (!informationSchemaViews.hasForeignKeysSql())
    {
      LOGGER.log(Level.INFO,
                 "Retrieving foreign keys, using database metadata");
      retrieveForeignKeysUsingDatabaseMetadata(allTables);
    }
    else
    {
      LOGGER.log(Level.INFO, "Retrieving foreign keys, using SQL");
      retrieveForeignKeysUsingSql(informationSchemaViews);
    }
  }

  private void createForeignKeys(final MetadataResultSet results,
                                 final NamedObjectList<MutableForeignKey> foreignKeys)
                                   throws SQLException
  {
    try
    {
      while (results.next())
      {
        String foreignKeyName = quotedName(results.getString("FK_NAME"));
        LOGGER.log(Level.FINER, "Retrieving foreign key: " + foreignKeyName);

        final String pkTableCatalogName = quotedName(results
          .getString("PKTABLE_CAT"));
        final String pkTableSchemaName = quotedName(results
          .getString("PKTABLE_SCHEM"));
        final String pkTableName = quotedName(results
          .getString("PKTABLE_NAME"));
        final String pkColumnName = quotedName(results
          .getString("PKCOLUMN_NAME"));

        final String fkTableCatalogName = quotedName(results
          .getString("FKTABLE_CAT"));
        final String fkTableSchemaName = quotedName(results
          .getString("FKTABLE_SCHEM"));
        final String fkTableName = quotedName(results
          .getString("FKTABLE_NAME"));
        final String fkColumnName = quotedName(results
          .getString("FKCOLUMN_NAME"));

        final int keySequence = results.getInt("KEY_SEQ", 0);
        final int updateRule = results
          .getInt("UPDATE_RULE", ForeignKeyUpdateRule.unknown.getId());
        final int deleteRule = results
          .getInt("DELETE_RULE", ForeignKeyUpdateRule.unknown.getId());
        final int deferrability = results
          .getInt("DEFERRABILITY", ForeignKeyDeferrability.unknown.getId());

        final Column pkColumn = lookupOrCreateColumn(pkTableCatalogName,
                                                     pkTableSchemaName,
                                                     pkTableName,
                                                     pkColumnName);
        final Column fkColumn = lookupOrCreateColumn(fkTableCatalogName,
                                                     fkTableSchemaName,
                                                     fkTableName,
                                                     fkColumnName);
        final boolean isPkColumnPartial = pkColumn instanceof ColumnPartial;
        final boolean isFkColumnPartial = fkColumn instanceof ColumnPartial;

        if (pkColumn == null || fkColumn == null
            || isFkColumnPartial && isPkColumnPartial)
        {
          continue;
        }

        if (Utility.isBlank(foreignKeyName))
        {
          foreignKeyName = MetaDataUtility.constructForeignKeyName(pkColumn,
                                                                   fkColumn);
        }

        final Optional<MutableForeignKey> foreignKeyOptional = foreignKeys
          .lookup(foreignKeyName);
        final MutableForeignKey foreignKey;
        if (foreignKeyOptional.isPresent())
        {
          foreignKey = foreignKeyOptional.get();
        }
        else
        {
          foreignKey = new MutableForeignKey(foreignKeyName);
          foreignKeys.add(foreignKey);
        }

        foreignKey.addColumnReference(keySequence, pkColumn, fkColumn);
        foreignKey.setUpdateRule(ForeignKeyUpdateRule.valueOf(updateRule));
        foreignKey.setDeleteRule(ForeignKeyUpdateRule.valueOf(deleteRule));
        foreignKey
          .setDeferrability(ForeignKeyDeferrability.valueOf(deferrability));
        foreignKey.addAttributes(results.getAttributes());

        if (fkColumn instanceof MutableColumn)
        {
          ((MutableColumn) fkColumn).setReferencedColumn(pkColumn);
          ((MutableTable) fkColumn.getParent()).addForeignKey(foreignKey);
        }
        else if (isFkColumnPartial)
        {
          ((ColumnPartial) fkColumn).setReferencedColumn(pkColumn);
          ((TablePartial) fkColumn.getParent()).addForeignKey(foreignKey);
        }

        if (pkColumn instanceof MutableColumn)
        {
          ((MutableTable) pkColumn.getParent()).addForeignKey(foreignKey);
        }
        else if (isPkColumnPartial)
        {
          ((TablePartial) pkColumn.getParent()).addForeignKey(foreignKey);
        }
      }
    }
    finally
    {
      results.close();
    }

  }

  /**
   * Looks up a column in the database. If the column and table are not
   * found, they are created, and added to the schema. This is prevent
   * foreign key relationships from having a null pointer.
   */
  private Column lookupOrCreateColumn(final String catalogName,
                                      final String schemaName,
                                      final String tableName,
                                      final String columnName)
  {
    Column column = null;

    final SchemaReference schema = new SchemaReference(catalogName, schemaName);
    final Optional<MutableTable> tableOptional = catalog.lookupTable(schema,
                                                                     tableName);
    if (tableOptional.isPresent())
    {
      final Table table = tableOptional.get();
      final Optional<? extends Column> columnOptional = table
        .lookupColumn(columnName);
      if (columnOptional.isPresent())
      {
        column = columnOptional.get();
      }
    }

    if (column == null)
    {
      // Create the table and column, but do not add it to the schema
      final Table table = new TablePartial(schema, tableName);
      column = new ColumnPartial(table, columnName);
      ((TablePartial) table).addColumn(column);

      LOGGER.log(Level.FINER,
                 String.format(
                               "Creating column reference for a column that is referenced by a foreign key: %s",
                               column.getFullName()));
    }
    return column;
  }

  private void retrieveForeignKeysUsingDatabaseMetadata(final NamedObjectList<MutableTable> allTables)
    throws SchemaCrawlerSQLException
  {
    final NamedObjectList<MutableForeignKey> foreignKeys = new NamedObjectList<>();
    for (final MutableTable table: allTables)
    {
      if (table instanceof View)
      {
        continue;
      }

      final DatabaseMetaData metaData = getMetaData();
      try (final MetadataResultSet results = new MetadataResultSet(metaData
        .getImportedKeys(unquotedName(table.getSchema().getCatalogName()),
                         unquotedName(table.getSchema().getName()),
                         unquotedName(table.getName())));)
      {
        createForeignKeys(results, foreignKeys);
      }
      catch (final SQLException e)
      {
        throw new SchemaCrawlerSQLException("Could not retrieve foreign keys for table "
                                            + table, e);
      }

      // We need to get exported keys as well, since if only a single
      // table is
      // selected, we have not retrieved it's keys that are imported by
      // other
      // tables.
      try (final MetadataResultSet results = new MetadataResultSet(metaData
        .getExportedKeys(unquotedName(table.getSchema().getCatalogName()),
                         unquotedName(table.getSchema().getName()),
                         unquotedName(table.getName())));)
      {
        createForeignKeys(results, foreignKeys);
      }
      catch (final SQLException e)
      {
        throw new SchemaCrawlerSQLException("Could not retrieve foreign keys for table "
                                            + table, e);
      }
    }
  }

  private void retrieveForeignKeysUsingSql(final InformationSchemaViews informationSchemaViews)
    throws SchemaCrawlerSQLException
  {
    final String fkSql = informationSchemaViews.getForeignKeysSql();
    LOGGER.log(Level.FINER,
               String.format("Executing SQL to retrieve foreign keys: %n%s",
                             fkSql));

    final NamedObjectList<MutableForeignKey> foreignKeys = new NamedObjectList<>();
    final Connection connection = getDatabaseConnection();
    try (final Statement statement = connection.createStatement();
        final MetadataResultSet results = new MetadataResultSet("retrieveForeignKeysUsingSql",
                                                                executeSql(statement,
                                                                           fkSql));)
    {
      createForeignKeys(results, foreignKeys);
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerSQLException("Could not retrieve foreign keys from SQL:\n"
                                          + fkSql, e);
    }
  }

}
