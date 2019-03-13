package schemacrawler.tools.commandline;


import schemacrawler.schemacrawler.Options;

public interface OptionsParser<T extends Options>
{

  T parse(String[] args);

  String[] getRemainder();

}
