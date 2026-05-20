/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.text.embeddeddiagram;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.deleteIfExists;
import static java.nio.file.Files.newBufferedReader;
import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat.svg;
import static schemacrawler.tools.command.text.schema.options.TextOutputFormat.html;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.schemacrawler.exceptions.SchemaCrawlerException;
import schemacrawler.tools.command.AbstractSchemaCrawlerCommand;
import schemacrawler.tools.command.SchemaCrawlerCommand;
import schemacrawler.tools.command.text.diagram.DiagramRenderer;
import schemacrawler.tools.command.text.diagram.GraphExecutorFactory;
import schemacrawler.tools.command.text.diagram.options.DiagramOptions;
import schemacrawler.tools.command.text.schema.SchemaTextRenderer;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import us.fatehi.utility.property.PropertyName;

public class EmbeddedDiagramRenderer extends AbstractSchemaCrawlerCommand<DiagramOptions> {

  private static final Logger LOGGER = Logger.getLogger(EmbeddedDiagramRenderer.class.getName());

  private static final Pattern svgInsertionPoint = Pattern.compile("<h2.*Tables.*h2>");

  private final GraphExecutorFactory graphExecutorFactory;

  public EmbeddedDiagramRenderer(
      final PropertyName command, final GraphExecutorFactory graphExecutorFactory) {
    super(command);
    this.graphExecutorFactory =
        requireNonNull(graphExecutorFactory, "No graph executor factory provided");
  }

  @Override
  public void execute() {
    checkCatalog();

    try {
      final Path tempDir = Files.createTempDirectory("schemacrawler");
      final Path baseHtmlFile = tempDir.resolve("base." + html.getFormat());
      final Path baseSvgFile = tempDir.resolve("base." + svg.getFormat());

      try {
        final PropertyName commandName = getCommandName();
        executeCommand(new SchemaTextRenderer(commandName), baseHtmlFile, html);
        executeCommand(new DiagramRenderer(commandName, graphExecutorFactory), baseSvgFile, svg);

        // Interleave HTML and SVG, writing directly to the output writer
        try (final BufferedWriter writer = new BufferedWriter(outputOptions.openNewOutputWriter());
            final BufferedReader baseHtmlReader = newBufferedReader(baseHtmlFile, UTF_8)) {
          final SVGInserter svgInserter = new SVGInserter(baseSvgFile);
          boolean svgInserted = false;
          String line;
          while ((line = baseHtmlReader.readLine()) != null) {
            if (!svgInserted && svgInsertionPoint.matcher(line).matches()) {
              svgInserter.insert(writer);
              svgInserted = true;
            }
            writer.append(line).append(System.lineSeparator());
          }
          if (!svgInserted) {
            LOGGER.log(Level.WARNING, "SVG insertion point not found; embedded diagram is missing");
          }
        }
      } finally {
        try {
          deleteIfExists(baseHtmlFile);
          deleteIfExists(baseSvgFile);
          deleteIfExists(tempDir);
        } catch (final IOException e) {
          LOGGER.log(Level.WARNING, "Could not delete temp files in " + tempDir, e);
        }
      }
    } catch (final IOException e) {
      throw new UncheckedIOException("Could not create embedded diagram", e);
    } catch (final SchemaCrawlerException e) {
      throw e;
    } catch (final Exception e) {
      throw new ExecutionRuntimeException(e);
    }
  }

  @Override
  public void initialize() {
    super.initialize();
    graphExecutorFactory.canGenerate(svg);
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
    // Set information schema queries
    scCommand.setInformationSchemaViews(informationSchemaViews);

    // Initialize, and check if the command is available
    scCommand.initialize();

    // Prepare to execute
    transferState(scCommand);

    // Execute
    scCommand.execute();
  }
}
