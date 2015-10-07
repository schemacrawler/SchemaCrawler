package schemacrawler.tools.executable;


import java.sql.Connection;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.text.base.CommonTextOptionsBuilder;

/**
 * Allows chaining multiple executables together, that produce shared
 * artifacts, such as a single HTML file.
 */
public final class CommandDaisyChainExecutable
  extends BaseCommandChainExecutable
{

  public CommandDaisyChainExecutable(final String commands)
    throws SchemaCrawlerException
  {
    super(commands);
  }

  @Override
  public void executeOn(final Catalog catalog, final Connection connection)
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

      final CommonTextOptionsBuilder commonTextOptions = new CommonTextOptionsBuilder();
      commonTextOptions.fromConfig(additionalConfiguration);

      if (commands.hasMultipleCommands())
      {
        if (commands.isFirstCommand(command))
        {
          // First command - no footer
          commonTextOptions.hideFooter();
        }
        else if (commands.isLastCommand(command))
        {
          // Last command - no header, or info
          commonTextOptions.hideHeader();
          commonTextOptions.hideInfo();

          commonTextOptions.appendOutput();
        }
        else
        {
          // Middle command - no header, footer, or info
          commonTextOptions.hideHeader();
          commonTextOptions.hideInfo();
          commonTextOptions.hideFooter();

          commonTextOptions.appendOutput();
        }
      }

      final Config executableAdditionalConfig = new Config();
      if (additionalConfiguration != null)
      {
        executableAdditionalConfig.putAll(additionalConfiguration);
      }
      executableAdditionalConfig.putAll(commonTextOptions.toConfig());
      executable.setAdditionalConfiguration(executableAdditionalConfig);

    }

    executeChain(catalog, connection);

  }

  private final Executable addNext(final String command)
    throws SchemaCrawlerException
  {
    try
    {
      final Executable executable = commandRegistry
        .configureNewExecutable(command, schemaCrawlerOptions, outputOptions);
      if (executable == null)
      {
        return executable;
      }

      executable.setAdditionalConfiguration(additionalConfiguration);

      return addNext(executable);
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException(String
        .format("Cannot chain executable, unknown command, %s", command));
    }
  }

}
