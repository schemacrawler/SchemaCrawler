/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.text.diagram;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat.plain;
import static schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat.png;
import static schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat.ps;
import static schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat.svg;
import static schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat.xdot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat;
import us.fatehi.utility.IOUtility;

public class GraphvizJavaExecutorUtilityTest {

  @Test
  public void graphvizJavaSupportedFormats() throws IOException {

    final Path dotFile = Files.createTempFile("", "");
    Files.write(dotFile, "hello, world".getBytes());

    final Path outputFile = IOUtility.createTempFilePath("", "");

    for (final DiagramOutputFormat diagramOutputFormat :
        new DiagramOutputFormat[] {svg, png, ps, xdot, plain}) {
      final ExecutionRuntimeException runtimeException =
          assertThrows(
              ExecutionRuntimeException.class,
              () ->
                  GraphvizJavaExecutorUtility.generateGraph(
                      dotFile, outputFile, diagramOutputFormat));
      assertThat(
          runtimeException.getMessage(),
          containsString("Error: syntax error in line 1 near 'hello'"));
    }
  }

  @Test
  public void graphvizJavaUnsupportedFormat() throws IOException {

    final Path dotFile = Files.createTempFile("", "");
    Files.write(dotFile, "hello, world".getBytes());

    final Path outputFile = IOUtility.createTempFilePath("", "");

    final DiagramOutputFormat diagramOutputFormat = DiagramOutputFormat.cgimage;
    final Throwable runtimeException =
        assertThrows(
                ExecutionRuntimeException.class,
                () ->
                    GraphvizJavaExecutorUtility.generateGraph(
                        dotFile, outputFile, diagramOutputFormat))
            .getCause();
    assertThat(
        runtimeException.getMessage(),
        is("Unsupported output format <[cgimage] CGImage bitmap format>"));
  }
}
