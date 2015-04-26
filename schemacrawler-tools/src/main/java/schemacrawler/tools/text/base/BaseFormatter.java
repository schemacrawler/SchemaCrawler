package schemacrawler.tools.text.base;


import static java.util.Objects.requireNonNull;
import static sf.util.Utility.convertForComparison;
import static sf.util.Utility.isLowerCase;

import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import schemacrawler.schema.Column;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.text.utility.DatabaseObjectColorMap;
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
  protected final DatabaseObjectColorMap colorMap;
  protected final boolean printVerboseDatabaseInfo;

  protected BaseFormatter(final O options,
                          final boolean printVerboseDatabaseInfo,
                          final OutputOptions outputOptions)
    throws SchemaCrawlerException
  {
    this.options = requireNonNull(options, "Options not provided");

    this.outputOptions = requireNonNull(outputOptions,
                                        "Output options not provided");

    colorMap = new DatabaseObjectColorMap();

    this.printVerboseDatabaseInfo = !options.isNoInfo()
                                    && printVerboseDatabaseInfo;

    final TextOutputFormat outputFormat = TextOutputFormat
      .fromFormat(outputOptions.getOutputFormatValue());
    if (outputFormat == TextOutputFormat.html)
    {
      formattingHelper = new HtmlFormattingHelper(outputFormat);
    }
    else
    {
      formattingHelper = new PlainTextFormattingHelper(outputFormat);
    }

    out = new PrintWriter(outputOptions.openNewOutputWriter(options
      .isAppendOutput()), true);
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

  protected String nodeId(final DatabaseObject dbObject)
  {
    if (dbObject == null)
    {
      return "";
    }
    else
    {
      return convertForComparison(dbObject.getName()) + "_"
             + Integer.toHexString(dbObject.getLookupKey().hashCode());
    }
  }

  protected String formatTimestamp(final TemporalAccessor timestamp)
  {
    return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(timestamp);
  }

}
