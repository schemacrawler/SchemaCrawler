package schemacrawler.tools.text.base;


import java.io.PrintWriter;

import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.SchemaCrawlerInfo;
import schemacrawler.schemacrawler.Options;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.util.HtmlFormattingHelper;
import schemacrawler.tools.text.util.PlainTextFormattingHelper;
import schemacrawler.tools.text.util.TextFormattingHelper;

public abstract class BaseFormatter<O extends Options>
{

  protected final O options;
  protected final OutputOptions outputOptions;
  protected final PrintWriter out;
  protected final TextFormattingHelper formattingHelper;
  protected final boolean printVerboseDatabaseInfo;

  protected BaseFormatter(final O options,
                          final boolean printVerboseDatabaseInfo,
                          final OutputOptions outputOptions)
    throws SchemaCrawlerException
  {
    if (options == null)
    {
      throw new IllegalArgumentException("Options not provided");
    }
    if (outputOptions == null)
    {
      throw new IllegalArgumentException("Output options not provided");
    }

    this.options = options;

    this.printVerboseDatabaseInfo = !outputOptions.isNoInfo()
                                    && printVerboseDatabaseInfo;

    this.outputOptions = outputOptions;
    final OutputFormat outputFormat = outputOptions.getOutputFormat();
    if (outputFormat == OutputFormat.html)
    {
      formattingHelper = new HtmlFormattingHelper(outputFormat);
    }
    else
    {
      formattingHelper = new PlainTextFormattingHelper(outputFormat);
    }

    out = outputOptions.openOutputWriter();
  }

  abstract void handleInfoStart()
    throws SchemaCrawlerException;

  abstract void handle(SchemaCrawlerInfo schemaCrawlerInfo)
    throws SchemaCrawlerException;

  abstract void handle(DatabaseInfo databaseInfo)
    throws SchemaCrawlerException;

  abstract void handle(JdbcDriverInfo jdbcDriverInfo)
    throws SchemaCrawlerException;

  abstract void handleInfoEnd()
    throws SchemaCrawlerException;

}
