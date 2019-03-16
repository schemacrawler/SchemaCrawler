package schemacrawler.tools.commandline;


import schemacrawler.schemacrawler.Options;
import schemacrawler.schemacrawler.SchemaCrawlerException;

public interface OptionsParser<T extends Options>
{

  T parse(String[] args)
    throws SchemaCrawlerException;

  String[] getRemainder();

}
