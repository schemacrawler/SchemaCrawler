/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.lint.formatter;

import static java.util.Objects.requireNonNull;

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
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;
import tools.jackson.databind.cfg.MapperBuilder;

abstract class BaseLintReportJacksonGenerator implements LintReportGenerator {

  @JsonPropertyOrder(alphabetic = true)
  @JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
  private abstract static class JacksonAnnotationMixIn {
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

  private final PrintWriter out;

  BaseLintReportJacksonGenerator(final OutputOptions outputOptions) {
    out = outputOptions.openNewOutputWriter();
  }

  @Override
  @SuppressWarnings({"rawtypes", "unchecked"})
  public void generateLintReport(final Lints report) {
    requireNonNull(out, "No output stream provided");
    try {
      final MapperBuilder builder = newMapperBuilder();
      builder.addMixIn(Object.class, JacksonAnnotationMixIn.class);
      builder.addMixIn(Lint.class, JacksonAnnotationMixIn.class);
      builder.addMixIn(Lints.class, JacksonAnnotationMixIn.class);
      builder.addMixIn(NamedObjectKey.class, JacksonAnnotationMixIn.class);
      builder.build().writeValue(out, report);
    } catch (final Exception e) {
      throw new ExecutionRuntimeException("Could not generate lint report", e);
    }
  }

  protected abstract <B extends MapperBuilder<?, B>> B newMapperBuilder();
}
