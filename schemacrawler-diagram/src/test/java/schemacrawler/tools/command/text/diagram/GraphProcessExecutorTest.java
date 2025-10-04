/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.text.diagram;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.test.utility.FileHasContent.contentsOf;
import static schemacrawler.test.utility.FileHasContent.hasNoContent;
import static schemacrawler.test.utility.FileHasContent.outputOf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.exceptions.IORuntimeException;
import schemacrawler.test.utility.CaptureSystemStreams;
import schemacrawler.test.utility.CapturedSystemStreams;
import schemacrawler.test.utility.OnlyRunWithGraphviz;
import schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat;
import us.fatehi.utility.IOUtility;

@CaptureSystemStreams
public class GraphProcessExecutorTest {

  private final class TestGraphProcessExecutor extends AbstractGraphProcessExecutor {
    private TestGraphProcessExecutor(
        final Path dotFile, final Path outputFile, final DiagramOutputFormat diagramOutputFormat) {
      super(dotFile, outputFile, diagramOutputFormat);
    }

    @Override
    public boolean canGenerate() {
      return false;
    }

    @Override
    public void run() {}
  }

  @Test
  public void constructorGraphProcessExecutor() throws IOException {

    final Path dotFile = Files.createTempFile("", "");
    Files.write(dotFile, "hello, world".getBytes());

    final Path outputFile = IOUtility.createTempFilePath("", "");

    final DiagramOutputFormat diagramOutputFormat = DiagramOutputFormat.png;

    // Assert no errors
    assertThat(
        new TestGraphProcessExecutor(dotFile, outputFile, diagramOutputFormat),
        is(not(nullValue())));

    // DOT file not readable
    final IORuntimeException exception1 =
        assertThrows(
            IORuntimeException.class,
            () ->
                new TestGraphProcessExecutor(
                    IOUtility.createTempFilePath("sc", "data"), outputFile, diagramOutputFormat));
    assertThat(exception1.getMessage(), containsString("Cannot read DOT file"));

    // Output file not writable
    final IORuntimeException exception2 =
        assertThrows(
            IORuntimeException.class,
            () ->
                new TestGraphProcessExecutor(
                    dotFile, Path.of("/not_a_directory/unwritable_file.dat"), diagramOutputFormat));
    assertThat(exception2.getMessage(), containsString("Cannot write output file"));
  }

  @Test
  @OnlyRunWithGraphviz
  public void graphvizProcessExecutorError(final CapturedSystemStreams streams) throws IOException {

    final Path dotFile = Files.createTempFile("", "");
    Files.write(dotFile, "hello, world".getBytes());

    final Path outputFile = IOUtility.createTempFilePath("", "");

    final DiagramOutputFormat diagramOutputFormat = DiagramOutputFormat.png;

    // Graphviz error due to bad input
    final GraphvizProcessExecutor processExecutor =
        new GraphvizProcessExecutor(
            dotFile, outputFile, diagramOutputFormat, Collections.emptyList());
    processExecutor.run();

    assertThat(contentsOf(streams.err()), containsString("syntax error in line 1 near 'hello'"));
    assertThat(outputOf(streams.out()), hasNoContent());
  }
}
