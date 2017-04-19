/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.integration.embeddedgraph;


import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.newBufferedReader;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static sf.util.IOUtility.copy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.regex.Pattern;

import schemacrawler.schema.Catalog;
import schemacrawler.tools.executable.BaseStagedExecutable;
import schemacrawler.tools.executable.CommandChainExecutable;
import schemacrawler.tools.integration.graph.GraphOutputFormat;
import schemacrawler.tools.options.TextOutputFormat;

public class EmbeddedGraphExecutable
  extends BaseStagedExecutable
{

  private static Pattern svgInsertionPoint = Pattern
    .compile("<h2.*Tables.*h2>");
  private static Pattern svgStart = Pattern.compile("<svg.*");

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

    chain.addNext(command, TextOutputFormat.html, baseHtmlFile);
    chain.addNext(command, GraphOutputFormat.svg, baseSvgFile);

    chain.executeOn(catalog, connection);

    // Interleave HTML and SVG
    try (
        final BufferedWriter finalHtmlFileWriter = newBufferedWriter(finalHtmlFile,
                                                                     UTF_8,
                                                                     WRITE,
                                                                     CREATE,
                                                                     TRUNCATE_EXISTING);
        final BufferedReader baseHtmlFileReader = newBufferedReader(baseHtmlFile,
                                                                    UTF_8);
        final BufferedReader baseSvgFileReader = newBufferedReader(baseSvgFile,
                                                                   UTF_8);)
    {
      String line;
      while ((line = baseHtmlFileReader.readLine()) != null)
      {
        if (svgInsertionPoint.matcher(line).matches())
        {
          insertSvg(finalHtmlFileWriter, baseSvgFileReader);
        }
        finalHtmlFileWriter.append(line).append(System.lineSeparator());
      }
    }

    try (final Writer writer = outputOptions.openNewOutputWriter();)
    {
      copy(newBufferedReader(finalHtmlFile, UTF_8), writer);
    }
  }

  private void insertSvg(final BufferedWriter finalHtmlFileWriter,
                         final BufferedReader baseSvgFileReader)
    throws IOException
  {
    finalHtmlFileWriter.append(System.lineSeparator());
    boolean skipLines = true;
    String line;
    while ((line = baseSvgFileReader.readLine()) != null)
    {
      if (skipLines)
      {
        skipLines = !svgStart.matcher(line).matches();
      }
      if (!skipLines)
      {
        finalHtmlFileWriter.append(line).append(System.lineSeparator());
      }
    }
    finalHtmlFileWriter.append(System.lineSeparator());
  }

}
