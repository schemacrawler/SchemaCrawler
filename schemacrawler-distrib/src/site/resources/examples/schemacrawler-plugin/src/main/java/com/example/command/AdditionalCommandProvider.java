package com.example.command;


import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.executable.CommandProvider;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.options.OutputOptions;

public class AdditionalCommandProvider
  implements CommandProvider
{

  @Override
  public Executable configureNewExecutable(final SchemaCrawlerOptions schemaCrawlerOptions,
                                           final OutputOptions outputOptions)
  {
    final AdditionalExecutable executable = new AdditionalExecutable();
    if (schemaCrawlerOptions != null)
    {
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    }
    if (outputOptions != null)
    {
      executable.setOutputOptions(outputOptions);
    }
    return executable;
  }

  @Override
  public String getCommand()
  {
    return AdditionalExecutable.COMMAND;
  }

  @Override
  public String getHelpResource()
  {
    return "/help/AdditionalCommandProvider.txt";
  }

}
