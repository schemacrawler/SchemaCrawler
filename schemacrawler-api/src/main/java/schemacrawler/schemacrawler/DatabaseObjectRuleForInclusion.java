/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schemacrawler;

public enum DatabaseObjectRuleForInclusion {
  ruleForColumnInclusion("column", false),
  ruleForRoutineInclusion("routine", true),
  ruleForRoutineParameterInclusion("routine.inout", false),
  ruleForSchemaInclusion("schema", false),
  ruleForSequenceInclusion("sequence", true),
  ruleForSynonymInclusion("synonym", true),
  ruleForTableInclusion("table", false),
  ;

  private final String key;
  private final boolean excludeByDefault;

  DatabaseObjectRuleForInclusion(final String key, final boolean excludeByDefault) {
    this.key = key;
    this.excludeByDefault = excludeByDefault;
  }

  public String getKey() {
    return key;
  }

  public boolean isExcludeByDefault() {
    return excludeByDefault;
  }
}
