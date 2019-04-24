package schemacrawler.tools.commandline.shell;


import java.io.PrintWriter;

import org.jline.reader.LineReader;
import org.jline.reader.impl.LineReaderImpl;
import picocli.CommandLine;
import schemacrawler.tools.commandline.command.*;

@CommandLine.Command(name = "", description = "SchemaCrawler interactive shell with command completion", footer = {
  "", "Press Ctl-D to exit." }, subcommands = {
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
  SystemCommand.class })
public class SchemaCrawlerShellCommands
  implements Runnable
{

  private PrintWriter out;
  private LineReaderImpl reader;

  public void setReader(final LineReader reader)
  {
    this.reader = (LineReaderImpl) reader;
    out = reader.getTerminal().writer();
  }

  @Override
  public void run()
  {
    out.println(new CommandLine(this).getUsageMessage());
  }

}
