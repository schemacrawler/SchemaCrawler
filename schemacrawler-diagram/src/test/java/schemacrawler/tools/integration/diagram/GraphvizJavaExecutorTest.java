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
package schemacrawler.tools.integration.diagram;


import static java.nio.file.Files.createTempFile;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static schemacrawler.test.utility.TestUtility.copyResourceToTempFile;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.SchemaCrawlerException;

public class GraphvizJavaExecutorTest
{

  @Test
  public void constructor()
    throws IOException
  {
    final Path dotFile = copyResourceToTempFile("/javaexecutor/input.dot");
    final Path outputFile = createTempFile("sc", ".dot");

    assertThrows(NullPointerException.class,
                 () -> new GraphvizJavaExecutor(null,
                                                outputFile,
                                                DiagramOutputFormat.dot));
    assertThrows(NullPointerException.class,
                 () -> new GraphvizJavaExecutor(dotFile,
                                                null,
                                                DiagramOutputFormat.dot));
    assertThrows(NullPointerException.class,
                 () -> new GraphvizJavaExecutor(dotFile, outputFile, null));
  }

  @Test
  public void canGenerate()
    throws IOException, SchemaCrawlerException
  {
    final Path dotFile = copyResourceToTempFile("/javaexecutor/input.dot");
    final Path outputFile = createTempFile("sc", ".dot");

    assertTrue(new GraphvizJavaExecutor(dotFile,
                                        outputFile,
                                        DiagramOutputFormat.png).canGenerate());
    assertFalse(new GraphvizJavaExecutor(dotFile,
                                        outputFile,
                                        DiagramOutputFormat.scdot).canGenerate());
  }

}
