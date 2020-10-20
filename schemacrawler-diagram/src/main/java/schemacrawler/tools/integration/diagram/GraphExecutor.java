package schemacrawler.tools.integration.diagram;

import java.util.concurrent.Callable;

interface GraphExecutor extends Callable<Boolean> {

  boolean canGenerate();
}
