package schemacrawler.tools.integration.graph;


import java.util.concurrent.Callable;

interface GraphExecutor
  extends Callable<Boolean>
{

  boolean canGenerate();

}
