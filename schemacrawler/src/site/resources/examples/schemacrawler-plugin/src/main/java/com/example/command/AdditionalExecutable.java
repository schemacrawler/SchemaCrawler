package com.example.command;


import java.io.PrintWriter;
import java.sql.Connection;

import schemacrawler.schema.Column;
import schemacrawler.schema.Database;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.tools.executable.BaseExecutable;
import schemacrawler.tools.options.OutputWriter;

public class AdditionalExecutable
  extends BaseExecutable
{

  static final String COMMAND = "additional";

  protected AdditionalExecutable()
  {
    super(COMMAND);
  }

  @Override
  protected void executeOn(final Database database, final Connection connection)
    throws Exception
  {
    final PrintWriter writer = new PrintWriter(new OutputWriter(outputOptions));
    for (final Schema schema: database.getSchemas())
    {
      System.out.println(schema);
      for (final Table table: schema.getTables())
      {
        writer.println("o--> " + table);
        for (final Column column: table.getColumns())
        {
          writer.println("     o--> " + column);
        }
      }
    }
    writer.close();
  }

}
