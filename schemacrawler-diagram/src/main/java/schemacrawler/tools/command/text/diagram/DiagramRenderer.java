/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.text.diagram;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat.scdot;
import static us.fatehi.utility.IOUtility.createTempFilePath;
import static us.fatehi.utility.IOUtility.readResourceFully;

import java.io.IOException;
import java.nio.file.Path;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.schemacrawler.exceptions.IORuntimeException;
import schemacrawler.schemacrawler.exceptions.SchemaCrawlerException;
import schemacrawler.tools.command.text.diagram.options.DiagramOptions;
import schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat;
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.executable.BaseSchemaCrawlerCommand;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.tools.text.formatter.diagram.SchemaDotFormatter;
import schemacrawler.tools.traversal.SchemaTraversalHandler;
import schemacrawler.tools.traversal.SchemaTraverser;
import schemacrawler.utility.NamedObjectSort;
import us.fatehi.utility.property.PropertyName;

public final class DiagramRenderer extends BaseSchemaCrawlerCommand<DiagramOptions> {

  private DiagramOutputFormat diagramOutputFormat;
  private final GraphExecutorFactory graphExecutorFactory;

  public DiagramRenderer(
      final PropertyName command, final GraphExecutorFactory graphExecutorFactory) {
    super(command);
    this.graphExecutorFactory =
        requireNonNull(graphExecutorFactory, "No graph executor factory provided");
  }

  @Override
  public void checkAvailability() {
    graphExecutorFactory.canGenerate(diagramOutputFormat);
  }

  /** {@inheritDoc} */
  @Override
  public void execute() {
    checkCatalog();

    // Set the format, in case we are using the default
    outputOptions =
        OutputOptionsBuilder.builder(outputOptions)
            .withOutputFormat(diagramOutputFormat)
            .withOutputFormatValue(diagramOutputFormat.getFormat())
            .toOptions();

    // Create dot file
    final Path dotFile;
    try {
      dotFile = createTempFilePath("schemacrawler.", "dot");
    } catch (final IOException e) {
      throw new IORuntimeException("Could not create temporary DOT file", e);
    }
    final OutputOptions dotFileOutputOptions;
    if (diagramOutputFormat == scdot) {
      dotFileOutputOptions = outputOptions;
    } else {
      dotFileOutputOptions =
          OutputOptionsBuilder.builder(outputOptions)
              .withOutputFormat(scdot)
              .withOutputFile(dotFile)
              .toOptions();
    }

    final SchemaTraversalHandler formatter = getSchemaTraversalHandler(dotFileOutputOptions);

    final SchemaTraverser traverser = new SchemaTraverser();
    traverser.setCatalog(catalog);
    traverser.setHandler(formatter);
    traverser.setTablesComparator(
        NamedObjectSort.getNamedObjectSort(commandOptions.isAlphabeticalSortForTables()));
    traverser.setRoutinesComparator(
        NamedObjectSort.getNamedObjectSort(commandOptions.isAlphabeticalSortForRoutines()));

    traverser.traverse();

    // Set the format, in case we are using the default
    final Path outputFile = outputOptions.getOutputFile(outputOptions.getOutputFormatValue());
    outputOptions =
        OutputOptionsBuilder.builder(outputOptions)
            .withOutputFormat(diagramOutputFormat)
            .withOutputFormatValue(diagramOutputFormat.getFormat())
            .withOutputFile(outputFile)
            .toOptions();

    try {
      final GraphExecutor graphExecutor =
          graphExecutorFactory.getGraphExecutor(
              dotFile, diagramOutputFormat, outputFile, commandOptions);
      graphExecutor.run();
    } catch (final Exception e) {
      final String errorMessage = extractErrorMessage(e);
      final String helpText = readResourceFully("/dot.error.txt");
      throw new ExecutionRuntimeException("%s%n%n%s".formatted(errorMessage, helpText), e);
    }
  }

  @Override
  public void initialize() {
    super.initialize();
    diagramOutputFormat = DiagramOutputFormat.fromFormat(outputOptions.getOutputFormatValue());
  }

  @Override
  public boolean usesConnection() {
    return false;
  }

  private String extractErrorMessage(final Exception e) {
    final String errorMessage;
    final boolean isSchemaCrawlerException = e instanceof SchemaCrawlerException;
    if (isSchemaCrawlerException) {
      errorMessage = e.getMessage();
    } else {
      errorMessage = "Could not generate diagram" + e.getMessage();
    }
    return errorMessage;
  }

  private SchemaTextDetailType getSchemaTextDetailType() {
    SchemaTextDetailType schemaTextDetailType;
    try {
      schemaTextDetailType = SchemaTextDetailType.valueOf(command.getName());
    } catch (final IllegalArgumentException e) {
      schemaTextDetailType = null;
    }
    return schemaTextDetailType;
  }

  private SchemaTraversalHandler getSchemaTraversalHandler(final OutputOptions outputOptions) {
    final SchemaTextDetailType schemaTextDetailType = getSchemaTextDetailType();

    return new SchemaDotFormatter(schemaTextDetailType, commandOptions, outputOptions, identifiers);
  }
}
