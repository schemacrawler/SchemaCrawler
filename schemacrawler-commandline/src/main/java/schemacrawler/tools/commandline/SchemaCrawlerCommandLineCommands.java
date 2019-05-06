package schemacrawler.tools.commandline;


import picocli.CommandLine;
import schemacrawler.tools.commandline.command.*;

@CommandLine.Command
class SchemaCrawlerCommandLineCommands
{

  @CommandLine.Mixin
  private ConfigFileCommand configFileCommand;
  @CommandLine.Mixin
  private ConnectCommand connectCommand;
  @CommandLine.Mixin
  private ExecuteCommand executeCommand;
  @CommandLine.Mixin
  private FilterCommand filterCommand;
  @CommandLine.Mixin
  private GrepCommand grepCommand;
  @CommandLine.Mixin
  private LimitCommand limitCommand;
  @CommandLine.Mixin
  private LoadCommand loadCommand;
  @CommandLine.Mixin
  private LogCommand logCommand;
  @CommandLine.Mixin
  private ShowCommand showCommand;
  @CommandLine.Mixin
  private SortCommand sortCommand;

}
