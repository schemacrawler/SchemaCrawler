/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.commandline;


import static us.fatehi.commandlineparser.CommandLineUtility.newCommandLine;

import java.util.Objects;

import picocli.CommandLine;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;

/**
 * Parses the command-line.
 *
 * @author Sualeh Fatehi
 */
public final class ShowOptionsParser
  implements OptionsParser<SchemaTextOptions>
{

  private final CommandLine commandLine;
  private final SchemaTextOptionsBuilder optionsBuilder;

  @CommandLine.Option(names = { "--no-info" }, description = "Whether to show database information")
  private Boolean noinfo;
  @CommandLine.Option(names = { "--no-remarks" }, description = "Whether to sort remarks")
  private Boolean noremarks;
  @CommandLine.Option(names = { "--weak-associations" }, description = "Whether to weak associations")
  private Boolean weakassociations;
  @CommandLine.Option(names = { "--portable-names" }, description = "Whether to use portable names")
  private Boolean portablenames;

  @CommandLine.Parameters
  private String[] remainder = new String[0];

  public ShowOptionsParser(final SchemaTextOptionsBuilder optionsBuilder)
  {
    commandLine = newCommandLine(this);
    this.optionsBuilder = Objects.requireNonNull(optionsBuilder);
  }

  @Override
  public SchemaTextOptions parse(final String[] args)
  {
    commandLine.parse(args);

    if (noinfo != null)
    {
      optionsBuilder.noInfo(noinfo);
    }
    if (noremarks != null)
    {
      optionsBuilder.noRemarks(noremarks);
    }
    if (weakassociations != null)
    {
      optionsBuilder.weakAssociations(weakassociations);
    }
    if (portablenames != null)
    {
      optionsBuilder.portableNames(portablenames);
    }

    return null;
  }

  @Override
  public String[] getRemainder()
  {
    return remainder;
  }

}
