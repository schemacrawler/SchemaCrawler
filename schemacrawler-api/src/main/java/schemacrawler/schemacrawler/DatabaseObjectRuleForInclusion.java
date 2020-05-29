/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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


public enum DatabaseObjectRuleForInclusion
{

  ruleForColumnInclusion("column"),
  ruleForRoutineInclusion("routine"),
  ruleForRoutineParameterInclusion("routine.inout"),
  ruleForSchemaInclusion("schema"),
  ruleForSequenceInclusion("sequence"),
  ruleForSynonymInclusion("synonym"),
  ruleForTableInclusion("table"),
  ;

  private final String key;

  DatabaseObjectRuleForInclusion(final String key)
  {
    this.key = key;
  }

  public String getInclusionConfigKey() {
    return String.format("schemacrawler.%s.pattern.include", key);
  }

  public String getExclusionConfigKey() {
    return String.format("schemacrawler.%s.pattern.exclude", key);
  }

}
