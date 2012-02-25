package schemacrawler.tools.text.base;


import java.io.PrintWriter;
import java.io.Writer;

import schemacrawler.schemacrawler.Options;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputWriter;
import schemacrawler.tools.text.utility.HtmlFormattingHelper;
import schemacrawler.tools.text.utility.PlainTextFormattingHelper;
import schemacrawler.tools.text.utility.TextFormattingHelper;
import schemacrawler.tools.traversal.TraversalHandler;

public abstract class BaseFormatter<O extends Options>
  implements TraversalHandler
{

  protected final O options;
  protected final OutputOptions outputOptions;
  protected final PrintWriter out;
  protected final TextFormattingHelper formattingHelper;
  protected final boolean printVerboseDatabaseInfo;

  protected BaseFormatter(final O options,
                          final boolean printVerboseDatabaseInfo,
                          final OutputOptions outputOptions,
                          final Writer writer)
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

    if (writer == null)
    {
      out = new PrintWriter(new OutputWriter(outputOptions));
    }
    else
    {
      out = new PrintWriter(new OutputWriter(writer));
    }
  }

}
