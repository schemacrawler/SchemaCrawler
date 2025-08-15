/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.filter;

import static schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion.ruleForSchemaInclusion;

import java.util.function.Predicate;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion;
import schemacrawler.schemacrawler.LimitOptions;

final class DatabaseObjectFilter<D extends DatabaseObject> implements Predicate<D> {

  private final InclusionRule databaseObjectInclusionRule;
  private final InclusionRule schemaInclusionRule;

  DatabaseObjectFilter(
      final LimitOptions options,
      final DatabaseObjectRuleForInclusion databaseObjectRuleForInclusion) {
    if (options != null) {
      schemaInclusionRule = options.get(ruleForSchemaInclusion);
    } else {
      schemaInclusionRule = new IncludeAll();
    }

    if (databaseObjectRuleForInclusion != null) {
      this.databaseObjectInclusionRule = options.get(databaseObjectRuleForInclusion);
    } else {
      this.databaseObjectInclusionRule = new IncludeAll();
    }
  }

  /**
   * Check for database object limiting rules.
   *
   * @param databaseObject Database object to check
   * @return Whether the table should be included
   */
  @Override
  public boolean test(final D databaseObject) {
    if (databaseObject == null) {
      return false;
    }

    boolean include = true;

    if (include && schemaInclusionRule != null) {
      include = schemaInclusionRule.test(databaseObject.getSchema().getFullName());
    }
    if (include && databaseObjectInclusionRule != null) {
      include = databaseObjectInclusionRule.test(databaseObject.getFullName());
    }

    return include;
  }
}
