package schemacrawler.tools.lint.executable;


import schemacrawler.tools.executable.CommandProvider;
import schemacrawler.tools.executable.Executable;

public class LintCommandProvider
  implements CommandProvider
{

  @Override
  public String getCommand()
  {
    return LintExecutable.COMMAND;
  }

  @Override
  public String getHelpResource()
  {
    return "/help/LintCommandProvider.txt";
  }

  @Override
  public Executable newExecutable()
  {
    return new LintExecutable();
  }

}
