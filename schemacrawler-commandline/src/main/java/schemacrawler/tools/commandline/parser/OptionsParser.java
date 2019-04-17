package schemacrawler.tools.commandline.parser;


public interface OptionsParser
{

  void parse(String[] args);

  String[] getRemainder();

}
