package schemacrawler.tools.commandline;


import picocli.CommandLine.Command;
import schemacrawler.tools.commandline.command.*;
import schemacrawler.tools.commandline.shell.*;

@Command(subcommands = {
  CommandLineHelpCommand.class,
  LogCommand.class,
  ConfigFileCommand.class,
  ConnectCommand.class,
  FilterCommand.class,
  GrepCommand.class,
  LimitCommand.class,
  ShowCommand.class,
  SortCommand.class,
  LoadCommand.class,
  ExecuteCommand.class,
  AvailableCommandsCommand.class,
  AvailableServersCommand.class,
  DisconnectCommand.class,
  SweepCommand.class,
  SystemCommand.class,
  ShowStateCommand.class,
  ExitCommand.class
})
public class SchemaCrawlerShellCommands
{

}
