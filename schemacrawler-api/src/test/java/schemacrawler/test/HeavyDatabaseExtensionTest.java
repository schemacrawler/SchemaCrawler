/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import us.fatehi.test.utility.extensions.HeavyDatabaseExtension;
import us.fatehi.test.utility.extensions.WithSystemProperty;

@Disabled("Disable for now")
public class HeavyDatabaseExtensionTest {

  @Test
  public void testRunDatabaseTests_NoOverrides() {

    final ConditionEvaluationResult executionCondition =
        new HeavyDatabaseExtension().evaluateExecutionCondition((String) null);

    assertThat(executionCondition.isDisabled(), is(true));
  }

  @Test
  @WithSystemProperty(key = "usetestcontainers", value = "true")
  public void testRunDatabaseTests_WithDevOverride() {

    final ConditionEvaluationResult executionCondition =
        new HeavyDatabaseExtension().evaluateExecutionCondition((String) null);

    assertThat(executionCondition.isDisabled(), is(false));
  }

  @Test
  @WithSystemProperty(key = "heavydb", value = "true")
  public void testRunDatabaseTests_WithGenericOverride() {

    final ConditionEvaluationResult executionCondition =
        new HeavyDatabaseExtension().evaluateExecutionCondition((String) null);

    assertThat(executionCondition.isDisabled(), is(false));
  }

  @Test
  @WithSystemProperty(key = "heavydb", value = "false")
  // @WithSystemProperty(key = "dbname", value = "false")
  public void testRunDatabaseTests_WithOverrides_NotRun() {

    final ConditionEvaluationResult executionCondition =
        new HeavyDatabaseExtension().evaluateExecutionCondition("dbname");

    assertThat(executionCondition.isDisabled(), is(true));
  }

  @Test
  @WithSystemProperty(key = "heavydb", value = "false")
  // @WithSystemProperty(key = "dbname", value = "true")
  @Disabled("Cannot set two system properties, since the annotation is not repeatable")
  public void testRunDatabaseTests_WithSpecificOverride() {

    final ConditionEvaluationResult executionCondition =
        new HeavyDatabaseExtension().evaluateExecutionCondition("dbname");

    assertThat(executionCondition.isDisabled(), is(false));
  }
}
