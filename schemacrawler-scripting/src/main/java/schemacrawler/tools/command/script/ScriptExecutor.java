package schemacrawler.tools.command.script;

import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import java.util.concurrent.Callable;

public interface ScriptExecutor extends Callable<Boolean> {

  boolean canGenerate();

  void initialize(Map<String, Object> context, Reader reader, Writer writer);
}
