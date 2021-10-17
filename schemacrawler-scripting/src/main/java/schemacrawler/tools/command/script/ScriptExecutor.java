package schemacrawler.tools.command.script;

import java.util.concurrent.Callable;

interface ScriptExecutor extends Callable<Boolean> {

  boolean canGenerate();
}
