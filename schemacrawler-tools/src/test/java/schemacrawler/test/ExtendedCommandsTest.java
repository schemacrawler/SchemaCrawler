/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2014, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.test;


import static schemacrawler.test.utility.TestUtility.createTempFile;
import static schemacrawler.test.utility.TestUtility.validateDiagram;

import java.nio.file.Path;

import org.junit.Test;

import schemacrawler.test.utility.BaseExecutableTest;
import schemacrawler.tools.integration.graph.GraphExecutable;
import schemacrawler.tools.integration.graph.GraphOutputFormat;
import schemacrawler.tools.integration.scripting.ScriptExecutable;
import schemacrawler.tools.options.OutputOptions;

public class ExtendedCommandsTest
  extends BaseExecutableTest
{

  @Test
  public void executableGraph()
    throws Exception
  {
    final GraphExecutable executable = new GraphExecutable();

    final Path testOutputFile = createTempFile(executable.getCommand(), "png");

    final OutputOptions outputOptions = new OutputOptions(GraphOutputFormat.png,
                                                          testOutputFile);

    executable.setOutputOptions(outputOptions);
    executable.execute(getConnection());

    validateDiagram(testOutputFile);
  }

  @Test
  public void executableGraphDot()
    throws Exception
  {
    executeExecutableAndCheckForOutputFile(new GraphExecutable(),
                                           "canon",
                                           "executableForGraph.txt");
  }

  @Test
  public void executableJavaScript()
    throws Exception
  {
    executeExecutableAndCheckForOutputFile(new ScriptExecutable(),
                                           "plaintextschema.js",
                                           "executableForJavaScript.txt");
  }

}
