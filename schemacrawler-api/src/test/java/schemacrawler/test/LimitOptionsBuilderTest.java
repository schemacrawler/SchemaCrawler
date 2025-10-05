/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static schemacrawler.schema.RoutineType.function;
import static schemacrawler.schema.RoutineType.procedure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.inclusionrule.RegularExpressionRule;
import schemacrawler.schema.RoutineType;
import schemacrawler.schemacrawler.DatabaseObjectRuleForInclusion;
import schemacrawler.schemacrawler.LimitOptions;
import schemacrawler.schemacrawler.LimitOptionsBuilder;

public class LimitOptionsBuilderTest {
  @Test
  public void inclusionRules() {

    final IncludeAll includeAll = new IncludeAll();
    final ExcludeAll excludeAll = new ExcludeAll();
    final RegularExpressionRule inclusionRule = new RegularExpressionRule(".*PUBLIC.*", "");
    final Pattern pattern = Pattern.compile(".*PUBLIC.*");

    final LimitOptionsBuilder limitOptionsBuilder = LimitOptionsBuilder.builder();
    LimitOptions limitOptions;

    // 1. Default values
    limitOptions = limitOptionsBuilder.toOptions();
    for (final DatabaseObjectRuleForInclusion databaseObjectRuleForInclusion :
        DatabaseObjectRuleForInclusion.values()) {
      if (databaseObjectRuleForInclusion.isExcludeByDefault()) {
        assertThat(limitOptions.get(databaseObjectRuleForInclusion), is(excludeAll));
      } else {
        assertThat(limitOptions.get(databaseObjectRuleForInclusion), is(includeAll));
      }
    }

    // 2. Non-default values
    for (final DatabaseObjectRuleForInclusion databaseObjectRuleForInclusion :
        DatabaseObjectRuleForInclusion.values()) {
      limitOptionsBuilder.include(databaseObjectRuleForInclusion, inclusionRule);
    }
    limitOptions = limitOptionsBuilder.toOptions();
    for (final DatabaseObjectRuleForInclusion databaseObjectRuleForInclusion :
        DatabaseObjectRuleForInclusion.values()) {
      assertThat(limitOptions.get(databaseObjectRuleForInclusion), is(inclusionRule));
    }

    // 3. Set to null, so effectively use defaults
    for (final DatabaseObjectRuleForInclusion databaseObjectRuleForInclusion :
        DatabaseObjectRuleForInclusion.values()) {
      limitOptionsBuilder.include(databaseObjectRuleForInclusion, null);
    }
    limitOptions = limitOptionsBuilder.toOptions();
    for (final DatabaseObjectRuleForInclusion databaseObjectRuleForInclusion :
        DatabaseObjectRuleForInclusion.values()) {
      if (databaseObjectRuleForInclusion.isExcludeByDefault()) {
        assertThat(limitOptions.get(databaseObjectRuleForInclusion), is(excludeAll));
      } else {
        assertThat(limitOptions.get(databaseObjectRuleForInclusion), is(includeAll));
      }
    }

    // 4. Set specific values, with inclusion rule
    limitOptions = limitOptionsBuilder.includeSchemas(inclusionRule).toOptions();
    assertThat(
        limitOptions.get(DatabaseObjectRuleForInclusion.ruleForSchemaInclusion), is(inclusionRule));
    limitOptions = limitOptionsBuilder.includeTables(inclusionRule).toOptions();
    assertThat(
        limitOptions.get(DatabaseObjectRuleForInclusion.ruleForTableInclusion), is(inclusionRule));
    limitOptions = limitOptionsBuilder.includeRoutines(inclusionRule).toOptions();
    assertThat(
        limitOptions.get(DatabaseObjectRuleForInclusion.ruleForRoutineInclusion),
        is(inclusionRule));
    limitOptions = limitOptionsBuilder.includeSequences(inclusionRule).toOptions();
    assertThat(
        limitOptions.get(DatabaseObjectRuleForInclusion.ruleForSequenceInclusion),
        is(inclusionRule));
    limitOptions = limitOptionsBuilder.includeSynonyms(inclusionRule).toOptions();
    assertThat(
        limitOptions.get(DatabaseObjectRuleForInclusion.ruleForSynonymInclusion),
        is(inclusionRule));
    limitOptions = limitOptionsBuilder.includeColumns(inclusionRule).toOptions();
    assertThat(
        limitOptions.get(DatabaseObjectRuleForInclusion.ruleForColumnInclusion), is(inclusionRule));
    limitOptions = limitOptionsBuilder.includeRoutineParameters(inclusionRule).toOptions();
    assertThat(
        limitOptions.get(DatabaseObjectRuleForInclusion.ruleForRoutineParameterInclusion),
        is(inclusionRule));

    // 5. Set specific values, with pattern
    limitOptions = limitOptionsBuilder.includeSchemas(pattern).toOptions();
    assertThat(
        limitOptions.get(DatabaseObjectRuleForInclusion.ruleForSchemaInclusion), is(inclusionRule));
    limitOptions = limitOptionsBuilder.includeTables(pattern).toOptions();
    assertThat(
        limitOptions.get(DatabaseObjectRuleForInclusion.ruleForTableInclusion), is(inclusionRule));
    limitOptions = limitOptionsBuilder.includeRoutines(pattern).toOptions();
    assertThat(
        limitOptions.get(DatabaseObjectRuleForInclusion.ruleForRoutineInclusion),
        is(inclusionRule));
    limitOptions = limitOptionsBuilder.includeSequences(pattern).toOptions();
    assertThat(
        limitOptions.get(DatabaseObjectRuleForInclusion.ruleForSequenceInclusion),
        is(inclusionRule));
    limitOptions = limitOptionsBuilder.includeSynonyms(pattern).toOptions();
    assertThat(
        limitOptions.get(DatabaseObjectRuleForInclusion.ruleForSynonymInclusion),
        is(inclusionRule));
    limitOptions = limitOptionsBuilder.includeColumns(pattern).toOptions();
    assertThat(
        limitOptions.get(DatabaseObjectRuleForInclusion.ruleForColumnInclusion), is(inclusionRule));
    limitOptions = limitOptionsBuilder.includeRoutineParameters(pattern).toOptions();
    assertThat(
        limitOptions.get(DatabaseObjectRuleForInclusion.ruleForRoutineParameterInclusion),
        is(inclusionRule));

    // 6. Set include all
    limitOptions = limitOptionsBuilder.includeAllRoutines().toOptions();
    assertThat(
        limitOptions.get(DatabaseObjectRuleForInclusion.ruleForRoutineInclusion), is(includeAll));
    limitOptions = limitOptionsBuilder.includeAllSequences().toOptions();
    assertThat(
        limitOptions.get(DatabaseObjectRuleForInclusion.ruleForSequenceInclusion), is(includeAll));
    limitOptions = limitOptionsBuilder.includeAllSynonyms().toOptions();
    assertThat(
        limitOptions.get(DatabaseObjectRuleForInclusion.ruleForSynonymInclusion), is(includeAll));
  }

