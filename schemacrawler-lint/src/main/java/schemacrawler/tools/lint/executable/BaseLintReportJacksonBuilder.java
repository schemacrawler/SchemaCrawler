package schemacrawler.tools.lint.executable;

import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static com.fasterxml.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS;
import static com.fasterxml.jackson.databind.SerializationFeature.USE_EQUALITY_FOR_OBJECT_ID;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_ENUMS_USING_TO_STRING;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.lint.Lint;
import schemacrawler.tools.lint.LintReport;
import schemacrawler.tools.options.OutputOptions;

abstract class BaseLintReportJacksonBuilder implements LintReportBuilder {

  private final PrintWriter out;

  BaseLintReportJacksonBuilder(final OutputOptions outputOptions) throws SchemaCrawlerException {
    try {
      out = new PrintWriter(outputOptions.openNewOutputWriter(), true);
    } catch (final IOException e) {
      throw new SchemaCrawlerException("Cannot open output writer", e);
    }
  }

  @Override
  public void generateLintReport(final LintReport report) throws SchemaCrawlerException {
    requireNonNull(out, "No output stream provided");
    try {
      @JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
      @JsonPropertyOrder(alphabetic = true)
      abstract class JacksonAnnotationMixIn {
        @JsonIgnore public Object value;

        @JsonProperty("value")
        public abstract Object getValueAsString();
      }

      final JavaTimeModule timeModule = new JavaTimeModule();
      timeModule.addSerializer(
          LocalDateTime.class,
          new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

      final ObjectMapper mapper = newObjectMapper();
      mapper.enable(
          ORDER_MAP_ENTRIES_BY_KEYS,
          INDENT_OUTPUT,
          USE_EQUALITY_FOR_OBJECT_ID,
          WRITE_ENUMS_USING_TO_STRING);
      mapper.addMixIn(Object.class, JacksonAnnotationMixIn.class);
      mapper.addMixIn(Lint.class, JacksonAnnotationMixIn.class);
      mapper.registerModule(timeModule);

      // Write JSON to stream
      mapper.writeValue(out, report);
    } catch (final Exception e) {
      throw new SchemaCrawlerException("Could not serialize catalog", e);
    }
  }

  protected abstract ObjectMapper newObjectMapper();
}
