/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.command.text.embeddeddiagram;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.newBufferedReader;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat.htmlx;
import static schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat.svg;
import static schemacrawler.tools.command.text.schema.options.TextOutputFormat.html;
import static us.fatehi.utility.IOUtility.copy;
import static us.fatehi.utility.IOUtility.createTempFilePath;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.regex.Pattern;
import static java.util.Objects.requireNonNull;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.schemacrawler.exceptions.IORuntimeException;
import schemacrawler.schemacrawler.exceptions.SchemaCrawlerException;
import schemacrawler.tools.command.text.diagram.DiagramRenderer;
import schemacrawler.tools.command.text.diagram.GraphExecutorFactory;
import schemacrawler.tools.command.text.diagram.options.DiagramOptions;
import schemacrawler.tools.command.text.schema.SchemaTextRenderer;
import schemacrawler.tools.executable.BaseSchemaCrawlerCommand;
import schemacrawler.tools.executable.SchemaCrawlerCommand;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import us.fatehi.utility.property.PropertyName;

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

  private final GraphExecutorFactory graphExecutorFactory;

  public EmbeddedDiagramRenderer(
      final PropertyName command, final GraphExecutorFactory graphExecutorFactory) {
    super(command);
    this.graphExecutorFactory =
        requireNonNull(graphExecutorFactory, "No graph executor factory provided");
  }

  @Override
  public void checkAvailability() {
    graphExecutorFactory.canGenerate(svg);
  }

  @Override
  public void execute() {
    checkCatalog();

    try {
      final String stem = "schemacrawler";
      final Path finalHtmlFile = createTempFilePath(stem, htmlx.getFormat());
      final Path baseHtmlFile = createTempFilePath(stem, html.getFormat());
      final Path baseSvgFile = createTempFilePath(stem, svg.getFormat());

      executeCommand(new SchemaTextRenderer(command), baseHtmlFile, html);
      executeCommand(new DiagramRenderer(command, graphExecutorFactory), baseSvgFile, svg);

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
    } catch (final IOException e) {
      throw new IORuntimeException("Could not create embedded diagram", e);
    } catch (final SchemaCrawlerException e) {
      throw e;
    } catch (final Exception e) {
      throw new ExecutionRuntimeException(e);
    }
  }

  @Override
  public boolean usesConnection() {
    return false;
  }

  /**
   * Lightweight execution of SchemaCrawler commands. Doing it this way avoids going via the command
   * registry and explicit loading and initialization of commands via a command provider, and
   * ability to avoid reloading the catalog, and not having to set the connection. On the other
   * hand, some of this code is duplicated from SchemaCrawlerExecuable.
   *
   * @param scCommand SchemaCrawler command to execute
   * @param outputFile Output file to create
   * @param outputFormat Output format
   * @throws Exception
   */
  private void executeCommand(
      final SchemaCrawlerCommand<? super DiagramOptions> scCommand,
      final Path outputFile,
      final OutputFormat outputFormat)
      throws Exception {

    final OutputOptions outputOptions =
        OutputOptionsBuilder.builder(getOutputOptions())
            .withOutputFormat(outputFormat)
            .withOutputFile(outputFile)
            .toOptions();

    // Normally set by the command provider during instantiation
    scCommand.configure(commandOptions);

    // Set when a new command provider is initialized
    scCommand.setSchemaCrawlerOptions(schemaCrawlerOptions);
    scCommand.setOutputOptions(outputOptions);

    // Set identifiers strategy
    scCommand.setIdentifiers(identifiers);

    // Initialize, and check if the command is available
    scCommand.initialize();
    scCommand.checkAvailability();

    // Prepare to execute
    scCommand.setCatalog(catalog);
    // Note: No need to set connection on the command

    // Execute
    scCommand.call();
  }
}
