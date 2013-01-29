package schemacrawler.tools.executable;


import java.sql.Connection;

import schemacrawler.schema.Database;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.text.base.BaseTextOptionsBuilder;

public final class CommandDaisyChainExecutable
  extends BaseCommandChainExecutable
{

  public CommandDaisyChainExecutable(final String commands)
    throws SchemaCrawlerException
  {
    super(commands);
  }

  @Override
  protected void executeOn(final Database database, final Connection connection)
    throws Exception
  {
    // Commands are processed at execution time. That is, after
    // all configuration settings are made.
    final Commands commands = new Commands(getCommand());
    if (commands.isEmpty())
    {
      throw new SchemaCrawlerException("No command specified");
    }

    for (final String command: commands)
    {
      final Executable executable = addNext(command);

      final BaseTextOptionsBuilder baseTextOptions = new BaseTextOptionsBuilder(additionalConfiguration);

      if (commands.hasMultipleCommands())
      {
        if (commands.isFirstCommand(command))
        {
          // First command - no footer
          baseTextOptions.hideFooter();
        }
        else if (commands.isLastCommand(command))
        {
          // Last command - no header, or info
          baseTextOptions.hideHeader();
          baseTextOptions.hideInfo();

          baseTextOptions.appendOutput();
        }
        else
        {
          // Middle command - no header, footer, or info
          baseTextOptions.hideHeader();
          baseTextOptions.hideInfo();
          baseTextOptions.hideFooter();

          baseTextOptions.appendOutput();
        }
      }

      final Config executableAdditionalConfig = new Config();
      if (additionalConfiguration != null)
      {
        executableAdditionalConfig.putAll(additionalConfiguration);
      }
      executableAdditionalConfig.putAll(baseTextOptions.toConfig());
      executable.setAdditionalConfiguration(executableAdditionalConfig);

    }

    executeChain(database, connection);

  }

}
