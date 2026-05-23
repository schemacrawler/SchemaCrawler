/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.lint.config;

import static tools.jackson.core.StreamWriteFeature.IGNORE_UNKNOWN;
import static tools.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS;
import static tools.jackson.databind.MapperFeature.SORT_PROPERTIES_ALPHABETICALLY;
import static tools.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static tools.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS;
import static tools.jackson.databind.SerializationFeature.USE_EQUALITY_FOR_OBJECT_ID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.inclusionrule.RegularExpressionRule;
import schemacrawler.tools.options.Config;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.ser.std.StdSerializer;

/**
 * A lazy {@link Supplier Supplier&lt;String&gt;} that serializes a {@link LinterConfigs} instance
 * to a detailed JSON string using Jackson mixins, exposing all internal fields correctly.
 *
 * <p>Addresses limitations of generic serializers:
 *
 * <ul>
 *   <li>{@link LinterConfigs} is {@code Iterable}, not a {@code Collection} — handled by a custom
 *       serializer that writes it as a JSON array.
 *   <li>{@link LinterConfig} carries {@code @ConstructorProperties} which distorts serialization
 *       property names — overridden via a mixin that applies {@code @JsonProperty} to each getter.
 *   <li>{@link Config} has a private {@code configMap} field with no public getter — accessed via
 *       reflection in {@link ConfigSerializer}.
 *   <li>{@link InclusionRule} / {@link RegularExpressionRule} hold {@code java.util.regex.Pattern}
 *       fields — serialized as {@code {"include": "...", "exclude": "..."}} strings.
 * </ul>
 *
 * <p>{@code toString()} delegates to {@code get()}, making instances directly usable as logger
 * arguments.
 */
public final class LinterConfigsFormat implements Supplier<String> {

  static final class ConfigSerializer extends StdSerializer<Config> {

    ConfigSerializer() {
      super(Config.class);
    }

    @Override
    public void serialize(
        final Config config, final JsonGenerator gen, final SerializationContext provider)
        throws JacksonException {
      final Map<String, Object> configMap = config.getSubMap("");
      provider.writeValue(gen, configMap);
    }
  }

  @JsonPropertyOrder({
    "id",
    "run",
    "severity",
    "threshold",
    "table-inclusion-pattern",
    "table-exclusion-pattern",
    "column-inclusion-pattern",
    "column-exclusion-pattern",
    "config"
  })
  private abstract static class LinterConfigMixin {

    @JsonSerialize(using = ConfigSerializer.class)
    abstract Config getConfig();

    @JsonSerialize
    @JsonProperty("id")
    abstract String getLinterId();

    @JsonProperty("run")
    abstract boolean isRunLinter();
  }

  private static final ObjectMapper MAPPER;

  static {
    MAPPER =
        JsonMapper.builder()
            .addMixIn(LinterConfig.class, LinterConfigMixin.class)
            .enable(IGNORE_UNKNOWN)
            .enable(ORDER_MAP_ENTRIES_BY_KEYS, INDENT_OUTPUT, USE_EQUALITY_FOR_OBJECT_ID)
            .enable(SORT_PROPERTIES_ALPHABETICALLY, ACCEPT_CASE_INSENSITIVE_ENUMS)
            .build();
  }

  private static final Logger LOGGER = Logger.getLogger(LinterConfigsFormat.class.getName());

  private final LinterConfigs linterConfigs;

  public LinterConfigsFormat(final LinterConfigs linterConfigs) {
    this.linterConfigs = linterConfigs;
  }

  @Override
  public String get() {
    if (linterConfigs == null) {
      return "null";
    }
    try {
      return MAPPER.writeValueAsString(linterConfigs);
    } catch (final JacksonException e) {
      LOGGER.log(Level.WARNING, "Could not serialize LinterConfigs", e);
      return linterConfigs.toString();
    }
  }

  @Override
  public String toString() {
    return get();
  }
}
