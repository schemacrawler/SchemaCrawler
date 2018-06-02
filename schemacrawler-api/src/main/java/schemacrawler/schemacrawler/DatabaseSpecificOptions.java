/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.schemacrawler;


import static java.util.Objects.requireNonNull;
import static sf.util.Utility.isBlank;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import schemacrawler.utility.Identifiers;
import schemacrawler.utility.TypeMap;

/**
 * Options that differ from database to database, obtained from the
 * connection. Overridden with provided values.
 *
 * @author Sualeh Fatehi
 */
public class DatabaseSpecificOptions
  implements Options
{

  private final boolean supportsCatalogs;
  private final boolean supportsSchemas;
  private final Identifiers identifiers;
  private final TypeMap typeMap;

  public DatabaseSpecificOptions(final Connection connection,
                                 final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions)
    throws SQLException
  {
    requireNonNull(connection, "No connection provided");
    requireNonNull(databaseSpecificOverrideOptions,
                   "No database specific override options provided");

    DatabaseMetaData metaData;
    try
    {
      metaData = connection.getMetaData();
    }
    catch (final SQLException e)
    {
      // Ignore
      metaData = null;
    }

    final String identifierQuoteString = lookupIdentifierQuoteString(metaData,
                                                                     databaseSpecificOverrideOptions);
    identifiers = Identifiers.identifiers().withConnectionIfPossible(connection)
      .withIdentifierQuoteString(identifierQuoteString).build();

    supportsCatalogs = lookupSupportsCatalogs(metaData,
                                              databaseSpecificOverrideOptions);
    supportsSchemas = lookupSupportsSchemas(metaData,
                                            databaseSpecificOverrideOptions);

    if (databaseSpecificOverrideOptions.hasOverrideForTypeMap())
    {
      typeMap = databaseSpecificOverrideOptions.getTypeMap();
    }
    else
    {
      typeMap = new TypeMap(connection);
    }
  }

  public String getIdentifierQuoteString()
  {
    return identifiers.getIdentifierQuoteString();
  }

  /**
   * Identifier quoting using the default quoting strategy, but with the
   * correct quote string for the database.
   *
   * @return Identifiers
   */
  public Identifiers getIdentifiers()
  {
    return identifiers;
  }

  public TypeMap getTypeMap()
  {
    return typeMap;
  }

  public boolean isSupportsCatalogs()
  {
    return supportsCatalogs;
  }

  public boolean isSupportsSchemas()
  {
    return supportsSchemas;
  }

  @Override
  public String toString()
  {
    return "DatabaseSpecificOptions [supportsSchemas=" + supportsSchemas
           + ", supportsCatalogs=" + supportsCatalogs
           + ", identifierQuoteString=\"" + getIdentifierQuoteString() + "\"]";
  }

  private String lookupIdentifierQuoteString(final DatabaseMetaData metaData,
                                             final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions)
    throws SQLException
  {
    // Default to SQL standard default
    String identifierQuoteString = "\"";

    if (databaseSpecificOverrideOptions != null
        && databaseSpecificOverrideOptions
          .hasOverrideForIdentifierQuoteString())
    {
      identifierQuoteString = databaseSpecificOverrideOptions
        .getIdentifierQuoteString();
    }
    else if (metaData != null)
    {
      identifierQuoteString = metaData.getIdentifierQuoteString();
    }

    if (isBlank(identifierQuoteString))
    {
      identifierQuoteString = "";
    }

    return identifierQuoteString;
  }

  private boolean lookupSupportsCatalogs(final DatabaseMetaData metaData,
                                         final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions)
    throws SQLException
  {
    final boolean supportsCatalogs;
    if (databaseSpecificOverrideOptions != null
        && databaseSpecificOverrideOptions.hasOverrideForSupportsCatalogs())
    {
      supportsCatalogs = databaseSpecificOverrideOptions.isSupportsCatalogs();
    }
    else if (metaData != null)
    {
      supportsCatalogs = metaData.supportsCatalogsInTableDefinitions();
    }
    else
    {
      supportsCatalogs = true;
    }
    return supportsCatalogs;
  }

  private boolean lookupSupportsSchemas(final DatabaseMetaData metaData,
                                        final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions)
    throws SQLException
  {
    final boolean supportsSchemas;
    if (databaseSpecificOverrideOptions != null
        && databaseSpecificOverrideOptions.hasOverrideForSupportsSchemas())
    {
      supportsSchemas = databaseSpecificOverrideOptions.isSupportsSchemas();
    }
    else if (metaData != null)
    {
      supportsSchemas = metaData.supportsSchemasInTableDefinitions();
    }
    else
    {
      supportsSchemas = true;
    }

    return supportsSchemas;
  }

}
