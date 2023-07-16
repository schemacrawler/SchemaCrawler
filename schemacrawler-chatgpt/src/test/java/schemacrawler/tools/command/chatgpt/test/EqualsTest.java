package schemacrawler.tools.command.chatgpt.test;

import java.util.function.Function;
import org.junit.jupiter.api.Test;
import nl.jqno.equalsverifier.EqualsVerifier;
import schemacrawler.tools.command.chatgpt.functions.AbstractFunctionDefinition;
import schemacrawler.tools.command.chatgpt.functions.FunctionParameters;
import schemacrawler.tools.command.chatgpt.functions.FunctionReturn;

public class EqualsTest {

  public static class TestFunctionDefinition
      extends AbstractFunctionDefinition<FunctionParameters> {

    protected TestFunctionDefinition(
        final String name, final String description, final Class<FunctionParameters> parameters) {
      super(description, parameters);
    }

    @Override
    public Function<FunctionParameters, FunctionReturn> getExecutor() {
      return null;
    }
  }

  @Test
  public void baseProductVersion() {
    EqualsVerifier.forClass(TestFunctionDefinition.class)
        .withIgnoredFields("catalog")
        .withRedefinedSuperclass()
        .usingGetClass()
        .verify();
  }
}
