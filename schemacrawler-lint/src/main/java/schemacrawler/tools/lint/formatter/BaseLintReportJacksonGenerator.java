package schemacrawler.tools.lint.formatter;

import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static com.fasterxml.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS;
import static com.fasterxml.jackson.databind.SerializationFeature.USE_EQUALITY_FOR_OBJECT_ID;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_ENUMS_USING_TO_STRING;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import static java.util.Objects.requireNonNull;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.tools.lint.Lint;
import schemacrawler.tools.lint.report.LintReport;
import schemacrawler.tools.options.OutputOptions;

abstract class BaseLintReportJacksonGenerator implements LintReportGenerator {

  private final PrintWriter out;

  BaseLintReportJacksonGenerator(final OutputOptions outputOptions) {
    out = outputOptions.openNewOutputWriter();
  }

  @Override
  public void generateLintReport(final LintReport report) {
    requireNonNull(out, "No output stream provided");
    try {
      final ObjectMapper mapper = newConfiguredObjectMapper();
      mapper.writeValue(out, report);
    } catch (final Exception e) {
      throw new ExecutionRuntimeException("Could not generate lint report", e);
    }
  }

  protected abstract ObjectMapper newObjectMapper();

  private ObjectMapper newConfiguredObjectMapper() {

    @JsonPropertyOrder(alphabetic = true)
    @JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
    abstract class JacksonAnnotationMixIn {
      @JsonIgnore public Object value;

      @JsonProperty("value")
      public abstract Object getValueAsString();

      @JsonProperty("key")
      private final String[] key = {};
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
    mapper.addMixIn(NamedObjectKey.class, JacksonAnnotationMixIn.class);
    mapper.registerModule(timeModule);
    return mapper;
  }
}
