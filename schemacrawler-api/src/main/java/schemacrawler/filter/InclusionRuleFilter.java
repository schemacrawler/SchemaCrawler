/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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
