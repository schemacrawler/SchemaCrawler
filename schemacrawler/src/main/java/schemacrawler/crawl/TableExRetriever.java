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


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.NamedObject;

/**
 * TableRetriever uses database metadata to get the details about the schema.
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
   * Retrieves table metadata according to the parameters specified. No column
   * metadata is retrieved, for reasons of efficiency.
   * 
   * @param tableTypes
   *          Array of table types
   * @param tablePatternInclude
   *          Table name pattern for table
   * @param useRegExpPattern
   *          True is the table name pattern is a regular expression; false if
   *          the table name pattern is the JDBC pattern
   * @return A list of tables in the database that matech the pattern
   * @throws SQLException
   *           On a SQL exception
   */
  void retrievePrivileges(final DatabaseObject parent,
                          final NamedObjectList namedObjectList)
    throws SQLException
  {
    LOGGER.entering(this.getClass().getName(),
                    "retrievePrivileges",
                    new Object[] {
                      parent, namedObjectList
                    });

    final ResultSet results;

    final boolean privilegesForTable = parent == null;
    if (privilegesForTable)
    {
      results = getRetrieverConnection().getMetaData()
        .getTablePrivileges(getRetrieverConnection().getCatalog(),
                            getRetrieverConnection().getSchemaPattern(),
                            "%");
    }
    else
    {
      results = getRetrieverConnection().getMetaData()
        .getColumnPrivileges(getRetrieverConnection().getCatalog(),
                             getRetrieverConnection().getSchemaPattern(),
                             parent.getName(),
                             "%");
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

}
