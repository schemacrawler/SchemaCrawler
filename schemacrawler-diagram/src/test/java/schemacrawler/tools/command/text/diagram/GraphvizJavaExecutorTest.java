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

package schemacrawler.tools.command.text.diagram;

import static java.nio.file.Files.createTempFile;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAsClasspathResource;
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
        outputOf(outputFile), hasSameContentAsClasspathResource("/javaexecutor/output.xdot"));
  }
}
