/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static schemacrawler.test.utility.TestUtility.compareOutput;
import static schemacrawler.test.utility.TestUtility.validateDiagram;
import static sf.util.IOUtility.readFully;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.integration.scripting.ScriptExecutable;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.TextOutputFormat;
import sf.util.IOUtility;

public class SchemaCrawlerExecutableChainTest
  extends BaseDatabaseTest
{

  @Test
  public void chainJavaScript()
    throws Exception
  {
    final Executable executable = new ScriptExecutable();
    final Path testOutputFile = IOUtility
      .createTempFilePath(executable.getCommand(), "data");

    final OutputOptions outputOptions = new OutputOptions("/chain.js",
                                                          testOutputFile);

    executable.setOutputOptions(outputOptions);
    executable.execute(getConnection());

    assertEquals("Created files \"schema.txt\" and \"schema.png\""
                 + System.lineSeparator(),
                 readFully(new FileReader(testOutputFile.toFile())));

    final List<String> failures = compareOutput("schema.txt",
                                                Paths.get("schema.txt"),
                                                TextOutputFormat.text.name());
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }

    final Path diagramFile = Paths.get("schema.png");
    validateDiagram(diagramFile);
    Files.deleteIfExists(diagramFile);
  }

}
