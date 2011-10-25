package schemacrawler.tools.integration.velocity;


import schemacrawler.tools.executable.CommandRegistryEntry;
import schemacrawler.tools.executable.Executable;

public class VelocityCommandRegistryEntry
  implements CommandRegistryEntry
{

  public String getCommand()
  {
    return "velocity";
  }

  public Executable newExecutable()
  {
    return new VelocityRenderer();
  }

}
