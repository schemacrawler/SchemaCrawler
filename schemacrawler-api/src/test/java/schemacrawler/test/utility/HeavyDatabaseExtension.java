/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.utility;

import static org.junit.jupiter.api.extension.ConditionEvaluationResult.disabled;
import static org.junit.jupiter.api.extension.ConditionEvaluationResult.enabled;
import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;
import static us.fatehi.utility.Utility.isBlank;
import java.util.Optional;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.logging.LoggerFactory;
import org.testcontainers.DockerClientFactory;

public final class HeavyDatabaseExtension implements ExecutionCondition {

  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(final ExtensionContext context) {

    final String databaseVariableName = findValue(context);

    final ConditionEvaluationResult executionCondition =
        evaluateExecutionCondition(databaseVariableName);
    LoggerFactory.getLogger(getClass()).info(() -> String.valueOf(executionCondition));
    return executionCondition;
  }

  public ConditionEvaluationResult evaluateExecutionCondition(final String databaseVariableName) {

    if (!isDockerAvailable()) {
      return disabled(
          "Do NOT run heavy Testcontainers test for databases: Docker is not available on this system");
    }

    if (isSetOverrideForDev()) {
      return enabled("Run heavy Testcontainers test for databases: overridden for development");
    }

    if (isSetToRun()) {
      return enabled("Run heavy Testcontainers test for databases: \"heavydb\" override is set");
    }

    if (isSetToRunForDatabase(databaseVariableName)) {
      return enabled(
          "Run heavy Testcontainers test for databases: database specific override is set");
    }

    return disabled(
        "Do NOT run heavy Testcontainers test for databases: no environmental variables set to run tests");
  }

  private String findValue(final ExtensionContext context) {
    final Optional<HeavyDatabaseTest> heavyDbAnnotation =
        findAnnotation(context.getTestClass(), HeavyDatabaseTest.class);
    return heavyDbAnnotation.map(HeavyDatabaseTest::value).orElse(null);
  }

  private boolean isDockerAvailable() {
    try {
      DockerClientFactory.instance().client();
      return true;
    } catch (final Throwable ex) {
      return false;
    }
  }

  private boolean isSetOverrideForDev() {
    return System.getProperty("usetestcontainers") != null;
  }

  private boolean isSetToRun() {
    final String heavydb = System.getProperty("heavydb");
    final boolean isNotSetHeavyDB =
        heavydb == null
            // this is not the same as the Boolean check
            || heavydb.toLowerCase().equals("false")
            || heavydb.toLowerCase().equals("no");
    return !isNotSetHeavyDB;
  }

  private boolean isSetToRunForDatabase(final String databaseVariableName) {
    if (isBlank(databaseVariableName)) {
      return false;
    }
    return System.getProperty(databaseVariableName) != null;
  }
}
