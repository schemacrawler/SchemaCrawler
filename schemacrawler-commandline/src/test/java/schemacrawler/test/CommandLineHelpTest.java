/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static schemacrawler.test.utility.TestUtility.flattenCommandlineArgs;

import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

import schemacrawler.Main;
import schemacrawler.test.utility.BaseDatabaseTest;
import schemacrawler.test.utility.TestName;
import schemacrawler.test.utility.TestWriter;

public class CommandLineHelpTest
  extends BaseDatabaseTest
{

  private static final String COMMAND_LINE_HELP_OUTPUT = "command_line_help_output/";

  @Rule
  public TestName testName = new TestName();

  @Rule
  public final SystemOutRule systemOutRule = new SystemOutRule().enableLog()
    .mute();

  @Test
  public void commandLineHelpDefaults()
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("h", null);

    run(args, null);
  }

  private void run(final Map<String, String> argsMap,
                   final Map<String, String> config)
    throws Exception
  {

    try (final TestWriter out = new TestWriter("text");)
    {
      Main.main(flattenCommandlineArgs(argsMap));
      out.write(systemOutRule.getLog());

      out.assertEquals(COMMAND_LINE_HELP_OUTPUT + testName.currentMethodName()
                       + ".txt");
    }
  }

}
