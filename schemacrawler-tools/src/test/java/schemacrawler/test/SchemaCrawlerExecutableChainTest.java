/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileReader;
import java.util.List;

import org.junit.Test;

import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.integration.scripting.ScriptExecutable;
import schemacrawler.tools.options.OutputOptions;
import sf.util.Utility;

public class SchemaCrawlerExecutableChainTest
  extends BaseDatabaseTest
{

  @Test
  public void chainJavaScript()
    throws Exception
  {
    final Executable executable = new ScriptExecutable();
    final File testOutputFile = File.createTempFile("schemacrawler."
                                                    + executable.getCommand()
                                                    + ".", ".test");
    testOutputFile.delete();
    final OutputOptions outputOptions = new OutputOptions("chain.js",
                                                          testOutputFile);

    executable.setOutputOptions(outputOptions);
    executable.execute(getConnection());

    assertEquals("Created files \"schema.txt\" and \"schema.png\"",
                 Utility.readFully(new FileReader(testOutputFile)));

    final List<String> failures = TestUtility
      .compareOutput("schema.txt", new File("schema.txt"));
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }

    checkDiagramFile(new File("schema.png"));
  }

}
