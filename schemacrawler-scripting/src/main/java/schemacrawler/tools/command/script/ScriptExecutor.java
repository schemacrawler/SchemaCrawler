package schemacrawler.tools.command.script;

import java.util.Map;
import java.util.concurrent.Callable;

public interface ScriptExecutor extends Callable<Boolean> {

  boolean canGenerate();

  void setContext(Map<String, Object> context);
}
