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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.ActionOrientationType;
import schemacrawler.schema.CheckOptionType;
import schemacrawler.schema.ConditionTimingType;
import schemacrawler.schema.EventManipulationType;
import schemacrawler.schemacrawler.InformationSchemaViews;

/**
 * A retriever uses database metadata to get the extended details about
 * the database tables.
 * 
 * @author Sualeh Fatehi
 */
final class TableExRetriever
  extends AbstractRetriever
{

  private static final Logger LOGGER = Logger.getLogger(TableExRetriever.class
    .getName());

  TableExRetriever(final RetrieverConnection retrieverConnection)
    throws SQLException
  {
    super(retrieverConnection);
  }

  /**
   * Retrieves a check constraint information from the database, in the
   * INFORMATION_SCHEMA format.
   * 
   * @param database
   *        List of tables and views.
   * @throws SQLException
   *         On a SQL exception
   */
  void retrieveCheckConstraintInformation(final MutableDatabase database)
    throws SQLException
  {
    final Map<String, MutableCheckConstraint> checkConstraintsMap = new HashMap<String, MutableCheckConstraint>();

    final InformationSchemaViews informationSchemaViews = getRetrieverConnection()
      .getInformationSchemaViews();

    if (!informationSchemaViews.hasTableConstraintsSql())
    {
      LOGGER
        .log(Level.FINE, "Table constraints SQL statement was not provided");
      return;
    }
    final String tableConstraintsInformationSql = informationSchemaViews
      .getTableConstraints().getQuery();

    final Connection connection = getDatabaseConnection();
    Statement statement = connection.createStatement();
    MetadataResultSet results = null;
    try
    {
      results = new MetadataResultSet(statement
        .executeQuery(tableConstraintsInformationSql));
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING,
                 "Could not retrieve check constraint information",
                 e);
      return;
    }

    try
    {
      while (results.next())
      {
        final String catalogName = results.getString("CONSTRAINT_CATALOG");
        final String schemaName = results.getString("CONSTRAINT_SCHEMA");
        final String constraintName = results.getString("CONSTRAINT_NAME");
        LOGGER.log(Level.FINEST, "Retrieving constraint information for "
                                 + constraintName);
        // final String tableCatalogName =
        // results.getString("TABLE_CATALOG");
        // final String tableSchemaName =
        // results.getString("TABLE_SCHEMA");
        final String tableName = results.getString("TABLE_NAME");

        final MutableTable table = database.lookupTable(catalogName,
                                                        schemaName,
                                                        tableName);
        if (table == null)
        {
          LOGGER.log(Level.FINE, String.format("Cannot find table, %s.%s.%s",
                                               catalogName,
                                               schemaName,
                                               tableName));
          continue;
        }

        final String constraintType = results.getString("CONSTRAINT_TYPE");
        final boolean deferrable = results.getBoolean("IS_DEFERRABLE");
        final boolean initiallyDeferred = results
          .getBoolean("INITIALLY_DEFERRED");

        if (constraintType.equalsIgnoreCase("check"))
        {
          final MutableCheckConstraint checkConstraint = new MutableCheckConstraint(table,
                                                                                    constraintName);
          checkConstraint.setDeferrable(deferrable);
          checkConstraint.setInitiallyDeferred(initiallyDeferred);

          checkConstraint.addAttributes(results.getAttributes());

          // Add to map, since we will need this later
          checkConstraintsMap.put(constraintName, checkConstraint);
        }
      }
    }
    finally
    {
      statement.close();
      results.close();
    }

    if (!informationSchemaViews.hasCheckConstraintsSql())
    {
      LOGGER
        .log(Level.FINE, "Check constraints SQL statement was not provided");
      return;
    }
    final String checkConstraintInformationSql = informationSchemaViews
      .getCheckConstraints().getQuery();

    // Get check constraint definitions
    statement = connection.createStatement();
    results = new MetadataResultSet(statement
      .executeQuery(checkConstraintInformationSql));
    try
    {
      while (results.next())
      {
        // final String catalogName =
        // results.getString("CONSTRAINT_CATALOG");
        // final String schemaName =
        // results.getString("CONSTRAINT_SCHEMA");
        final String constraintName = results.getString("CONSTRAINT_NAME");
        LOGGER.log(Level.FINEST, "Retrieving constraint definition for "
                                 + constraintName);
        String definition = results.getString("CHECK_CLAUSE");

        final MutableCheckConstraint checkConstraint = checkConstraintsMap
          .get(constraintName);
        if (checkConstraint == null)
        {
          LOGGER.log(Level.FINEST, "Could not add check constraint to table: "
                                   + constraintName);
          continue;
        }
        final String text = checkConstraint.getDefinition();

        if (!(text == null || text.trim().length() == 0))
        {
          definition = checkConstraint.getDefinition() + definition;
        }

        checkConstraint.setDefinition(definition);
      }
    }
    finally
    {
      statement.close();
      results.close();
    }

    // Add check constraints to tables
    final Collection<MutableCheckConstraint> checkConstraintsCollection = checkConstraintsMap
      .values();
    for (final MutableCheckConstraint checkConstraint: checkConstraintsCollection)
    {
      final MutableTable table = (MutableTable) checkConstraint.getParent();
      table.addCheckConstraint(checkConstraint);
    }

  }

  void retrieveTableColumnPrivileges(final MutableDatabase database)
    throws SQLException
  {
    MetadataResultSet results = null;
    try
    {
      results = new MetadataResultSet(getRetrieverConnection().getMetaData()
        .getColumnPrivileges(null, null, "%", "%"));
      createPrivileges(results, database, true);
    }
    finally
    {
      if (results != null)
      {
        results.close();
      }
    }
  }

  void retrieveTablePrivileges(final MutableDatabase database)
    throws SQLException
  {
    MetadataResultSet results = null;
    try
    {
      results = new MetadataResultSet(getRetrieverConnection().getMetaData()
        .getTablePrivileges(null, null, "%"));
      createPrivileges(results, database, false);
    }
    finally
    {
      if (results != null)
      {
        results.close();
      }
    }
  }

  /**
   * Retrieves a trigger information from the database, in the
   * INFORMATION_SCHEMA format.
   * 
   * @param database
   *        List of tables and views.
   * @throws SQLException
   *         On a SQL exception
   */
  void retrieveTriggerInformation(final MutableDatabase database)
    throws SQLException
  {
    final InformationSchemaViews informationSchemaViews = getRetrieverConnection()
      .getInformationSchemaViews();
    if (!informationSchemaViews.hasTriggerSql())
    {
      LOGGER.log(Level.FINE,
                 "Trigger definition SQL statement was not provided");
      return;
    }
    final String triggerInformationSql = informationSchemaViews.getTriggers()
      .getQuery();

    final Connection connection = getDatabaseConnection();
    final Statement statement = connection.createStatement();
    statement.setFetchSize(FETCHSIZE);
    MetadataResultSet results = null;
    try
    {
      results = new MetadataResultSet(statement
        .executeQuery(triggerInformationSql));
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, "Could not retrieve trigger information", e);
      return;
    }

    try
    {
      while (results.next())
      {
        final String catalogName = results.getString("TRIGGER_CATALOG");
        final String schemaName = results.getString("TRIGGER_SCHEMA");
        final String triggerName = results.getString("TRIGGER_NAME");
        LOGGER.log(Level.FINEST, "Retrieving trigger information for "
                                 + triggerName);

        // final String eventObjectCatalog = results
        // .getString("EVENT_OBJECT_CATALOG");
        // final String eventObjectSchema = results
        // .getString("EVENT_OBJECT_SCHEMA");
        final String tableName = results.getString("EVENT_OBJECT_TABLE");

        final MutableTable table = database.lookupTable(catalogName,
                                                        schemaName,
                                                        tableName);
        if (table == null)
        {
          LOGGER.log(Level.FINE, String.format("Cannot find table, %s.%s.%s",
                                               catalogName,
                                               schemaName,
                                               tableName));
          continue;
        }

        EventManipulationType eventManipulationType;
        try
        {
          eventManipulationType = EventManipulationType.valueOf(results
            .getString("EVENT_MANIPULATION").toLowerCase(Locale.ENGLISH));
        }
        catch (final IllegalArgumentException e)
        {
          eventManipulationType = EventManipulationType.unknown;
        }
        final int actionOrder = results.getInt("ACTION_ORDER", 0);
        final String actionCondition = results.getString("ACTION_CONDITION");
        final String actionStatement = results.getString("ACTION_STATEMENT");
        final ActionOrientationType actionOrientation = ActionOrientationType
          .valueOf(results.getString("ACTION_ORIENTATION")
            .toLowerCase(Locale.ENGLISH));
        final ConditionTimingType conditionTiming = ConditionTimingType
          .valueOfFromValue(results.getString("CONDITION_TIMING")
            .toLowerCase(Locale.ENGLISH));

        MutableTrigger trigger = table.lookupTrigger(triggerName);
        if (trigger == null)
        {
          trigger = new MutableTrigger(table, triggerName);
        }
        trigger.setEventManipulationType(eventManipulationType);
        trigger.setActionOrder(actionOrder);
        trigger.appendActionCondition(actionCondition);
        trigger.appendActionStatement(actionStatement);
        trigger.setActionOrientation(actionOrientation);
        trigger.setConditionTiming(conditionTiming);

        trigger.addAttributes(results.getAttributes());
        // Add trigger to the table
        table.addTrigger(trigger);

      }
    }
    finally
    {
      results.close();
      statement.close();
    }

  }

  /**
   * Retrieves a view information from the database, in the
   * INFORMATION_SCHEMA format.
   * 
   * @param database
   *        List of tables and views.
   * @throws SQLException
   *         On a SQL exception
   */
  void retrieveViewInformation(final MutableDatabase database)
    throws SQLException
  {
    final InformationSchemaViews informationSchemaViews = getRetrieverConnection()
      .getInformationSchemaViews();

    if (!informationSchemaViews.hasViewsSql())
    {
      LOGGER.log(Level.FINE, "Views SQL statement was not provided");
      return;
    }
    final String viewInformationSql = informationSchemaViews.getViews()
      .getQuery();

    final Connection connection = getDatabaseConnection();
    final Statement statement = connection.createStatement();
    statement.setFetchSize(FETCHSIZE);
    MetadataResultSet results = null;
    try
    {
      results = new MetadataResultSet(statement
        .executeQuery(viewInformationSql));
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, "Could not retrieve view information", e);
      return;
    }

    try
    {
      while (results.next())
      {
        final String catalogName = results.getString("TABLE_CATALOG");
        final String schemaName = results.getString("TABLE_SCHEMA");
        final String viewName = results.getString("TABLE_NAME");

        final MutableView view = (MutableView) database
          .lookupTable(catalogName, schemaName, viewName);
        if (view == null)
        {
          LOGGER.log(Level.FINE, String.format("Cannot find table, %s.%s.%s",
                                               catalogName,
                                               schemaName,
                                               viewName));
          continue;
        }

        LOGGER.log(Level.FINEST, "Retrieving view information for " + viewName);
        final String definition = results.getString("VIEW_DEFINITION");
        final CheckOptionType checkOption = CheckOptionType.valueOf(results
          .getString("CHECK_OPTION").toLowerCase(Locale.ENGLISH));
        final boolean updatable = results.getBoolean("IS_UPDATABLE");

        view.appendDefinition(definition);
        view.setCheckOption(checkOption);
        view.setUpdatable(updatable);

        view.addAttributes(results.getAttributes());
      }
    }
    finally
    {
      statement.close();
      results.close();
    }

  }

  private void createPrivileges(final MetadataResultSet results,
                                final MutableDatabase database,
                                final boolean privilegesForColumn)
    throws SQLException
  {

    while (results.next())
    {
      final String catalogName = results.getString("TABLE_CAT");
      final String schemaName = results.getString("TABLE_SCHEM");
      final String tableName = results.getString(TABLE_NAME);
      final String columnName;
      if (privilegesForColumn)
      {
        columnName = results.getString(COLUMN_NAME);
      }
      else
      {
        columnName = null;
      }

      final MutableTable table = database.lookupTable(catalogName,
                                                      schemaName,
                                                      tableName);
      if (table == null)
      {
        LOGGER.log(Level.FINE, String.format("Cannot find schema, %s.%s.%s",
                                             catalogName,
                                             schemaName,
                                             table));
        continue;
      }

      final MutableColumn column = (MutableColumn) table.getColumn(columnName);
      if (privilegesForColumn && column == null)
      {
        LOGGER.log(Level.FINE, String.format("Cannot find column, %s.%s.%s.%s",
                                             catalogName,
                                             schemaName,
                                             tableName,
                                             columnName));
        continue;
      }

      final String privilegeName = results.getString("PRIVILEGE");
      final String grantor = results.getString("GRANTOR");
      final String grantee = results.getString("GRANTEE");
      final boolean isGrantable = results.getBoolean("IS_GRANTABLE");

      final MutablePrivilege privilege;
      if (privilegesForColumn)
      {
        privilege = new MutablePrivilege(column, privilegeName);
      }
      else
      {
        privilege = new MutablePrivilege(table, privilegeName);
      }

      privilege.setGrantor(grantor);
      privilege.setGrantee(grantee);
      privilege.setGrantable(isGrantable);

      privilege.addAttributes(results.getAttributes());

      if (privilegesForColumn)
      {
        column.addPrivilege(privilege);
      }
      else
      {
        table.addPrivilege(privilege);
      }
    }
  }

}
