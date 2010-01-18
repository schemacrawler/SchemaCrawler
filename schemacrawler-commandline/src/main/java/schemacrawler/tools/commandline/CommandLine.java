package schemacrawler.tools.commandline;


public interface CommandLine {

  void execute()
    throws Exception;

  String getCommand();

}
