/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package schemacrawler.tools.lint.executable;


import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;

import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.executable.CommandProvider;
import schemacrawler.tools.executable.SchemaCrawlerCommand;
import schemacrawler.tools.iosource.ClasspathInputResource;
import schemacrawler.tools.iosource.EmptyInputResource;
import schemacrawler.tools.iosource.FileInputResource;
import schemacrawler.tools.iosource.InputResource;
import schemacrawler.tools.lint.LinterHelp;
import schemacrawler.tools.options.OutputOptions;
import sf.util.IOUtility;
import sf.util.SchemaCrawlerLogger;

public class LintCommandProvider
  implements CommandProvider
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(LintCommandProvider.class.getName());

  @Override
  public InputResource getHelp()
  {
    try
    {
      final Path tempFilePath = IOUtility.createTempFilePath("sc_lint_help",
                                                             ".txt");
      try (Writer writer = Files.newBufferedWriter(tempFilePath,
                                                   StandardCharsets.UTF_8,
                                                   StandardOpenOption.WRITE,
                                                   StandardOpenOption.APPEND))
      {
        final InputResource helpResource = new ClasspathInputResource("/help/LintCommandProvider.txt");
        try (Reader helpReader = helpResource
          .openNewInputReader(StandardCharsets.UTF_8);)
        {
          IOUtility.copy(helpReader, writer);
        }
        try (Reader additionalHelpReader = new StringReader(LinterHelp
          .getLinterHelpText());)
        {
          IOUtility.copy(additionalHelpReader, writer);
        }
      }
      return new FileInputResource(tempFilePath);
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could not generate lint command help", e);
      return new EmptyInputResource();
    }
  }

  @Override
  public Collection<String> getSupportedCommands()
  {
    return Arrays.asList(LintCommand.COMMAND);
  }

  @Override
  public SchemaCrawlerCommand newSchemaCrawlerCommand(final String command)
  {
    final LintCommand scCommand = new LintCommand();
    return scCommand;
  }

  @Override
  public boolean supportsSchemaCrawlerCommand(final String command,
                                              final SchemaCrawlerOptions schemaCrawlerOptions,
                                              final OutputOptions outputOptions)
  {
    return LintCommand.COMMAND.equals(command);
  }

}