  @Test
  public void newOptions() {
    assertDefaultLimitOptions(LimitOptionsBuilder.newLimitOptions());
    assertDefaultLimitOptions(LimitOptionsBuilder.builder().fromOptions(null).toOptions());
  }

  @Test
  public void routineTypes() {

    final LimitOptionsBuilder limitOptionsBuilder = LimitOptionsBuilder.builder();
    LimitOptions limitOptions;
    LimitOptions limitOptionsPlayback;

    // 1. Test defaults
    limitOptions = limitOptionsBuilder.toOptions();
    assertThat(limitOptions.routineTypes(), containsInAnyOrder(function, procedure));
    limitOptionsPlayback = LimitOptionsBuilder.builder().fromOptions(limitOptions).toOptions();
    assertThat(limitOptionsPlayback.routineTypes(), containsInAnyOrder(function, procedure));

    // 2. Test empty collection
    limitOptionsBuilder.routineTypes(new ArrayList<>());
    limitOptions = limitOptionsBuilder.toOptions();
    assertThat(limitOptions.routineTypes(), is(empty()));
    limitOptionsPlayback = LimitOptionsBuilder.builder().fromOptions(limitOptions).toOptions();
    assertThat(limitOptionsPlayback.routineTypes(), is(empty()));

    // 3. Test collection with non-defaults
    limitOptionsBuilder.routineTypes(asList(function));
    limitOptions = limitOptionsBuilder.toOptions();
    assertThat(limitOptions.routineTypes(), containsInAnyOrder(function));
    limitOptionsPlayback = LimitOptionsBuilder.builder().fromOptions(limitOptions).toOptions();
    assertThat(limitOptionsPlayback.routineTypes(), containsInAnyOrder(function));

    // 4. Test null collection (which resets to defaults)
    limitOptionsBuilder.routineTypes((Collection<RoutineType>) null);
    limitOptions = limitOptionsBuilder.toOptions();
    assertThat(limitOptions.routineTypes(), containsInAnyOrder(function, procedure));
    limitOptionsPlayback = LimitOptionsBuilder.builder().fromOptions(limitOptions).toOptions();
    assertThat(limitOptionsPlayback.routineTypes(), containsInAnyOrder(function, procedure));
  }

