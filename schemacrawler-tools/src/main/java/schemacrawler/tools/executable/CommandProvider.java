package schemacrawler.tools.executable;


import schemacrawler.schemacrawler.SchemaCrawlerException;

public interface CommandProvider
{

  String getCommand();

  Executable newExecutable()
    throws SchemaCrawlerException;

  String getHelpResource();

}
