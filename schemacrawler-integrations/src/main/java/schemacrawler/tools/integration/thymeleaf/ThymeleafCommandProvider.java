/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi.
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
package schemacrawler.tools.integration.thymeleaf;


import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.executable.CommandProvider;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.options.OutputOptions;

public class ThymeleafCommandProvider
  implements CommandProvider
{

  @Override
  public Executable configureNewExecutable(final SchemaCrawlerOptions schemaCrawlerOptions,
                                           final OutputOptions outputOptions)
  {
    final ThymeleafRenderer executable = new ThymeleafRenderer();
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
    return ThymeleafRenderer.COMMAND;
  }

  @Override
  public String getHelpResource()
  {
    return "/help/ThymeleafRenderer.txt";
  }

}
