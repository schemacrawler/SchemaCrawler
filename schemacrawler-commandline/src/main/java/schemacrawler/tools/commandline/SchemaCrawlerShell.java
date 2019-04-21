package schemacrawler.tools.commandline;


import org.jline.reader.*;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import picocli.CommandLine;
import picocli.shell.jline3.PicocliJLineCompleter;
import schemacrawler.tools.commandline.shellcommand.SchemaCrawlerShellCommands;
import schemacrawler.tools.commandline.shellcommand.StateFactory;

public class SchemaCrawlerShell
{

  public static void main(final String[] args)
  {
    try
    {
      final StateFactory stateFactory = new StateFactory();
      final SchemaCrawlerShellCommands commands = new SchemaCrawlerShellCommands();
      final CommandLine cmd = new CommandLine(commands, stateFactory);
      final Terminal terminal = TerminalBuilder.builder().build();
      final LineReader reader = LineReaderBuilder.builder().terminal(terminal)
        .completer(new PicocliJLineCompleter(cmd.getCommandSpec()))
        .parser(new DefaultParser()).build();
      commands.setReader(reader);
      final String prompt = "schemacrawler> ";

      String line;
      while (true)
      {
        try
        {
          line = reader.readLine(prompt, null, (MaskingCallback) null, null);
          final ParsedLine pl = reader.getParser().parse(line, 0);
          final String[] arguments = pl.words().toArray(new String[0]);

          final CommandLine currentCmd = new CommandLine(commands,
                                                         stateFactory);
          currentCmd.parseWithHandlers(new CommandLine.RunLast(),
                                       new CommandLine.DefaultExceptionHandler(),
                                       arguments);
        }
        catch (final UserInterruptException e)
        {
          // Ignore
        }
        catch (final EndOfFileException e)
        {
          return;
        }
      }
    }
    catch (final Throwable t)
    {
      t.printStackTrace();
    }
  }

}
