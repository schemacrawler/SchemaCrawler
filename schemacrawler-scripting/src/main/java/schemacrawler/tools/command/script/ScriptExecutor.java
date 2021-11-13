package schemacrawler.tools.command.script;

import java.io.Reader;
import java.io.Writer;
import java.util.Map;

public interface ScriptExecutor extends Runnable {

  boolean canGenerate();

  void initialize(Map<String, Object> context, Reader reader, Writer writer);
}
