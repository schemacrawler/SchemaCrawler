package schemacrawler.tools.integration.embeddedgraph;


import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.newBufferedReader;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;
import static sf.util.Utility.NEWLINE;
import static sf.util.Utility.UTF8;
import static sf.util.Utility.copy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.sql.Connection;

import schemacrawler.schema.Catalog;
import schemacrawler.tools.executable.BaseStagedExecutable;
import schemacrawler.tools.executable.CommandChainExecutable;
import schemacrawler.tools.integration.graph.GraphOutputFormat;
import schemacrawler.tools.options.OutputWriter;
import schemacrawler.tools.options.TextOutputFormat;

public class EmbeddedGraphExecutable
  extends BaseStagedExecutable
{

  public EmbeddedGraphExecutable(final String command)
  {
    super(command);
  }

  @Override
  public void executeOn(final Catalog catalog, final Connection connection)
    throws Exception
  {
    final Path finalHtmlFile = createTempFile("schemacrawler", ".html");
    final Path baseHtmlFile = createTempFile("schemacrawler", ".html");
    final Path baseSvgFile = createTempFile("schemacrawler", ".svg");

    final CommandChainExecutable chain = new CommandChainExecutable();
    chain.setSchemaCrawlerOptions(schemaCrawlerOptions);
    chain.setAdditionalConfiguration(additionalConfiguration);

    chain.addNext(command, TextOutputFormat.html, baseHtmlFile.toFile());
    chain.addNext(command, GraphOutputFormat.svg, baseSvgFile.toFile());

    chain.executeOn(catalog, connection);

    // Interleave HTML and SVG
    try (final BufferedWriter finalHtmlFileWriter = newBufferedWriter(finalHtmlFile,
                                                                      UTF8,
                                                                      CREATE,
                                                                      WRITE);
        final BufferedReader baseHtmlFileReader = newBufferedReader(baseHtmlFile,
                                                                    UTF8);
        final BufferedReader baseSvgFileReader = newBufferedReader(baseSvgFile,
                                                                   Charset
                                                                     .defaultCharset());)
    {
      String line;
      while ((line = baseHtmlFileReader.readLine()) != null)
      {
        if (line.matches(".*h2.*Tables.*h2.*"))
        {
          finalHtmlFileWriter.append(NEWLINE);
          copy(baseSvgFileReader, finalHtmlFileWriter);
          finalHtmlFileWriter.append(NEWLINE);
        }
        finalHtmlFileWriter.append(line).append(NEWLINE);
      }
    }

    try (final OutputWriter writer = new OutputWriter(outputOptions);)
    {
      copy(newBufferedReader(finalHtmlFile, UTF8), writer);
    }
  }

}
