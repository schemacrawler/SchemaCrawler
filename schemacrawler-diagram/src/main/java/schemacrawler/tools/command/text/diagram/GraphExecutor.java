package schemacrawler.tools.command.text.diagram;

import java.util.concurrent.Callable;

interface GraphExecutor extends Callable<Boolean> {

  boolean canGenerate();
}
