#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};


import java.io.PrintWriter;
import java.sql.Connection;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.tools.executable.BaseStagedExecutable;
import schemacrawler.tools.options.OutputWriter;

public class AdditionalExecutable
  extends BaseStagedExecutable
{

  static final String COMMAND = "additional";

  protected AdditionalExecutable()
  {
    super(COMMAND);
  }

  @Override
  public void executeOn(final Catalog catalog, final Connection connection)
    throws Exception
  {
    try (final PrintWriter writer = new PrintWriter(new OutputWriter(outputOptions));)
    {
      for (final Schema schema: catalog.getSchemas())
      {
        System.out.println(schema);
        for (final Table table: catalog.getTables(schema))
        {
          writer.println("o--> " + table);
          for (final Column column: table.getColumns())
          {
            writer.println("     o--> " + column);
          }
        }
      }
    }
  }

}
