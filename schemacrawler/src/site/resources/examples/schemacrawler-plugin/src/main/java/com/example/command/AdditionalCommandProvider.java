package com.example.command;


import schemacrawler.tools.executable.CommandProvider;
import schemacrawler.tools.executable.Executable;

public class AdditionalCommandProvider
  implements CommandProvider
{

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

  @Override
  public Executable newExecutable()
  {
    return new AdditionalExecutable();
  }

}
