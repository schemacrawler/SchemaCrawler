package schemacrawler.tools.commandline.command;


import picocli.CommandLine;

@CommandLine.Command(name = "",
                     description = "SchemaCrawler command-line")
public class SchemaCrawlerCommandLineCommands
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
