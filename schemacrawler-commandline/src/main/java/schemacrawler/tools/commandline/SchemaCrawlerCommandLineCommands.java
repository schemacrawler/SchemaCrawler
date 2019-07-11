package schemacrawler.tools.commandline;


import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import schemacrawler.tools.commandline.command.*;

@Command
class SchemaCrawlerCommandLineCommands
{

  @Mixin
  private ConfigFileCommand configfile;
  @Mixin
  private ConnectCommand connect;
  @Mixin
  private ExecuteCommand execute;
  @Mixin
  private FilterCommand filter;
  @Mixin
  private GrepCommand grep;
  @Mixin
  private LimitCommand limit;
  @Mixin
  private LoadCommand load;
  @Mixin
  private LogCommand log;
  @Mixin
  private ShowCommand show;
  @Mixin
  private ShowStateCommand showstate;
  @Mixin
  private SortCommand sort;

}
