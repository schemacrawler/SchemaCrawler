/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.command.text.diagram;

import static java.nio.file.Files.createTempFile;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.copyResourceToTempFile;
import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat;

public class GraphvizJavaExecutorTest {

  @Test
  public void canGenerate() throws IOException {
    final Path dotFile = copyResourceToTempFile("/javaexecutor/input.dot");
    final Path outputFile = createTempFile("sc", ".dot");

    assertThat(
        new GraphvizJavaExecutor(dotFile, outputFile, DiagramOutputFormat.png).canGenerate(),
        is(true));
    assertThat(
        new GraphvizJavaExecutor(dotFile, outputFile, DiagramOutputFormat.scdot).canGenerate(),
        is(false));
  }

  @Test
  public void constructor() throws IOException {
    final Path dotFile = copyResourceToTempFile("/javaexecutor/input.dot");
    final Path outputFile = createTempFile("sc", ".dot");

    assertThrows(
        NullPointerException.class,
        () -> new GraphvizJavaExecutor(null, outputFile, DiagramOutputFormat.dot));
    assertThrows(
        NullPointerException.class,
        () -> new GraphvizJavaExecutor(dotFile, null, DiagramOutputFormat.dot));
    assertThrows(
        NullPointerException.class, () -> new GraphvizJavaExecutor(dotFile, outputFile, null));
  }

  @Test
  public void generate() throws IOException {
    final Path dotFile = copyResourceToTempFile("/javaexecutor/input.dot");
    final Path outputFile = createTempFile("sc", ".dot");

    final GraphvizJavaExecutor graphvizJavaExecutor =
        new GraphvizJavaExecutor(dotFile, outputFile, DiagramOutputFormat.xdot);
    assertThat(graphvizJavaExecutor.canGenerate(), is(true));

    graphvizJavaExecutor.run();

    assertThat(
        outputOf(outputFile), hasSameContentAs(classpathResource("/javaexecutor/output.xdot")));
  }
}
