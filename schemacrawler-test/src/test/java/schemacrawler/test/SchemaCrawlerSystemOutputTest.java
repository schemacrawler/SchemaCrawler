/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi.
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


import static org.junit.Assert.fail;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.TextOutputFormat;
import sf.util.ObjectToString;

public class SchemaCrawlerSystemOutputTest
  extends AbstractSchemaCrawlerSystemTest
{

  private static final String COMMAND_OUTPUT = "results/";

  @Test
  public void outputs()
    throws Exception
  {
    final List<String> messages = new ArrayList<>();
    String message;
    message = output("MicrosoftSQLServer", "Books.dbo");
    if (message != null)
    {
      messages.add(message);
    }

    message = output("Oracle", "BOOKS");
    if (message != null)
    {
      messages.add(message);
    }

    message = output("IBM_DB2", "BOOKS");
    if (message != null)
    {
      messages.add(message);
    }

    message = output("MySQL", null);
    if (message != null)
    {
      messages.add(message);
    }

    message = output("PostgreSQL", "books");
    if (message != null)
    {
      messages.add(message);
    }

    message = output("Derby", "BOOKS");
    if (message != null)
    {
      messages.add(message);
    }

    if (!messages.isEmpty())
    {
      final String error = ObjectToString.toString(messages);
      System.out.println(error);
      fail(error);
    }
  }

  private String output(final String dataSourceName,
                        final String schemaInclusion)
    throws Exception
  {
    try (final TestWriter out = new TestWriter("text");)
    {
      final Connection connection = connect(dataSourceName);

      final SchemaCrawlerOptions schemaCrawlerOptions = createOptions(dataSourceName,
                                                                      schemaInclusion);

      final OutputOptions outputOptions = new OutputOptions(TextOutputFormat.text,
                                                            out);

      final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable("details");
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable.setOutputOptions(outputOptions);
      executable.execute(connection);

      out.assertEquals(COMMAND_OUTPUT + dataSourceName + ".txt");
    }

    return null;
  }

}
