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

package schemacrawler.test.utility;


import static org.junit.Assert.fail;
import static schemacrawler.test.utility.TestUtility.compareOutput;
import static schemacrawler.test.utility.TestUtility.createTempFile;

import java.nio.file.Path;
import java.util.List;

import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.options.OutputOptions;

public abstract class BaseExecutableTest
  extends BaseDatabaseTest
{

  protected Path executeExecutable(final Executable executable,
                                   final String outputFormatValue)
    throws Exception
  {
    final Path testOutputFile = createTempFile(executable.getCommand(),
                                               outputFormatValue);

    final OutputOptions outputOptions = new OutputOptions(outputFormatValue,
                                                          testOutputFile);

    executable.setOutputOptions(outputOptions);
    executable.execute(getConnection());

    return testOutputFile;
  }

  protected void executeExecutableAndCheckForOutputFile(final Executable executable,
                                                        final String outputFormatValue,
                                                        final String referenceFileName)
    throws Exception
  {
    final Path testOutputFile = executeExecutable(executable, outputFormatValue);

    final List<String> failures = compareOutput(referenceFileName,
                                                testOutputFile);
    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

}
