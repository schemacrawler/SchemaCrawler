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

public class DatabaseObjectFilter<D extends DatabaseObject>
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

    public DatabaseObjectFilter<D> build()
    {
      return new DatabaseObjectFilter<D>(this);
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

  private final Identifiers identifiers;
  private final InclusionRule databaseObjectInclusionRule;

  private DatabaseObjectFilter(final Builder<D> builder)
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
