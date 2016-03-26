/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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


import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.executable.CommandProvider;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.lint.LinterHelp;
import schemacrawler.tools.options.OutputOptions;

public class LintCommandProvider
  implements CommandProvider
{

  private static final Logger LOGGER = Logger
    .getLogger(LintCommandProvider.class.getName());

  @Override
  public Executable configureNewExecutable(final SchemaCrawlerOptions schemaCrawlerOptions,
                                           final OutputOptions outputOptions)
  {
    final LintExecutable executable = new LintExecutable();
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
    return LintExecutable.COMMAND;
  }

  @Override
  public String getHelpAdditionalText()
  {
    try
    {
      return LinterHelp.getLinterHelpText();
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.FINE, "Cannot get linter help", e);
      return "";
    }
  }

  @Override
  public String getHelpResource()
  {
    return "/help/LintCommandProvider.txt";
  }

}
