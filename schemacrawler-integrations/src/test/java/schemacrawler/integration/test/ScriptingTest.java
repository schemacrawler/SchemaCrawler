/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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

package schemacrawler.integration.test;


import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.junit.Test;

import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.integration.scripting.ScriptExecutable;
import schemacrawler.tools.options.OutputOptions;

public class ScriptingTest
  extends BaseDatabaseTest
{

  @Test
  public void executableGroovy()
    throws Exception
  {
    executeExecutableAndCheckForOutputFile(new ScriptExecutable(),
                                           "plaintextschema.groovy",
                                           "script_output.txt");
  }

  @Test
  public void executablePython()
    throws Exception
  {
    executeExecutableAndCheckForOutputFile(new ScriptExecutable(),
                                           "plaintextschema.py",
                                           "script_output.txt");
  }

  @Test
  public void executableRuby()
    throws Exception
  {
    executeExecutableAndCheckForOutputFile(new ScriptExecutable(),
                                           "plaintextschema.rb",
                                           "script_output.txt");
  }

  private void executeExecutableAndCheckForOutputFile(final Executable executable,
                                                      final String outputFormatValue,
                                                      final String referenceFileName)
    throws Exception
  {
    final File testOutputFile = File.createTempFile("schemacrawler."
                                                    + executable.getCommand()
                                                    + ".", ".test");
    testOutputFile.delete();
    final OutputOptions outputOptions = new OutputOptions(outputFormatValue,
                                                          testOutputFile);

    executable.setOutputOptions(outputOptions);
    executable.execute(getConnection());

    final List<String> failures = TestUtility.compareOutput(referenceFileName,
                                                            testOutputFile);
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

}
