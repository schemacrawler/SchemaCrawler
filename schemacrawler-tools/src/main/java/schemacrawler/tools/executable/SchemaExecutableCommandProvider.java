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
