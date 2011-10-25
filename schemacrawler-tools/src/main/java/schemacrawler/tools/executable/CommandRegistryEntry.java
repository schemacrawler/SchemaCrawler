package schemacrawler.tools.executable;


public interface CommandRegistryEntry
{

  String getCommand();

  Executable newExecutable();

}
