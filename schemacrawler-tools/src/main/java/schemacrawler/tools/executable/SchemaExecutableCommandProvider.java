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
package schemacrawler.tools.executable;


import static java.util.Objects.requireNonNull;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.integration.embeddedgraph.EmbeddedGraphExecutable;
import schemacrawler.tools.integration.graph.GraphExecutable;
import schemacrawler.tools.integration.graph.GraphOutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import schemacrawler.tools.text.schema.SchemaTextExecutable;

class SchemaExecutableCommandProvider
  extends ExecutableCommandProvider
{

  public SchemaExecutableCommandProvider(final SchemaTextDetailType schemaTextDetailType)
  {
    super(requireNonNull(schemaTextDetailType, "No schema text detail provided")
      .name(), "");
  }

  @Override
  public Executable configureNewExecutable(final SchemaCrawlerOptions schemaCrawlerOptions,
                                           final OutputOptions outputOptions)
                                             throws SchemaCrawlerException
  {
    final boolean isGraph;
    final boolean isEmbeddedGraph;
    if (outputOptions != null)
    {
      final String outputFormatValue = outputOptions.getOutputFormatValue();
      isGraph = GraphOutputFormat.isGraphOutputFormat(outputFormatValue);
      isEmbeddedGraph = GraphOutputFormat.htmlx.getFormat()
        .equalsIgnoreCase(outputFormatValue);
    }
    else
    {
      isGraph = false;
      isEmbeddedGraph = false;
    }

    // Create and configure executable
    final Executable executable;
    if (isEmbeddedGraph)
    {
      executable = new EmbeddedGraphExecutable(getCommand());
    }
    else if (isGraph)
    {
      executable = new GraphExecutable(getCommand());
    }
    else
    {
      executable = new SchemaTextExecutable(getCommand());
    }

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
}
