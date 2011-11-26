package schemacrawler.tools.executable;


import schemacrawler.schemacrawler.SchemaCrawlerException;

public interface CommandProvider
{

  String getCommand();

  String getHelpResource();

  Executable newExecutable()
    throws SchemaCrawlerException;

}
