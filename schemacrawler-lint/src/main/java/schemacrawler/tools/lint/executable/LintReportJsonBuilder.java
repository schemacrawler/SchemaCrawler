package schemacrawler.tools.lint.executable;


import static com.fasterxml.jackson.databind.SerializationFeature.*;
import static java.util.Objects.requireNonNull;
import static sf.util.Utility.isBlank;

import java.io.IOException;
import java.io.PrintWriter;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.lint.Lint;
import schemacrawler.tools.lint.LintedCatalog;
import schemacrawler.tools.options.OutputOptions;

public class LintReportJsonBuilder
  implements LintReportBuilder
{

  private final LintOptions lintOptions;
  private final PrintWriter out;

  LintReportJsonBuilder(final LintOptions lintOptions,
                        final OutputOptions outputOptions,
                        final String identifierQuoteString)
    throws SchemaCrawlerException
  {
    try
    {
      out = new PrintWriter(outputOptions.openNewOutputWriter(), true);
    }
    catch (final IOException e)
    {
      throw new SchemaCrawlerException("Cannot open output writer", e);
    }

    this.lintOptions = lintOptions;
  }

  @Override
  public boolean canBuildReport(final LintOptions options,
                                final OutputOptions outputOptions)
  {
    final boolean canBuildReport;
    final String outputFormatValue = outputOptions.getOutputFormatValue();
    canBuildReport =
      !isBlank(outputFormatValue) && outputFormatValue.equalsIgnoreCase("json");
    return canBuildReport;
  }

  @Override
  public void generateLintReport(final LintedCatalog catalog)
    throws SchemaCrawlerException
  {
    requireNonNull(out, "No output stream provided");
    try
    {
      @JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
      @JsonPropertyOrder(alphabetic = true)
      abstract class JacksonAnnotationMixIn
      {
        @JsonProperty("value")
        public abstract Object getValueAsString();
        @JsonIgnore
        public Object value;
      }

      final ObjectMapper mapper = new ObjectMapper();
      mapper.enable(ORDER_MAP_ENTRIES_BY_KEYS,
                    INDENT_OUTPUT,
                    USE_EQUALITY_FOR_OBJECT_ID,
                    WRITE_ENUMS_USING_TO_STRING);
      mapper.addMixIn(Object.class, JacksonAnnotationMixIn.class);
      mapper.addMixIn(Lint.class, JacksonAnnotationMixIn.class);

      // Write JSON to stream
      mapper.writeValue(out, catalog.getCollector());
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerException("Could not serialize catalog", e);
    }
  }

}
