package schemacrawler.tools.commandline.shell;


import picocli.CommandLine;
import schemacrawler.tools.commandline.command.*;

@CommandLine.Command(subcommands = {
  CommandLine.HelpCommand.class,
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
  IsConnectedCommand.class,
  SweepCommand.class,
  SystemCommand.class,
  ExitCommand.class
})
public class SchemaCrawlerShellCommands
{

}
