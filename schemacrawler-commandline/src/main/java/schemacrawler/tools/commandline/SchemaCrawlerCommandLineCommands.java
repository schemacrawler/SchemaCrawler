package schemacrawler.tools.commandline;


import picocli.CommandLine;
import schemacrawler.tools.commandline.command.*;

@CommandLine.Command
class SchemaCrawlerCommandLineCommands
{

  @CommandLine.Mixin
  private ConfigFileCommand configfile;
  @CommandLine.Mixin
  private ConnectCommand connect;
  @CommandLine.Mixin
  private ExecuteCommand execute;
  @CommandLine.Mixin
  private FilterCommand filter;
  @CommandLine.Mixin
  private GrepCommand grep;
  @CommandLine.Mixin
  private LimitCommand limit;
  @CommandLine.Mixin
  private LoadCommand load;
  @CommandLine.Mixin
  private LogCommand log;
  @CommandLine.Mixin
  private ShowCommand show;
  @CommandLine.Mixin
  private SortCommand sort;

}
