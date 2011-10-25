package schemacrawler.tools.integration.freemarker;


import schemacrawler.tools.executable.CommandRegistryEntry;
import schemacrawler.tools.executable.Executable;

public class FreeMarkerCommandRegistryEntry
  implements CommandRegistryEntry
{

  public String getCommand()
  {
    return "freemarker";
  }

  public Executable newExecutable()
  {
    return new FreeMarkerRenderer();
  }

}
