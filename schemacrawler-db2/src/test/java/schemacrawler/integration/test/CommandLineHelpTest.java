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
package schemacrawler.integration.test;


import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasNoContent;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import schemacrawler.Main;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestContextParameterResolver;
import schemacrawler.test.utility.TestOutputStream;

@ExtendWith(TestContextParameterResolver.class)
public class CommandLineHelpTest
{

  private static final String COMMAND_LINE_HELP_OUTPUT =
    "command_line_help_output/";

  private TestOutputStream err;
  private TestOutputStream out;

  @AfterEach
  public void cleanUpStreams()
  {
    System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
    System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
  }

  @Test
  public void commandLineHelp(final TestContext testContext)
    throws Exception
  {
    final String server = "db2";
    Main.main("-help", "-server", server);

    assertThat(outputOf(err), hasNoContent());
    assertThat(outputOf(out),
               hasSameContentAs(classpathResource(COMMAND_LINE_HELP_OUTPUT
                                                  + server
                                                  + ".help.txt")));
  }

  @BeforeEach
  public void setUpStreams()
    throws Exception
  {
    out = new TestOutputStream();
    System.setOut(new PrintStream(out));

    err = new TestOutputStream();
    System.setErr(new PrintStream(err));
  }

}
