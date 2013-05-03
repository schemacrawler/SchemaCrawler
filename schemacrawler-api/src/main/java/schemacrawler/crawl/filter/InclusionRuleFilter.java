/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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
package schemacrawler.crawl.filter;


import schemacrawler.schema.DatabaseObject;
import schemacrawler.schemacrawler.InclusionRule;

class InclusionRuleFilter<D extends DatabaseObject>
  implements DatabaseObjectFilter<D>
{
  private final InclusionRule inclusionRule;

  public InclusionRuleFilter(final InclusionRule inclusionRule)
  {
    if (inclusionRule != null)
    {
      this.inclusionRule = inclusionRule;
    }
    else
    {
      this.inclusionRule = InclusionRule.INCLUDE_ALL;
    }
  }

  @Override
  public boolean include(final D databaseObject)
  {
    if (databaseObject == null)
    {
      return false;
    }
    return inclusionRule.include(databaseObject.getFullName());
  }

}
