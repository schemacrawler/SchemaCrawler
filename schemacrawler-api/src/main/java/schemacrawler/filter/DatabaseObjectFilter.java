/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2014, Sualeh Fatehi.
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
package schemacrawler.filter;


import schemacrawler.schema.DatabaseObject;
import schemacrawler.schemacrawler.IncludeAll;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

public class DatabaseObjectFilter<D extends DatabaseObject>
  implements NamedObjectFilter<D>
{

  private final InclusionRule schemaInclusionRule;
  private final InclusionRule databaseObjectInclusionRule;

  public DatabaseObjectFilter(final SchemaCrawlerOptions options,
                              final InclusionRule databaseObjectInclusionRule)
  {
    if (options != null)
    {
      schemaInclusionRule = options.getSchemaInclusionRule();
    }
    else
    {
      schemaInclusionRule = new IncludeAll();
    }

    if (databaseObjectInclusionRule != null)
    {
      this.databaseObjectInclusionRule = databaseObjectInclusionRule;
    }
    else
    {
      this.databaseObjectInclusionRule = new IncludeAll();
    }
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

    boolean include = true;

    if (include && schemaInclusionRule != null)
    {
      include = schemaInclusionRule.test(databaseObject.getSchema()
        .getFullName());
    }
    if (include && databaseObjectInclusionRule != null)
    {
      include = databaseObjectInclusionRule.test(databaseObject.getFullName());
    }

    return include;
  }

}
