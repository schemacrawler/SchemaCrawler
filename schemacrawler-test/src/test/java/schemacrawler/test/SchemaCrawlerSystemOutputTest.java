/*
 * SchemaCrawler
 * Copyright (c) 2000-2015, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
