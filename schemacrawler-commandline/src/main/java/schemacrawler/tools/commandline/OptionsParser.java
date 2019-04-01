package schemacrawler.tools.commandline;


public interface OptionsParser
{

  void parse(String[] args);

  String[] getRemainder();

}
