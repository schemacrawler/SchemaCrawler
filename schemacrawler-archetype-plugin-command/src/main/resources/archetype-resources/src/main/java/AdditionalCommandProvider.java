#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};


import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.executable.CommandProvider;
import schemacrawler.tools.executable.SchemaCrawlerCommand;
import schemacrawler.tools.iosource.ClasspathInputResource;
import schemacrawler.tools.iosource.InputResource;
import schemacrawler.tools.iosource.StringInputResource;
import schemacrawler.tools.options.OutputOptions;

/**
 * SchemaCrawler command plug-in.
 * 
 * @see <a href="https://www.schemacrawler.com">SchemaCrawler</a>
 * 
 * @author Automatically generated by SchemaCrawler 15.03.02
 */
public class AdditionalCommandProvider
  implements CommandProvider
{

  @Override
  public InputResource getHelp()
  {
    final String helpResource = "/help/AdditionalCommandProvider.txt";
    try
    {
      return new ClasspathInputResource(helpResource);
    }
    catch (final IOException e)
    {
      return new StringInputResource("No help available");
    }
  }

  @Override
  public Collection<String> getSupportedCommands()
  {
    return Arrays.asList(AdditionalCommand.COMMAND);
  }

  @Override
  public SchemaCrawlerCommand newSchemaCrawlerCommand(final String command)
  {
    if (!AdditionalCommand.COMMAND.equals(command))
    {
      throw new IllegalArgumentException("Cannot support command, " + command);
    }
    final AdditionalCommand scCommand = new AdditionalCommand();
    return scCommand;
  }

  @Override
  public boolean supportsSchemaCrawlerCommand(final String command,
                                              final SchemaCrawlerOptions schemaCrawlerOptions,
                                              final OutputOptions outputOptions)
  {
    return AdditionalCommand.COMMAND.equals(command);
  }
    
}
