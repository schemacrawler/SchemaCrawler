/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.lint.formatter;

import static java.util.Objects.requireNonNull;
import static tools.jackson.core.StreamReadFeature.IGNORE_UNDEFINED;
import static tools.jackson.core.StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION;
import static tools.jackson.core.StreamWriteFeature.IGNORE_UNKNOWN;
import static tools.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
import static tools.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static tools.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS;
import static tools.jackson.databind.SerializationFeature.USE_EQUALITY_FOR_OBJECT_ID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.PrintWriter;
import java.util.List;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.tools.lint.Lint;
import schemacrawler.tools.lint.Lints;
import schemacrawler.tools.options.OutputOptions;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;
import tools.jackson.databind.cfg.MapperBuilder;

abstract class BaseLintReportJacksonGenerator implements LintReportGenerator {

  private static ObjectMapper newConfiguredObjectMapper(
      final MapperBuilder<? extends ObjectMapper, ?> mapperBuilder) {

    requireNonNull(mapperBuilder, "No mapper builder provided");
    mapperBuilder.enable(ORDER_MAP_ENTRIES_BY_KEYS, INDENT_OUTPUT, USE_EQUALITY_FOR_OBJECT_ID);
    mapperBuilder.disable(FAIL_ON_NULL_FOR_PRIMITIVES);
    mapperBuilder.enable(INCLUDE_SOURCE_IN_LOCATION, IGNORE_UNDEFINED);
    mapperBuilder.enable(IGNORE_UNKNOWN);

    @JsonPropertyOrder(alphabetic = true)
    @JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
    abstract class JacksonAnnotationMixIn {
      @JsonIgnore public Object value;

      // The JSON property tag is required
      @JsonProperty("key")
      private final String[] key = {};

      @JsonIgnore
      public abstract List<?> getCatalogLints();

      @JsonProperty("value")
      public abstract Object getValueAsString();

      @JsonIgnore
      public abstract boolean isEmpty();
    }

    mapperBuilder.addMixIn(Object.class, JacksonAnnotationMixIn.class);
    mapperBuilder.addMixIn(Lint.class, JacksonAnnotationMixIn.class);
    mapperBuilder.addMixIn(Lints.class, JacksonAnnotationMixIn.class);
    mapperBuilder.addMixIn(NamedObjectKey.class, JacksonAnnotationMixIn.class);

    final ObjectMapper objectMapper = mapperBuilder.build();
    return objectMapper;
  }

  private final PrintWriter out;

  BaseLintReportJacksonGenerator(final OutputOptions outputOptions) {
    out = outputOptions.openNewOutputWriter();
  }

  @Override
  public void generateLintReport(final Lints report) {
    requireNonNull(out, "No output stream provided");
    try {
      final ObjectMapper mapper = newConfiguredObjectMapper(newMapperBuilder());
      mapper.writeValue(out, report);
    } catch (final Exception e) {
      throw new ExecutionRuntimeException("Could not generate lint report", e);
    }
  }

  protected abstract <B extends MapperBuilder<?, B>> B newMapperBuilder();
}
