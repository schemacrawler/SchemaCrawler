package schemacrawler.tools.executable;


import schemacrawler.schemacrawler.SchemaCrawlerException;

public interface CommandRegistryEntry
{

  String getCommand();

  Executable newExecutable()
    throws SchemaCrawlerException;

}
