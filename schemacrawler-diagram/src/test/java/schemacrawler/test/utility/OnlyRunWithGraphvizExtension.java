/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.utility;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

import schemacrawler.tools.command.text.diagram.GraphvizUtility;

final class OnlyRunWithGraphvizExtension implements ExecutionCondition {

  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(final ExtensionContext context) {
    if (GraphvizUtility.isGraphvizAvailable()) {
      return ConditionEvaluationResult.enabled("Graphviz is installed on the system");
    } else {
      return ConditionEvaluationResult.disabled("Graphviz is not installed on the system");
    }
  }
}
