package schemacrawler.tools.integration.graph;


import java.nio.file.Path;
import java.util.List;

import schemacrawler.schemacrawler.SchemaCrawlerException;

interface GraphExecutor
{

  boolean canGenerate(GraphOutputFormat format);

  int generate(final Path dotFile,
               final Path outputFile,
               final List<String> graphvizOpts,
               final GraphOutputFormat graphOutputFormat)
    throws SchemaCrawlerException;

}
