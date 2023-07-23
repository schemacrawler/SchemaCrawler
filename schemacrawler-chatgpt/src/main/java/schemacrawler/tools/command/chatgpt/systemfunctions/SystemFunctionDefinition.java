package schemacrawler.tools.command.chatgpt.systemfunctions;

import static schemacrawler.tools.command.chatgpt.FunctionDefinition.FunctionType.SYSTEM;
import schemacrawler.tools.command.chatgpt.FunctionDefinition;
import schemacrawler.tools.command.chatgpt.functions.NoFunctionParameters;

public interface SystemFunctionDefinition extends FunctionDefinition<NoFunctionParameters> {
  @Override
  default FunctionType getFunctionType() {
    return SYSTEM;
  }
}
