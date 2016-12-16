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
package schemacrawler.filter;


import java.util.function.Predicate;

import schemacrawler.schema.DatabaseObject;
import schemacrawler.schemacrawler.IncludeAll;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

public class DatabaseObjectFilter<D extends DatabaseObject>
  implements Predicate<D>
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
      include = schemaInclusionRule
        .test(databaseObject.getSchema().getFullName());
    }
    if (include && databaseObjectInclusionRule != null)
    {
      include = databaseObjectInclusionRule.test(databaseObject.getFullName());
    }

    return include;
  }

}
