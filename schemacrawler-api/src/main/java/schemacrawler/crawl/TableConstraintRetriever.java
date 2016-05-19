/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.crawl;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.SchemaReference;
import schemacrawler.schema.TableConstraintType;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.utility.Query;
import sf.util.StringFormat;

/**
 * A retriever uses database metadata to get the constraints on the
 * database tables.
 *
 * @author Sualeh Fatehi
 */
final class TableConstraintRetriever
  extends AbstractRetriever
{

  private static final Logger LOGGER = Logger
    .getLogger(TableConstraintRetriever.class.getName());

  private final Map<String, MutableTableConstraint> tableConstraintsMap;

  TableConstraintRetriever(final RetrieverConnection retrieverConnection,
                           final MutableCatalog catalog,
                           final SchemaCrawlerOptions options)
    throws SQLException
  {
    super(retrieverConnection, catalog, options);
    tableConstraintsMap = new HashMap<>();
  }

  void retrieveTableConstraintDefinitions()
  {
    if (tableConstraintsMap.isEmpty())
    {
      LOGGER.log(Level.FINE, "No table constraints found");
      return;
    }

    final InformationSchemaViews informationSchemaViews = getRetrieverConnection()
      .getInformationSchemaViews();

    final Connection connection = getDatabaseConnection();

    if (!informationSchemaViews.hasExtTableConstraintsSql())
    {
      LOGGER.log(Level.FINE,
                 "Extended table constraints SQL statement was not provided");
      return;
    }
    final Query extTableConstraintInformationSql = informationSchemaViews
      .getExtTableConstraintsSql();

    // Get check constraint definitions
    try (final Statement statement = connection.createStatement();
        final MetadataResultSet results = new MetadataResultSet(extTableConstraintInformationSql,
                                                                statement,
                                                                getSchemaInclusionRule());)
    {
      while (results.next())
      {
        final String catalogName = quotedName(results
          .getString("CONSTRAINT_CATALOG"));
        final String schemaName = quotedName(results
          .getString("CONSTRAINT_SCHEMA"));
        final String constraintName = quotedName(results
          .getString("CONSTRAINT_NAME"));
        LOGGER.log(Level.FINER,
                   new StringFormat("Retrieving constraint definition, %s",
                                    constraintName));
        final String definition = results.getString("CHECK_CLAUSE");

        final String constraintKey = new SchemaReference(catalogName,
                                                         schemaName)
                                     + "." + constraintName;
        final MutableTableConstraint tableConstraint = tableConstraintsMap
          .get(constraintKey);
        if (tableConstraint == null)
        {
          LOGGER.log(Level.FINEST,
                     new StringFormat("Could not add constraint definition to table, %s",
                                      constraintName));
          continue;
        }
        tableConstraint.appendDefinition(definition);

        tableConstraint.addAttributes(results.getAttributes());

      }
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could not retrieve check constraints", e);
    }
  }

  /**
   * Retrieves table constraint information from the database, in the
   * INFORMATION_SCHEMA format.
   *
   * @throws SQLException
   *         On a SQL exception
   */
  void retrieveTableConstraintInformation()
    throws SQLException
  {

    final InformationSchemaViews informationSchemaViews = getRetrieverConnection()
      .getInformationSchemaViews();

    final Connection connection = getDatabaseConnection();

    createTableConstraints(connection,
                           tableConstraintsMap,
                           informationSchemaViews);

    if (!tableConstraintsMap.isEmpty())
    {
      retrieveTableConstraintsColumns(connection,
                                      tableConstraintsMap,
                                      informationSchemaViews);
    }
  }

  private void createTableConstraints(final Connection connection,
                                      final Map<String, MutableTableConstraint> tableConstraintsMap,
                                      final InformationSchemaViews informationSchemaViews)
  {
    if (!informationSchemaViews.hasTableConstraintsSql())
    {
      LOGGER.log(Level.FINE,
                 "Table constraints SQL statement was not provided");
      return;
    }

    final Query tableConstraintsInformationSql = informationSchemaViews
      .getTableConstraintsSql();
    try (final Statement statement = connection.createStatement();
        final MetadataResultSet results = new MetadataResultSet(tableConstraintsInformationSql,
                                                                statement,
                                                                getSchemaInclusionRule());)
    {

      while (results.next())
      {
        final String catalogName = quotedName(results
          .getString("CONSTRAINT_CATALOG"));
        final String schemaName = quotedName(results
          .getString("CONSTRAINT_SCHEMA"));
        final String constraintName = quotedName(results
          .getString("CONSTRAINT_NAME"));
        LOGGER
          .log(Level.FINER,
               new StringFormat("Retrieving constraint, %s", constraintName));
        // "TABLE_CATALOG", "TABLE_SCHEMA"
        final String tableName = quotedName(results.getString("TABLE_NAME"));

        final Optional<MutableTable> tableOptional = lookupTable(catalogName,
                                                                 schemaName,
                                                                 tableName);
        if (!tableOptional.isPresent())
        {
          LOGGER.log(Level.FINE,
                     new StringFormat("Cannot find table, %s.%s.%s",
                                      catalogName,
                                      schemaName,
                                      tableName));
          continue;
        }

        final MutableTable table = tableOptional.get();
        final String constraintType = results.getString("CONSTRAINT_TYPE");
        final boolean deferrable = results.getBoolean("IS_DEFERRABLE");
        final boolean initiallyDeferred = results
          .getBoolean("INITIALLY_DEFERRED");

        final MutableTableConstraint tableConstraint = new MutableTableConstraint(table,
                                                                                  constraintName);
        tableConstraint.setTableConstraintType(TableConstraintType
          .valueOfFromValue(constraintType));
        tableConstraint.setDeferrable(deferrable);
        tableConstraint.setInitiallyDeferred(initiallyDeferred);

        tableConstraint.addAttributes(results.getAttributes());

        // Add constraint to table
        table.addTableConstraint(tableConstraint);

        // Add to map, since we will need this later
        final String constraintKey = table.getSchema().getFullName() + "."
                                     + constraintName;
        tableConstraintsMap.put(constraintKey, tableConstraint);
      }
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING,
                 "Could not retrieve table constraint information",
                 e);
      return;
    }
  }

  private void retrieveTableConstraintsColumns(final Connection connection,
                                               final Map<String, MutableTableConstraint> tableConstraintsMap,
                                               final InformationSchemaViews informationSchemaViews)
  {
    if (!informationSchemaViews.hasTableConstraintsColumnsSql())
    {
      LOGGER
        .log(Level.FINE,
             "Extended table constraints columns SQL statement was not provided");
      return;
    }
    final Query tableConstraintsColumnsInformationSql = informationSchemaViews
      .getTableConstraintsColumnsSql();

    try (final Statement statement = connection.createStatement();
        final MetadataResultSet results = new MetadataResultSet(tableConstraintsColumnsInformationSql,
                                                                statement,
                                                                getSchemaInclusionRule());)
    {
      while (results.next())
      {
        final String catalogName = quotedName(results
          .getString("CONSTRAINT_CATALOG"));
        final String schemaName = quotedName(results
          .getString("CONSTRAINT_SCHEMA"));
        final String constraintName = quotedName(results
          .getString("CONSTRAINT_NAME"));
        LOGGER.log(Level.FINER,
                   new StringFormat("Retrieving constraint definition, %s",
                                    constraintName));

        final String constraintKey = new SchemaReference(catalogName,
                                                         schemaName)
                                     + "." + constraintName;
        final MutableTableConstraint tableConstraint = tableConstraintsMap
          .get(constraintKey);
        if (tableConstraint == null)
        {
          LOGGER.log(Level.FINEST,
                     new StringFormat("Could not add column for constraint to table, %s",
                                      constraintName));
          continue;
        }

        // "TABLE_CATALOG", "TABLE_SCHEMA"
        final String tableName = quotedName(results.getString("TABLE_NAME"));

        final Optional<MutableTable> tableOptional = lookupTable(catalogName,
                                                                 schemaName,
                                                                 tableName);
        if (!tableOptional.isPresent())
        {
          LOGGER.log(Level.FINE,
                     new StringFormat("Cannot find table, %s.%s.%s",
                                      catalogName,
                                      schemaName,
                                      tableName));
          continue;
        }

        final MutableTable table = tableOptional.get();
        final String columnName = quotedName(results.getString("COLUMN_NAME"));
        final Optional<MutableColumn> columnOptional = table
          .lookupColumn(columnName);
        if (!columnOptional.isPresent())
        {
          LOGGER.log(Level.FINE,
                     new StringFormat("Cannot find column, %s.%s.%s.%s",
                                      catalogName,
                                      schemaName,
                                      tableName,
                                      columnName));
          continue;
        }
        final MutableColumn column = columnOptional.get();
        final int ordinalPosition = results.getInt("ORDINAL_POSITION", 0);
        final MutableTableConstraintColumn constraintColumn = new MutableTableConstraintColumn(tableConstraint,
                                                                                               column);
        constraintColumn.setTableConstraintOrdinalPosition(ordinalPosition);

        tableConstraint.addColumn(constraintColumn);
      }
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could not retrieve check constraints", e);
    }
  }

}