  @Test
  public void routineTypesWithString() {

    final LimitOptionsBuilder limitOptionsBuilder = LimitOptionsBuilder.builder();
    LimitOptions limitOptions;
    LimitOptions limitOptionsPlayback;

    // 1. Test defaults
    limitOptions = limitOptionsBuilder.toOptions();
    assertThat(limitOptions.routineTypes(), containsInAnyOrder(function, procedure));
    limitOptionsPlayback = LimitOptionsBuilder.builder().fromOptions(limitOptions).toOptions();
    assertThat(limitOptionsPlayback.routineTypes(), containsInAnyOrder(function, procedure));

    // 2. Test empty string
    limitOptionsBuilder.routineTypes("");
    limitOptions = limitOptionsBuilder.toOptions();
    assertThat(limitOptions.routineTypes(), is(empty()));
    limitOptionsPlayback = LimitOptionsBuilder.builder().fromOptions(limitOptions).toOptions();
    assertThat(limitOptionsPlayback.routineTypes(), is(empty()));

    // 3. Test string with non-defaults
    limitOptionsBuilder.routineTypes("function");
    limitOptions = limitOptionsBuilder.toOptions();
    assertThat(limitOptions.routineTypes(), containsInAnyOrder(function));
    limitOptionsPlayback = LimitOptionsBuilder.builder().fromOptions(limitOptions).toOptions();
    assertThat(limitOptionsPlayback.routineTypes(), containsInAnyOrder(function));

    // 4. Test string with list
    limitOptionsBuilder.routineTypes("function,PROCEDURE");
    limitOptions = limitOptionsBuilder.toOptions();
    assertThat(limitOptions.routineTypes(), containsInAnyOrder(function, procedure));
    limitOptionsPlayback = LimitOptionsBuilder.builder().fromOptions(limitOptions).toOptions();
    assertThat(limitOptionsPlayback.routineTypes(), containsInAnyOrder(function, procedure));

    // 5. Test string with list with bad values
    limitOptionsBuilder.routineTypes("function,bad_value");
    limitOptions = limitOptionsBuilder.toOptions();
    assertThat(limitOptions.routineTypes(), containsInAnyOrder(function));
    limitOptionsPlayback = LimitOptionsBuilder.builder().fromOptions(limitOptions).toOptions();
    assertThat(limitOptionsPlayback.routineTypes(), containsInAnyOrder(function));

    // 6. Test null string (which resets to defaults)
    limitOptionsBuilder.routineTypes((String) null);
    limitOptions = limitOptionsBuilder.toOptions();
    assertThat(limitOptions.routineTypes(), containsInAnyOrder(function, procedure));
    limitOptionsPlayback = LimitOptionsBuilder.builder().fromOptions(limitOptions).toOptions();
    assertThat(limitOptionsPlayback.routineTypes(), containsInAnyOrder(function, procedure));
  }

