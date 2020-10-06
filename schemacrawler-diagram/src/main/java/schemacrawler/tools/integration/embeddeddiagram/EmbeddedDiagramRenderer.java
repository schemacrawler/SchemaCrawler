/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.integration.embeddeddiagram;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.newBufferedReader;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static schemacrawler.tools.integration.diagram.DiagramOutputFormat.svg;
import static schemacrawler.tools.integration.diagram.GraphvizUtility.isGraphvizAvailable;
import static schemacrawler.tools.integration.diagram.GraphvizUtility.isGraphvizJavaAvailable;
import static us.fatehi.utility.IOUtility.copy;
import static us.fatehi.utility.IOUtility.createTempFilePath;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.regex.Pattern;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.executable.BaseSchemaCrawlerCommand;
import schemacrawler.tools.executable.SchemaCrawlerCommand;
import schemacrawler.tools.integration.diagram.DiagramOptions;
import schemacrawler.tools.integration.diagram.DiagramOutputFormat;
import schemacrawler.tools.integration.diagram.DiagramRenderer;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.text.schema.SchemaTextRenderer;

public class EmbeddedDiagramRenderer extends BaseSchemaCrawlerCommand<DiagramOptions> {

  private static final Pattern svgInsertionPoint = Pattern.compile("<h2.*Tables.*h2>");
  private static final Pattern svgStart = Pattern.compile("<svg.*");

  private static void insertSvg(
      final BufferedWriter finalHtmlFileWriter, final BufferedReader baseSvgFileReader)
      throws IOException {
    finalHtmlFileWriter.append(System.lineSeparator());
    boolean skipLines = true;
    boolean isSvgStart = false;
    String line;
    while ((line = baseSvgFileReader.readLine()) != null) {
      if (skipLines) {
        isSvgStart = svgStart.matcher(line).matches();
        skipLines = !isSvgStart;
      }
      if (!skipLines) {
        if (isSvgStart) {
          line = "<svg";
          isSvgStart = false;
        }
        finalHtmlFileWriter.append(line).append(System.lineSeparator());
      }
    }
    finalHtmlFileWriter.append(System.lineSeparator());
  }

  public EmbeddedDiagramRenderer(final String command) {
    super(command);
  }

  @Override
  public void checkAvailability() throws Exception {
    if (isGraphvizAvailable()) {
      return;
    } else if (isGraphvizJavaAvailable(svg)) {
      return;
    } else {
      throw new SchemaCrawlerException("Cannot generate diagram in SVG format");
    }
  }

  @Override
  public void execute() throws Exception {
    checkCatalog();

    final Path finalHtmlFile = createTempFilePath("schemacrawler", "html");
    final Path baseHtmlFile = createTempFilePath("schemacrawler", "html");
    final Path baseSvgFile = createTempFilePath("schemacrawler", "svg");

    executeCommand(new SchemaTextRenderer(command), baseHtmlFile, TextOutputFormat.html);
    executeCommand(new DiagramRenderer(command), baseSvgFile, DiagramOutputFormat.svg);

    // Interleave HTML and SVG
    try (final BufferedWriter finalHtmlFileWriter =
            newBufferedWriter(finalHtmlFile, UTF_8, WRITE, CREATE, TRUNCATE_EXISTING);
        final BufferedReader baseHtmlFileReader = newBufferedReader(baseHtmlFile, UTF_8);
        final BufferedReader baseSvgFileReader = newBufferedReader(baseSvgFile, UTF_8)) {
      String line;
      while ((line = baseHtmlFileReader.readLine()) != null) {
        if (svgInsertionPoint.matcher(line).matches()) {
          insertSvg(finalHtmlFileWriter, baseSvgFileReader);
        }
        finalHtmlFileWriter.append(line).append(System.lineSeparator());
      }
    }

    try (final Writer writer = outputOptions.openNewOutputWriter()) {
      copy(newBufferedReader(finalHtmlFile, UTF_8), writer);
    }
  }

  @Override
  public boolean usesConnection() {
    return false;
  }

  private void executeCommand(
      final SchemaCrawlerCommand<? super DiagramOptions> scCommand,
      final Path outputFile,
      final OutputFormat outputFormat)
      throws Exception {
    scCommand.setSchemaCrawlerOptions(getSchemaCrawlerOptions());
    scCommand.setCommandOptions(commandOptions);

    scCommand.setIdentifiers(getIdentifiers());
    scCommand.setCatalog(getCatalog());
    scCommand.setConnection(getConnection());

    final OutputOptions outputOptions =
        OutputOptionsBuilder.builder(getOutputOptions())
            .withOutputFormat(outputFormat)
            .withOutputFile(outputFile)
            .toOptions();
    scCommand.setOutputOptions(outputOptions);

    scCommand.checkAvailability();
    scCommand.initialize();
    scCommand.execute();
  }
}
