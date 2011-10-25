package schemacrawler.tools.integration.scripting;


import schemacrawler.tools.executable.CommandRegistryEntry;
import schemacrawler.tools.executable.Executable;

public class ScriptCommandRegistryEntry
  implements CommandRegistryEntry
{

  public String getCommand()
  {
    return "script";
  }

  public Executable newExecutable()
  {
    return new ScriptExecutable();
  }

}
