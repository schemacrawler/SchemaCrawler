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
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.DatabaseObject;
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
   *          An open database connection.
   * @param driverClassName
   *          Class name of the JDBC driver
   * @param schemaPatternString
   *          JDBC schema pattern, or null
   * @throws SQLException
   *           On a SQL exception
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
   *          Array of table types
   * @param tablePatternInclude
   *          Table name pattern for table
   * @param useRegExpPattern
   *          True is the table name pattern is a regular expression;
   *          false if the table name pattern is the JDBC pattern
   * @throws SQLException
   *           On a SQL exception
   */
  void retrievePrivileges(final DatabaseObject parent,
                          final NamedObjectList namedObjectList)
    throws SQLException
  {
    LOGGER.entering(getClass().getName(), "retrievePrivileges", new Object[] {
        parent, namedObjectList
    });

    final ResultSet results;

    final boolean privilegesForTable = parent == null;
    if (privilegesForTable)
    {
      results = getRetrieverConnection().getMetaData()
        .getTablePrivileges(getRetrieverConnection().getCatalog(),
                            getRetrieverConnection().getSchemaPattern(), "%");
    }
    else
    {
      results = getRetrieverConnection().getMetaData()
        .getColumnPrivileges(getRetrieverConnection().getCatalog(),
                             getRetrieverConnection().getSchemaPattern(),
                             parent.getName(), "%");
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
                                final NamedObjectList namedObjectList,
                                final boolean privilegesForTable)
    throws SQLException
  {
    while (results.next())
    {
      final String name;
      if (privilegesForTable)
      {
        name = results.getString(TABLE_NAME);
      }
      else
      {
        name = results.getString(COLUMN_NAME);
      }
      final NamedObject namedObject = namedObjectList.lookup(name);
      if (namedObject != null)
      {
        final String privilegeName = results
          .getString(AbstractRetriever.PRIVILEGE);
        final String grantor = results.getString(AbstractRetriever.GRANTOR);
        final String grantee = results.getString(AbstractRetriever.GRANTEE);
        final String isGrantableString = results
          .getString(AbstractRetriever.IS_GRANTABLE);
        boolean isGrantable = false;
        if (isGrantableString != null
            && isGrantableString.equalsIgnoreCase("YES"))
        {
          isGrantable = true;
        }

        final MutablePrivilege privilege = new MutablePrivilege();
        privilege.setName(privilegeName);
        privilege.setParent(namedObject);
        privilege.setGrantor(grantor);
        privilege.setGrantee(grantee);
        privilege.setGrantable(isGrantable);
        if (privilegesForTable)
        {
          final MutableTable table = (MutableTable) namedObject;
          table.addPrivilege(privilege);
        }
        else
        {
          final MutableColumn column = (MutableColumn) namedObject;
          column.addPrivilege(privilege);
        }
      }
    }
  }

  /**
   * Retrieves a view definitions from the database.
   * 
   * @param tables
   *          List of tables and views.
   * @throws SQLException
   *           On a SQL exception
   */
  void retrieveViewDefinitions(final NamedObjectList tables)
    throws SQLException
  {
    LOGGER.entering(getClass().getName(), "retrieveViewDefinitions",
                    new Object[] {});
    
    String viewDefinitionsSql = getRetrieverConnection().getViewDefinitionsSql();
    if (Utilities.isBlank(viewDefinitionsSql)) {
      LOGGER.log(Level.FINE, "View definition SQL statement was not provided");
      return;
    }      
    
    Connection connection = getRetrieverConnection().getMetaData().getConnection();
    Statement statement = connection.createStatement();
    final ResultSet results = statement.executeQuery(viewDefinitionsSql);
    
    try
    {

      while (results.next())
      {     
//        final String catalog = results.getString("VIEW_CAT");
//        final String schema = results.getString("VIEW_SCHEM");
        final String viewName = results.getString("VIEW_NAME");
        LOGGER.log(Level.FINEST, "Retrieving view definition: " + viewName);
        final String definition = results.getString("VIEW_DEFINITION");

        final MutableTable view = (MutableTable) tables.lookup(viewName);
        if (view == null) {
          LOGGER.log(Level.FINEST, "View not found: " + viewName);
          continue;
        }
        
        view.setDefinition(definition);
      }
    }
    finally
    {
      statement.close();
      results.close();
    }

  }

}
