package schemacrawler.tools.commandline.command;


import java.util.logging.Level;

import picocli.CommandLine;
import sf.util.SchemaCrawlerLogger;

@CommandLine.Command(name = "",
                     description = "SchemaCrawler command-line")
public class SchemaCrawlerCommandLineCommands
  implements Runnable
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger.getLogger(
    SchemaCrawlerCommandLineCommands.class.getName());
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
  private HelpCommand helpCommand;
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

  @Override
  public void run()
  {

    for (final Runnable command : new Runnable[] {
      helpCommand,
      logCommand,
      configFileCommand,
      connectCommand,
      filterCommand,
      limitCommand,
      grepCommand,
      showCommand,
      sortCommand,
      loadCommand,
      executeCommand
    })
    {
      if (command != null)
      {
        LOGGER.log(Level.INFO,
                   "Running command " + command.getClass().getSimpleName());
        command.run();
      }
    }

  }

}
