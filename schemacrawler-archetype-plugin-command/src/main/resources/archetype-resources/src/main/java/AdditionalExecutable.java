#set($symbol_pound='#')#set($symbol_dollar='$')#set($symbol_escape='\')package ${package};

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.tools.executable.BaseStagedExecutable;
import sf.util.StringFormat;

public class AdditionalExecutable
  extends BaseStagedExecutable
{

  private static final Logger LOGGER = Logger
    .getLogger(AdditionalExecutable.class.getName());

  static final String COMMAND = "additional";

  protected AdditionalExecutable()
  {
    super(COMMAND);
  }

  @Override
  public void executeOn(final Catalog catalog, final Connection connection)
    throws Exception
  {
    // TODO: Possibly process command-line options, which are available
    // in
    // additionalConfiguration

    try (final PrintWriter writer = new PrintWriter(outputOptions
      .openNewOutputWriter());)
    {
      for (final Schema schema: catalog.getSchemas())
      {
        // SchemaCrawler will control output of log messages if you use
        // JDK logging
        LOGGER.log(Level.INFO,
                   new StringFormat("Processing, %s", schema));
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
