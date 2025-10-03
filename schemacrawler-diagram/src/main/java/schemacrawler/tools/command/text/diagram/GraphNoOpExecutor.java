/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.text.diagram;

import static java.util.Objects.requireNonNull;

import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat;

final class GraphNoOpExecutor implements GraphExecutor {

  GraphNoOpExecutor(final DiagramOutputFormat diagramOutputFormat) {
    requireNonNull(diagramOutputFormat, "No diagram output format provided");
    if (diagramOutputFormat != DiagramOutputFormat.scdot) {
      throw new ExecutionRuntimeException(
          "Format should be <%s>".formatted(DiagramOutputFormat.scdot));
    }
  }

  @Override
  public boolean canGenerate() {
    return true;
  }

  @Override
  public void run() {
    // No-op
  }
}
