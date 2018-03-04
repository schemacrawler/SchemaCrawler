#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};


import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;

import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.executable.CommandProvider;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.integration.freemarker.FreeMarkerRenderer;
import schemacrawler.tools.iosource.ClasspathInputResource;
import schemacrawler.tools.iosource.InputResource;
import schemacrawler.tools.iosource.StringInputResource;
import schemacrawler.tools.options.OutputOptions;

public class AdditionalCommandProvider
  implements CommandProvider
{

  @Override
  public Executable configureNewExecutable(final String command,
                                           final SchemaCrawlerOptions schemaCrawlerOptions,
                                           final OutputOptions outputOptions)
  {
    if (!AdditionalExecutable.COMMAND.equals(command)) {
      throw new IllegalArgumentException("Cannot support command, " + command);
    }
    
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
  public InputResource getHelp()
  {
    final String helpResource = "/help/AdditionalCommandProvider.txt";
    try
    {
      return new ClasspathInputResource(helpResource);
    }
    catch (final IOException e)
    {
      // Log error...
      return new StringInputResource("No help available");
    }
  }
  
  @Override
  public Collection<String> getSupportedCommands()
  {
    return Arrays.asList(AdditionalExecutable.COMMAND);
  }

  @Override
  public boolean supportsCommand(final String command,
                                 final SchemaCrawlerOptions schemaCrawlerOptions,
                                 final OutputOptions outputOptions)
  {
    return AdditionalExecutable.COMMAND.equals(command);
  }

}
