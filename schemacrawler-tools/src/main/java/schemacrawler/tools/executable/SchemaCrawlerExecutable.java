package schemacrawler.tools.executable;


import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import schemacrawler.schema.Database;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.text.base.BaseTextOptions;

public class SchemaCrawlerExecutable
  extends BaseExecutable
{

  private final List<Executable> executables;

  public SchemaCrawlerExecutable(final String commands)
    throws SchemaCrawlerException
  {
    super(commands);

    final Commands commandsList = new Commands(commands);
    if (commandsList.isEmpty())
    {
      throw new SchemaCrawlerException("No command specified");
    }

    final CommandRegistry commandRegistry = new CommandRegistry();
    executables = new ArrayList<Executable>();
    for (final String command: commandsList)
    {
      final Executable executable = commandRegistry.newExecutable(command);
      executables.add(executable);
    }
  }

  @Override
  protected void executeOn(final Database database, final Connection connection)
    throws Exception
  {
    for (final Executable executable: executables)
    {
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable.setOutputOptions(outputOptions);

      final String command = executable.getCommand();

      final BaseTextOptions baseTextOptions = new BaseTextOptions(additionalConfiguration);

      final Commands commands = new Commands(getCommand());
      if (commands.size() > 1)
      {
        if (commands.isFirstCommand(command))
        {
          // First command - no footer
          baseTextOptions.setNoFooter(true);
        }
        else if (commands.isLastCommand(command))
        {
          // Last command - no header, or info
          baseTextOptions.setNoHeader(true);
          baseTextOptions.setNoInfo(true);

          baseTextOptions.setAppendOutput(true);
        }
        else
        {
          // Middle command - no header, footer, or info
          baseTextOptions.setNoHeader(true);
          baseTextOptions.setNoInfo(true);
          baseTextOptions.setNoFooter(true);

          baseTextOptions.setAppendOutput(true);
        }
      }

      Config executableAdditionalConfig = new Config();
      if (additionalConfiguration != null)
      {
        executableAdditionalConfig.putAll(additionalConfiguration);
      }
      executableAdditionalConfig.putAll(baseTextOptions.toConfig());
      executable.setAdditionalConfiguration(executableAdditionalConfig);

      ((BaseExecutable) executable).executeOn(database, connection);
    }
  }
}
