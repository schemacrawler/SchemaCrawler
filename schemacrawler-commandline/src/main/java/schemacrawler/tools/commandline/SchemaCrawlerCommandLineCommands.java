package schemacrawler.tools.commandline;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import schemacrawler.tools.commandline.command.ConfigFileCommand;
import schemacrawler.tools.commandline.command.ConnectCommand;
import schemacrawler.tools.commandline.command.ExecuteCommand;
import schemacrawler.tools.commandline.command.FilterCommand;
import schemacrawler.tools.commandline.command.GrepCommand;
import schemacrawler.tools.commandline.command.LimitCommand;
import schemacrawler.tools.commandline.command.LoadCommand;
import schemacrawler.tools.commandline.command.LogCommand;
import schemacrawler.tools.commandline.command.ShowStateCommand;

@Command
class SchemaCrawlerCommandLineCommands {

  @Mixin private ConfigFileCommand configfile;
  @Mixin private ConnectCommand connect;
  @Mixin private ExecuteCommand execute;
  @Mixin private FilterCommand filter;
  @Mixin private GrepCommand grep;
  @Mixin private LimitCommand limit;
  @Mixin private LoadCommand load;
  @Mixin private LogCommand log;
  @Mixin private ShowStateCommand showstate;
}
