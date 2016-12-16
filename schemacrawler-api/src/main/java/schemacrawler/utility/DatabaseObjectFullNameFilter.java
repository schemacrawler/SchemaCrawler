/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.utility;


import static sf.util.Utility.isBlank;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Predicate;

import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Schema;
import schemacrawler.schema.SchemaReference;
import schemacrawler.schemacrawler.IncludeAll;
import schemacrawler.schemacrawler.InclusionRule;

public class DatabaseObjectFullNameFilter<D extends DatabaseObject>
  implements Predicate<D>
{

  public static class Builder<D extends DatabaseObject>
  {

    private final Identifiers.Builder identifiersBuilder;
    private InclusionRule databaseObjectInclusionRule;

    private Builder()
    {
      identifiersBuilder = Identifiers.identifiers();
      this.databaseObjectInclusionRule = new IncludeAll();
    }

    public DatabaseObjectFullNameFilter<D> build()
    {
      return new DatabaseObjectFullNameFilter<>(this);
    }

    public Builder<D> withConnection(final Connection connection)
      throws SQLException
    {
      identifiersBuilder.withConnection(connection);
      return this;
    }

    /**
     * Uses the string used to quote database object identifiers as
     * provided.
     *
     * @param identifierQuoteString
     *        Identifier quote string override, or null if not
     *        overridden
     */
    public Builder<D> withIdentifierQuoteString(final String identifierQuoteString)
    {
      identifiersBuilder.withIdentifierQuoteString(identifierQuoteString);
      return this;
    }

    public Builder<D> withInclusionRule(final InclusionRule databaseObjectInclusionRule)
    {
      if (databaseObjectInclusionRule != null)
      {
        this.databaseObjectInclusionRule = databaseObjectInclusionRule;
      }
      else
      {
        this.databaseObjectInclusionRule = new IncludeAll();
      }
      return this;
    }

  }

  public static <D extends DatabaseObject> Builder<D> databaseObjectFullNameFilter()
  {
    return new Builder<>();
  }

  private final Identifiers identifiers;
  private final InclusionRule databaseObjectInclusionRule;

  private DatabaseObjectFullNameFilter(final Builder<D> builder)
  {
    identifiers = builder.identifiersBuilder.build();
    this.databaseObjectInclusionRule = builder.databaseObjectInclusionRule;
  }

  /**
   * Check for database object limiting rules.
   *
   * @param databaseObject
   *        Database object to check
   * @return Whether the table should be included
   */
  @Override
  public boolean test(final D databaseObject)
  {
    if (databaseObject == null)
    {
      return false;
    }

    final boolean include = databaseObjectInclusionRule
      .test(getUnqotedFullName(databaseObject));

    return include;
  }

  private String getUnqotedFullName(final D databaseObject)
  {
    final Schema schema = databaseObject.getSchema();
    final String unquotedName = identifiers
      .unquotedName(databaseObject.getName());
    final String unquotedSchemaName = identifiers
      .unquotedName(schema.getName());
    final String unquotedCatalogName = identifiers
      .unquotedName(schema.getCatalogName());

    final StringBuilder buffer = new StringBuilder(64);

    final String schemaFullName = new SchemaReference(unquotedCatalogName,
                                                      unquotedSchemaName)
                                                        .getFullName();
    if (!isBlank(schemaFullName))
    {
      buffer.append(schemaFullName).append('.');
    }
    if (!isBlank(unquotedName))
    {
      buffer.append(unquotedName);
    }
    return buffer.toString();
  }

}
