/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.text.diagram;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.IOUtility.isFileReadable;
import static us.fatehi.utility.IOUtility.isFileWritable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat;

abstract class AbstractGraphProcessExecutor implements GraphExecutor {

  protected final Path dotFile;
  protected final Path outputFile;
  protected final DiagramOutputFormat diagramOutputFormat;

  protected AbstractGraphProcessExecutor(
      final Path dotFile, final Path outputFile, final DiagramOutputFormat diagramOutputFormat) {
    requireNonNull(dotFile, "No DOT file provided");
    requireNonNull(outputFile, "No diagram output file provided");
    requireNonNull(diagramOutputFormat, "No diagram output format provided");

    this.dotFile = dotFile.normalize().toAbsolutePath();
    this.outputFile = outputFile.normalize().toAbsolutePath();
    this.diagramOutputFormat = diagramOutputFormat;

    if (!isFileReadable(this.dotFile)) {
      final IOException cause =
          new IOException("Cannot read DOT file <%s>".formatted(this.dotFile));
      throw new UncheckedIOException(cause);
    }

    if (!isFileWritable(this.outputFile)) {
      final IOException cause =
          new IOException("Cannot write output file <%s>".formatted(this.outputFile));
      throw new UncheckedIOException(cause);
    }
  }
}
