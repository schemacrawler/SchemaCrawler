package schemacrawler.tools.integration.serialization;


import schemacrawler.tools.executable.CommandRegistryEntry;
import schemacrawler.tools.executable.Executable;

public class SerializationCommandRegistryEntry
  implements CommandRegistryEntry
{

  public String getCommand()
  {
    return "serialize";
  }

  public Executable newExecutable()
  {
    return new SerializationExecutable();
  }

}
