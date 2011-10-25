package schemacrawler.tools.integration.graph;


import schemacrawler.tools.executable.CommandRegistryEntry;
import schemacrawler.tools.executable.Executable;

public class GraphCommandRegistryEntry
  implements CommandRegistryEntry
{

  public String getCommand()
  {
    return "graph";
  }

  public Executable newExecutable()
  {
    return new GraphExecutable();
  }

}