  @Test
  public void tableNamePattern() {

    final LimitOptionsBuilder limitOptionsBuilder = LimitOptionsBuilder.builder();
    LimitOptions limitOptions;

    // 1. Test defaults
    limitOptions = limitOptionsBuilder.toOptions();
    assertThat(limitOptions.tableNamePattern(), is(nullValue()));

    // 2. Test empty string
    limitOptionsBuilder.tableNamePattern("");
    limitOptions = limitOptionsBuilder.toOptions();
    assertThat(limitOptions.tableNamePattern(), is(nullValue()));

    // 3. Test blank string
    limitOptionsBuilder.tableNamePattern("\t\t");
    limitOptions = limitOptionsBuilder.toOptions();
    assertThat(limitOptions.tableNamePattern(), is(nullValue()));

    // 4. Test pattern
    limitOptionsBuilder.tableNamePattern("pattern");
    limitOptions = limitOptionsBuilder.toOptions();
    assertThat(limitOptions.tableNamePattern(), is("pattern"));

    // 5. Test null
    limitOptionsBuilder.tableNamePattern(null);
    limitOptions = limitOptionsBuilder.toOptions();
    assertThat(limitOptions.tableNamePattern(), is(nullValue()));
  }

  @Test
  public void tableTypes() {

    final LimitOptionsBuilder limitOptionsBuilder = LimitOptionsBuilder.builder();
    LimitOptions limitOptions;
    LimitOptions limitOptionsPlayback;

    // 1. Test defaults
    limitOptions = limitOptionsBuilder.toOptions();
    assertThat(
        limitOptions.tableTypes().toArray(), is(new String[] {"TABLE", "VIEW", "BASE TABLE"}));
    limitOptionsPlayback = LimitOptionsBuilder.builder().fromOptions(limitOptions).toOptions();
    assertThat(
        limitOptionsPlayback.tableTypes().toArray(),
        is(new String[] {"TABLE", "VIEW", "BASE TABLE"}));

    // 2. Test empty collection
    limitOptionsBuilder.tableTypes(new ArrayList<>());
    limitOptions = limitOptionsBuilder.toOptions();
    assertThat(limitOptions.tableTypes().toArray(), is(new String[0]));
    limitOptionsPlayback = LimitOptionsBuilder.builder().fromOptions(limitOptions).toOptions();
    assertThat(limitOptionsPlayback.tableTypes().toArray(), is(new String[0]));

    // 3. Test collection with non-defaults
    limitOptionsBuilder.tableTypes("TABLE");
    limitOptions = limitOptionsBuilder.toOptions();
    assertThat(limitOptions.tableTypes().toArray(), is(new String[] {"TABLE"}));
    limitOptionsPlayback = LimitOptionsBuilder.builder().fromOptions(limitOptions).toOptions();
    assertThat(limitOptionsPlayback.tableTypes().toArray(), is(new String[] {"TABLE"}));

    // 4. Test null collection (which resets to defaults)
    limitOptionsBuilder.tableTypes((Collection<String>) null);
    limitOptions = limitOptionsBuilder.toOptions();
    assertThat(limitOptions.tableTypes().toArray(), is(nullValue()));
    limitOptionsPlayback = LimitOptionsBuilder.builder().fromOptions(limitOptions).toOptions();
    assertThat(limitOptionsPlayback.tableTypes().toArray(), is(nullValue()));

    // 5. Test null varargs (which resets to defaults)
    limitOptionsBuilder.tableTypes((String[]) null);
    limitOptions = limitOptionsBuilder.toOptions();
    assertThat(limitOptions.tableTypes().toArray(), is(nullValue()));
    limitOptionsPlayback = LimitOptionsBuilder.builder().fromOptions(limitOptions).toOptions();
    assertThat(limitOptionsPlayback.tableTypes().toArray(), is(nullValue()));
  }

  private void assertDefaultLimitOptions(final LimitOptions limitOptions) {
    assertThat(
        limitOptions.routineTypes(), is(EnumSet.of(RoutineType.function, RoutineType.procedure)));
    assertThat(
        asList(limitOptions.tableTypes().toArray()),
        containsInAnyOrder("TABLE", "VIEW", "BASE TABLE"));

    final IncludeAll includeAll = new IncludeAll();
    final ExcludeAll excludeAll = new ExcludeAll();
    for (final DatabaseObjectRuleForInclusion databaseObjectRuleForInclusion :
        DatabaseObjectRuleForInclusion.values()) {
      if (databaseObjectRuleForInclusion.isExcludeByDefault()) {
        assertThat(limitOptions.get(databaseObjectRuleForInclusion), is(excludeAll));
      } else {
        assertThat(limitOptions.get(databaseObjectRuleForInclusion), is(includeAll));
      }
    }
  }
}
