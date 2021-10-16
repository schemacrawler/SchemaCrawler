package schemacrawler.test.utility;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

import schemacrawler.tools.command.text.diagram.GraphvizUtility;

public class DisabledWithoutGraphviz implements ExecutionCondition {

  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(final ExtensionContext context) {
    if (GraphvizUtility.isGraphvizAvailable()) {
      return ConditionEvaluationResult.enabled("Graphviz is installed on the system");
    } else {
      return ConditionEvaluationResult.enabled("Graphviz is not installed on the system");
    }
  }
}
