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


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.ActionOrientationType;
import schemacrawler.schema.CheckOptionType;
import schemacrawler.schema.ConditionTimingType;
import schemacrawler.schema.ConstraintType;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.EventManipulationType;
import schemacrawler.schema.NamedObject;
import sf.util.Utilities;

/**
 * TableRetriever uses database metadata to get the details about the
 * schema.
 * 
 * @author sfatehi
 */
final class TableExRetriever
  extends AbstractRetriever
{

  private static final Logger LOGGER = Logger.getLogger(TableExRetriever.class
      .getName());

  /**
   * Constructs a SchemaCrawler object, from a connection.
   * 
   * @param connection
   *        An open database connection.
   * @param driverClassName
   *        Class name of the JDBC driver
   * @param schemaPatternString
   *        JDBC schema pattern, or null
   * @throws SQLException
   *         On a SQL exception
   */
  TableExRetriever(final RetrieverConnection retrieverConnection)
    throws SQLException
  {
    super(retrieverConnection);
  }

  /**
   * Retrieves table metadata according to the parameters specified. No
   * column metadata is retrieved, for reasons of efficiency.
   * 
   * @param tableTypes
   *        Array of table types
   * @param tablePatternInclude
   *        Table name pattern for table
   * @param useRegExpPattern
   *        True is the table name pattern is a regular expression;
   *        false if the table name pattern is the JDBC pattern
   * @throws SQLException
   *         On a SQL exception
   */
  void retrievePrivileges(final DatabaseObject parent,
      final NamedObjectList namedObjectList)
    throws SQLException
  {
    LOGGER.entering(getClass().getName(), "retrievePrivileges", new Object[]
    { parent, namedObjectList });

    final ResultSet results;

    final boolean privilegesForTable = parent == null;
    if (privilegesForTable)
    {
      results = getRetrieverConnection().getMetaData().getTablePrivileges(
          getRetrieverConnection().getCatalog(),
          getRetrieverConnection().getSchemaPattern(), "%");
    } else
    {
      results = getRetrieverConnection().getMetaData().getColumnPrivileges(
          getRetrieverConnection().getCatalog(),
          getRetrieverConnection().getSchemaPattern(), parent.getName(), "%");
    }
    try
    {
      createPrivileges(results, namedObjectList, privilegesForTable);
    }
    finally
    {
      results.close();
    }

  }

  private void createPrivileges(final ResultSet results,
      final NamedObjectList namedObjectList, final boolean privilegesForTable)
    throws SQLException
  {
    while (results.next())
    {
      final String name;
      if (privilegesForTable)
      {
        name = results.getString(TABLE_NAME);
      } else
      {
        name = results.getString(COLUMN_NAME);
      }
      final NamedObject namedObject = namedObjectList.lookup(name);
      if (namedObject != null)
      {
        final String privilegeName = results.getString("PRIVILEGE");
        final String grantor = results.getString("GRANTOR");
        final String grantee = results.getString("GRANTEE");
        final String isGrantableString = results.getString("IS_GRANTABLE");
        boolean isGrantable = false;
        if (isGrantableString != null
            && isGrantableString.equalsIgnoreCase("YES"))
        {
          isGrantable = true;
        }

        final MutablePrivilege privilege = new MutablePrivilege(privilegeName,
            namedObject);
        privilege.setGrantor(grantor);
        privilege.setGrantee(grantee);
        privilege.setGrantable(isGrantable);
        if (privilegesForTable)
        {
          final MutableTable table = (MutableTable) namedObject;
          table.addPrivilege(privilege);
        } else
        {
          final MutableColumn column = (MutableColumn) namedObject;
          column.addPrivilege(privilege);
        }
      }
    }
  }

  /**
   * Retrieves a view information from the database, in the
   * INFORMATION_SCHEMA format.
   * 
   * @param tables
   *        List of tables and views.
   * @throws SQLException
   *         On a SQL exception
   */
  void retrieveViewInformation(final NamedObjectList tables)
    throws SQLException
  {
    LOGGER.entering(getClass().getName(), "retrieveViewInformation",
        new Object[]
        {});

    final InformationSchemaViews informationSchemaViews = getRetrieverConnection()
        .getInformationSchemaViews();

    if (informationSchemaViews.hasViewsSql())
    {
      LOGGER.log(Level.FINE, "Views SQL statement was not provided");
      return;
    }
    final String viewInformationSql = informationSchemaViews.getViewsSql();

    final Connection connection = getRetrieverConnection().getMetaData()
        .getConnection();
    final Statement statement = connection.createStatement();
    final ResultSet results = statement.executeQuery(viewInformationSql);

    try
    {

      while (results.next())
      {
        // final String catalog = results.getString("TABLE_CATALOG");
        // final String schema = results.getString("TABLE_SCHEMA");
        final String viewName = results.getString("TABLE_NAME");
        LOGGER.log(Level.FINEST, "Retrieving view information for " + viewName);
        String definition = results.getString("VIEW_DEFINITION");
        final CheckOptionType checkOption = CheckOptionType.valueOf(results
            .getString("CHECK_OPTION"));
        final boolean updatable = Utilities.parseBoolean(results
            .getString("IS_UPDATABLE"));

        final MutableView view = (MutableView) tables.lookup(viewName);
        if (view == null)
        {
          LOGGER.log(Level.FINEST, "View not found: " + viewName);
          continue;
        }

        if (!Utilities.isBlank(view.getDefinition()))
        {
          definition = view.getDefinition() + definition;
        }

        view.setDefinition(definition);
        view.setCheckOption(checkOption);
        view.setUpdatable(updatable);
      }
    }
    finally
    {
      statement.close();
      results.close();
    }

  }

  /**
   * Retrieves a trigger information from the database, in the
   * INFORMATION_SCHEMA format.
   * 
   * @param tables
   *        List of tables and views.
   * @throws SQLException
   *         On a SQL exception
   */
  void retrieveTriggerInformation(final NamedObjectList tables)
    throws SQLException
  {
    LOGGER.entering(getClass().getName(), "retrieveTriggerInformation",
        new Object[]
        {});

    final InformationSchemaViews informationSchemaViews = getRetrieverConnection()
        .getInformationSchemaViews();
    final String triggerInformationSql = informationSchemaViews
        .getTriggersSql();
    if (Utilities.isBlank(triggerInformationSql))
    {
      LOGGER.log(Level.FINE,
          "Trigger definition SQL statement was not provided");
      return;
    }

    final Connection connection = getRetrieverConnection().getMetaData()
        .getConnection();
    final Statement statement = connection.createStatement();
    final ResultSet results = statement.executeQuery(triggerInformationSql);

    try
    {

      while (results.next())
      {
        // final String catalog = results.getString("TRIGGER_CATALOG");
        // final String schema = results.getString("TRIGGER_SCHEMA");
        final String triggerName = results.getString("TRIGGER_NAME");
        LOGGER.log(Level.FINEST, "Retrieving trigger information for "
            + triggerName);

        EventManipulationType eventManipulationType = EventManipulationType
            .valueOf(results.getString("EVENT_MANIPULATION"));

        final String eventObjectCatalog = results
            .getString("EVENT_OBJECT_CATALOG");
        final String eventObjectSchema = results
            .getString("EVENT_OBJECT_SCHEMA");
        final String tableName = results.getString("EVENT_OBJECT_TABLE");

        int actionOrder = results.getInt("ACTION_ORDER");
        String actionCondition = results.getString("ACTION_CONDITION");
        String actionStatement = results.getString("ACTION_STATEMENT");
        ActionOrientationType actionOrientation = ActionOrientationType
            .valueOf(results.getString("ACTION_ORIENTATION"));
        ConditionTimingType conditionTiming = ConditionTimingType
            .valueOf(results.getString("CONDITION_TIMING"));

        final MutableTable table = (MutableTable) tables.lookup(tableName);
        if (table == null)
        {
          LOGGER
              .log(Level.FINEST, "Table not found for trigger " + triggerName);
          continue;
        }

        MutableTrigger trigger = new MutableTrigger(triggerName, table);

      }
    }
    finally
    {
      statement.close();
      results.close();
    }

  }

  /**
   * Retrieves a check constraint information from the database, in the
   * INFORMATION_SCHEMA format.
   * 
   * @param tables
   *        List of tables and views.
   * @throws SQLException
   *         On a SQL exception
   */
  void retrieveCheckConstraintInformation(final NamedObjectList tables)
    throws SQLException
  {
    LOGGER.entering(getClass().getName(), "retrieveCheckConstraintInformation",
        new Object[]
        {});

    final Map checkConstraintsMap = new HashMap();

    final InformationSchemaViews informationSchemaViews = getRetrieverConnection()
        .getInformationSchemaViews();

    if (informationSchemaViews.hasTableConstraintsSql())
    {
      LOGGER
          .log(Level.FINE, "Table constraints SQL statement was not provided");
      return;
    }
    final String tableConstraintsInformationSql = informationSchemaViews
        .getTableConstraintsSql();

    final Connection connection = getRetrieverConnection().getMetaData()
        .getConnection();
    Statement statement = connection.createStatement();
    ResultSet results = statement.executeQuery(tableConstraintsInformationSql);

    try
    {
      while (results.next())
      {
        // final String catalog =
        // results.getString("CONSTRAINT_CATALOG");
        // final String schema = results.getString("CONSTRAINT_SCHEMA");
        final String constraintName = results.getString("CONSTRAINT_NAME");
        LOGGER.log(Level.FINEST, "Retrieving constraint information for "
            + constraintName);
        // final String tableCatalog =
        // results.getString("TABLE_CATALOG");
        // final String tableSchema = results.getString("TABLE_SCHEMA");
        final String tableName = results.getString("TABLE_NAME");
        final ConstraintType constraintType = ConstraintType.valueOf(results
            .getString("CONSTRAINT_TYPE"));
        final boolean deferrable = Utilities.parseBoolean(results
            .getString("IS_DEFERRABLE"));
        final boolean initiallyDeferred = Utilities.parseBoolean(results
            .getString("INITIALLY_DEFERRED"));

        if (constraintType == ConstraintType.CHECK)
        {
          final MutableTable table = (MutableTable) tables.lookup(tableName);
          if (table == null)
          {
            LOGGER.log(Level.FINEST, "Table not found: " + tableName);
            continue;
          }

          final MutableTableConstraint checkConstraint = new MutableTableConstraint(
              constraintName, table);
          checkConstraint.setType(constraintType);
          checkConstraint.setDeferrable(deferrable);
          checkConstraint.setInitiallyDeferred(initiallyDeferred);
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

    if (informationSchemaViews.hasCheckConstraintsSql())
    {
      LOGGER
          .log(Level.FINE, "Check constraints SQL statement was not provided");
      return;
    }
    final String checkConstraintInformationSql = informationSchemaViews
        .getCheckConstraintsSql();

    // Get check constraint definitions
    statement = connection.createStatement();
    results = statement.executeQuery(checkConstraintInformationSql);
    try
    {
      while (results.next())
      {
        // final String catalog =
        // results.getString("CONSTRAINT_CATALOG");
        // final String schema = results.getString("CONSTRAINT_SCHEMA");
        final String constraintName = results.getString("CONSTRAINT_NAME");
        LOGGER.log(Level.FINEST, "Retrieving constraint definition for "
            + constraintName);
        String definition = results.getString("CHECK_CLAUSE");

        final MutableTableConstraint checkConstraint = (MutableTableConstraint) checkConstraintsMap
            .get(constraintName);
        if (checkConstraint == null)
        {
          LOGGER.log(Level.FINEST, "Could not add check constraint to table: "
              + constraintName);
          continue;
        }

        if (!Utilities.isBlank(checkConstraint.getDefinition()))
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
    final Collection checkConstraintsCollection = checkConstraintsMap.values();
    for (final Iterator iter = checkConstraintsCollection.iterator(); iter
        .hasNext();)
    {
      final MutableTableConstraint checkConstraint = (MutableTableConstraint) iter
          .next();
      final MutableTable table = (MutableTable) checkConstraint.getParent();
      table.addCheckConstraint(checkConstraint);
    }

  }

}
