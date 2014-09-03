package schemacrawler.tools.text.base;


import static sf.util.Utility.isLowerCase;

import java.io.PrintWriter;

import schemacrawler.schema.Column;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputWriter;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.text.utility.HtmlFormattingHelper;
import schemacrawler.tools.text.utility.PlainTextFormattingHelper;
import schemacrawler.tools.text.utility.TextFormattingHelper;
import schemacrawler.tools.traversal.TraversalHandler;

public abstract class BaseFormatter<O extends BaseTextOptions>
  implements TraversalHandler
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

    this.printVerboseDatabaseInfo = !options.isNoInfo()
                                    && printVerboseDatabaseInfo;

    this.outputOptions = outputOptions;
    final OutputFormat outputFormat = outputOptions.getOutputFormat();
    if (outputFormat == TextOutputFormat.html)
    {
      formattingHelper = new HtmlFormattingHelper((TextOutputFormat) outputFormat);
    }
    else
    {
      formattingHelper = new PlainTextFormattingHelper((TextOutputFormat) outputFormat);
    }

    out = new PrintWriter(new OutputWriter(outputOptions,
                                           options.isAppendOutput()), true);
  }

  protected String columnNullable(final String columnTypeName,
                                  final boolean isNullable)
  {
    return isNullable? "": isLowerCase(columnTypeName)? " not null"
                                                      : " NOT NULL";
  }

  protected boolean isColumnSignificant(final Column column)
  {
    return column != null
           && (column.isPartOfPrimaryKey() || column.isPartOfForeignKey() || column
             .isPartOfIndex());
  }

}
