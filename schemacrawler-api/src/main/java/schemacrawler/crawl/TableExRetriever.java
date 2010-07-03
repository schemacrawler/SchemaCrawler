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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
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

  TableExRetriever(final RetrieverConnection retrieverConnection,
                   final MutableDatabase database)
    throws SQLException
  {
    super(retrieverConnection, database);
  }

  private void createPrivileges(final MetadataResultSet results,
                                final boolean privilegesForColumn)
    throws SQLException
  {
    while (results.next())
    {
      final String catalogName = results.getString("TABLE_CAT");
      final String schemaName = results.getString("TABLE_SCHEM");
      final String tableName = quotedName(results.getString("TABLE_NAME"));
      final String columnName;
      if (privilegesForColumn)
      {
        columnName = quotedName(results.getString("COLUMN_NAME"));
      }
      else
      {
        columnName = null;
      }

      final MutableTable table = lookupTable(catalogName, schemaName, tableName);
      if (table == null)
      {
        continue;
      }

      final MutableColumn column = table.getColumn(columnName);
      if (privilegesForColumn && column == null)
      {
        continue;
      }

      final String privilegeName = results.getString("PRIVILEGE");
      final String grantor = results.getString("GRANTOR");
      final String grantee = results.getString("GRANTEE");
      final boolean isGrantable = results.getBoolean("IS_GRANTABLE");

      final MutablePrivilege privilege;
      if (privilegesForColumn)
      {
        final MutablePrivilege columnPrivilege = column
          .getPrivilege(privilegeName);
        if (columnPrivilege == null)
        {
          privilege = new MutablePrivilege(column, privilegeName);
          column.addPrivilege(privilege);
        }
        else
        {
          privilege = columnPrivilege;
        }
      }
      else
      {
        final MutablePrivilege tablePrivilege = table
          .getPrivilege(privilegeName);
        if (tablePrivilege == null)
        {
          privilege = new MutablePrivilege(table, privilegeName);
          table.addPrivilege(privilege);
        }
        else
        {
          privilege = tablePrivilege;
        }
      }
      privilege.addGrant(grantor, grantee, isGrantable);
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

  /**
   * Retrieves a check constraint information from the database, in the
   * INFORMATION_SCHEMA format.
   * 
   * @throws SQLException
   *         On a SQL exception
   */
  void retrieveCheckConstraintInformation()
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
      .getTableConstraints();

    final Connection connection = getDatabaseConnection();
    Statement statement = null;
    MetadataResultSet results = null;
    try
    {
      statement = connection.createStatement();
      results = new MetadataResultSet(statement
        .executeQuery(tableConstraintsInformationSql));

      while (results.next())
      {
        final String catalogName = results.getString("CONSTRAINT_CATALOG");
        final String schemaName = results.getString("CONSTRAINT_SCHEMA");
        final String constraintName = quotedName(results
          .getString("CONSTRAINT_NAME"));
        LOGGER.log(Level.FINER, "Retrieving constraint: " + constraintName);
        // final String tableCatalogName =
        // results.getString("TABLE_CATALOG");
        // final String tableSchemaName =
        // results.getString("TABLE_SCHEMA");
        final String tableName = quotedName(results.getString("TABLE_NAME"));

        final MutableTable table = lookupTable(catalogName,
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
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING,
                 "Could not retrieve check constraint information",
                 e);
      return;
    }
    finally
    {
      if (results != null)
      {
        results.close();
      }
      if (statement != null)
      {
        statement.close();
      }
    }

    if (!informationSchemaViews.hasCheckConstraintsSql())
    {
      LOGGER
        .log(Level.FINE, "Check constraints SQL statement was not provided");
      return;
    }
    final String checkConstraintInformationSql = informationSchemaViews
      .getCheckConstraints();

    // Get check constraint definitions
    statement = null;
    results = null;
    try
    {
      statement = connection.createStatement();
      results = new MetadataResultSet(statement
        .executeQuery(checkConstraintInformationSql));
      while (results.next())
      {
        // final String catalogName =
        // results.getString("CONSTRAINT_CATALOG");
        // final String schemaName =
        // results.getString("CONSTRAINT_SCHEMA");
        final String constraintName = quotedName(results
          .getString("CONSTRAINT_NAME"));
        LOGGER.log(Level.FINER, "Retrieving constraint definition: "
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
      if (results != null)
      {
        results.close();
      }
      if (statement != null)
      {
        statement.close();
      }
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

  void retrieveTableColumnPrivileges()
    throws SQLException
  {
    MetadataResultSet results = null;
    try
    {
      results = new MetadataResultSet(getMetaData().getColumnPrivileges(null,
                                                                        null,
                                                                        "%",
                                                                        "%"));
      createPrivileges(results, true);
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, "Could not retrieve table column privileges:"
                                + e.getMessage());
    }
    finally
    {
      if (results != null)
      {
        results.close();
      }
    }
  }

  void retrieveTablePrivileges()
    throws SQLException
  {
    MetadataResultSet results = null;
    try
    {
      results = new MetadataResultSet(getMetaData().getTablePrivileges(null,
                                                                       null,
                                                                       "%"));
      createPrivileges(results, false);
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, "Could not retrieve table privileges", e);
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
   * @throws SQLException
   *         On a SQL exception
   */
  void retrieveTriggerInformation()
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
    final String triggerInformationSql = informationSchemaViews.getTriggers();

    final Connection connection = getDatabaseConnection();
    final Statement statement = connection.createStatement();
    MetadataResultSet results = null;
    try
    {
      results = new MetadataResultSet(statement
        .executeQuery(triggerInformationSql));

      while (results.next())
      {
        final String catalogName = results.getString("TRIGGER_CATALOG");
        final String schemaName = results.getString("TRIGGER_SCHEMA");
        final String triggerName = quotedName(results.getString("TRIGGER_NAME"));
        LOGGER.log(Level.FINER, "Retrieving trigger: " + triggerName);

        // final String eventObjectCatalog = results
        // .getString("EVENT_OBJECT_CATALOG");
        // final String eventObjectSchema = results
        // .getString("EVENT_OBJECT_SCHEMA");
        final String tableName = results.getString("EVENT_OBJECT_TABLE");

        final MutableTable table = lookupTable(catalogName,
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

        final EventManipulationType eventManipulationType = results
          .getEnum("EVENT_MANIPULATION", EventManipulationType.unknown);
        final int actionOrder = results.getInt("ACTION_ORDER", 0);
        final String actionCondition = results.getString("ACTION_CONDITION");
        final String actionStatement = results.getString("ACTION_STATEMENT");
        final ActionOrientationType actionOrientation = results
          .getEnum("ACTION_ORIENTATION", ActionOrientationType.unknown);
        final ConditionTimingType conditionTiming = results
          .getEnum("CONDITION_TIMING", ConditionTimingType.unknown);

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
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, "Could not retrieve trigger information", e);
    }
    finally
    {
      if (results != null)
      {
        results.close();
      }
      statement.close();
    }

  }

  /**
   * Retrieves a view information from the database, in the
   * INFORMATION_SCHEMA format.
   * 
   * @throws SQLException
   *         On a SQL exception
   */
  void retrieveViewInformation()
    throws SQLException
  {
    final InformationSchemaViews informationSchemaViews = getRetrieverConnection()
      .getInformationSchemaViews();

    if (!informationSchemaViews.hasViewsSql())
    {
      LOGGER.log(Level.FINE, "Views SQL statement was not provided");
      return;
    }
    final String viewInformationSql = informationSchemaViews.getViews();

    final Connection connection = getDatabaseConnection();
    final Statement statement = connection.createStatement();
    MetadataResultSet results = null;
    try
    {
      results = new MetadataResultSet(statement
        .executeQuery(viewInformationSql));

      while (results.next())
      {
        final String catalogName = results.getString("TABLE_CATALOG");
        final String schemaName = results.getString("TABLE_SCHEMA");
        final String viewName = quotedName(results.getString("TABLE_NAME"));

        final MutableView view = (MutableView) lookupTable(catalogName,
                                                           schemaName,
                                                           viewName);
        if (view == null)
        {
          LOGGER.log(Level.FINE, String.format("Cannot find table, %s.%s.%s",
                                               catalogName,
                                               schemaName,
                                               viewName));
          continue;
        }

        LOGGER.log(Level.FINER, "Retrieving view information: " + viewName);
        final String definition = results.getString("VIEW_DEFINITION");
        final CheckOptionType checkOption = results
          .getEnum("CHECK_OPTION", CheckOptionType.unknown);
        final boolean updatable = results.getBoolean("IS_UPDATABLE");

        view.appendDefinition(definition);
        view.setCheckOption(checkOption);
        view.setUpdatable(updatable);

        view.addAttributes(results.getAttributes());
      }
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, "Could not retrieve view information", e);
    }
    finally
    {
      if (results != null)
      {
        results.close();
      }
      statement.close();
    }

  }

}
