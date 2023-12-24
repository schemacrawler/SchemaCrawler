/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schema.NamedObject;

public class InclusionRuleFilter<N extends NamedObject> implements Predicate<N> {

  private final InclusionRule inclusionRule;

  public InclusionRuleFilter(final InclusionRule inclusionRule, final boolean inclusive) {
    if (inclusionRule != null) {
      this.inclusionRule = inclusionRule;
    } else {
      if (inclusive) {
        this.inclusionRule = new IncludeAll();
      } else {
        this.inclusionRule = new ExcludeAll();
      }
    }
  }

  public boolean isExcludeAll() {
    return inclusionRule instanceof ExcludeAll;
  }

  @Override
  public boolean test(final N namedObject) {
    if (namedObject == null) {
      return false;
    }
    // Schema names may be null
    if (namedObject.getFullName() == null) {
      return false;
    }
    return inclusionRule.test(namedObject.getFullName());
  }

  @Override
  public String toString() {
    return inclusionRule.toString();
  }
}
