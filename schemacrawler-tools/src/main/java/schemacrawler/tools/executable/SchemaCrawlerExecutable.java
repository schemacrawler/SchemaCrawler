package schemacrawler.tools.executable;


import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import schemacrawler.schema.Database;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.options.OutputOptions;

public class SchemaCrawlerExecutable
  extends BaseExecutable
{

  public SchemaCrawlerExecutable(final String commands)
  {
    super(commands);
  }

  @Override
  protected void executeOn(final Database database, final Connection connection)
    throws Exception
  {
    final CommandRegistry commandRegistry = new CommandRegistry();
    final Commands commands = new Commands(getCommand());
    if (commands.isEmpty())
    {
      throw new SchemaCrawlerException("No command specified");
    }
    final List<Executable> executables = new ArrayList<Executable>();
    for (final String command: commands)
    {
      final Executable executable = commandRegistry.newExecutable(command);
      executables.add(executable);
    }

    for (final Executable executable: executables)
    {
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable.setAdditionalConfiguration(additionalConfiguration);

      final String command = executable.getCommand();
      final OutputOptions executableOutputOptions = outputOptions.duplicate();
      if (commands.size() > 1)
      {
        if (commands.isFirstCommand(command))
        {
          // First command - no footer
          executableOutputOptions.setNoFooter(true);
        }
        else if (commands.isLastCommand(command))
        {
          // Last command - no header, or info
          executableOutputOptions.setNoHeader(true);
          executableOutputOptions.setNoInfo(true);

          executableOutputOptions.setAppendOutput(true);
        }
        else
        {
          // Middle command - no header, footer, or info
          executableOutputOptions.setNoHeader(true);
          executableOutputOptions.setNoInfo(true);
          executableOutputOptions.setNoFooter(true);

          executableOutputOptions.setAppendOutput(true);
        }
      }
      executable.setOutputOptions(executableOutputOptions);

      ((BaseExecutable) executable).executeOn(database, connection);
    }
  }
}
