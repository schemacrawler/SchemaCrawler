/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.lint.config;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.inclusionrule.RegularExpressionRule;
import schemacrawler.tools.lint.LintSeverity;
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

    private static final Logger LOGGER = Logger.getLogger(ConfigSerializer.class.getName());

    ConfigSerializer() {
      super(Config.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void serialize(
        final Config config, final JsonGenerator gen, final SerializationContext provider)
        throws JacksonException {
      try {
        final Field field = Config.class.getDeclaredField("configMap");
        field.setAccessible(true);
        final Map<String, Object> configMap = (Map<String, Object>) field.get(config);
        provider.writeValue(gen, configMap);
      } catch (final ReflectiveOperationException e) {
        LOGGER.log(Level.WARNING, "Could not access Config.configMap via reflection", e);
        gen.writeString(config.toString());
      }
    }
  }

  static final class InclusionRuleSerializer extends StdSerializer<InclusionRule> {
    InclusionRuleSerializer() {
      super(InclusionRule.class);
    }

    @Override
    public void serialize(
        final InclusionRule rule, final JsonGenerator gen, final SerializationContext provider)
        throws JacksonException {
      if (rule instanceof final RegularExpressionRule regexRule) {
        gen.writeStartObject();
        gen.writeName("include");
        gen.writeString(regexRule.getInclusionPattern().pattern());
        gen.writeName("exclude");
        gen.writeString(regexRule.getExclusionPattern().pattern());
        gen.writeEndObject();
      } else {
        gen.writeString(rule.toString());
      }
    }
  }

  static final class LinterConfigsSerializer extends StdSerializer<LinterConfigs> {
    LinterConfigsSerializer() {
      super(LinterConfigs.class);
    }

    @Override
    public void serialize(
        final LinterConfigs configs, final JsonGenerator gen, final SerializationContext provider)
        throws JacksonException {
      gen.writeStartArray();
      for (final LinterConfig config : configs) {
        provider.writeValue(gen, config);
      }
      gen.writeEndArray();
    }
  }

  @JsonSerialize(using = InclusionRuleSerializer.class)
  private abstract static class InclusionRuleMixin {}

  private abstract static class LinterConfigMixin {
    @JsonSerialize(using = InclusionRuleSerializer.class)
    abstract InclusionRule getColumnInclusionRule();

    @JsonSerialize(using = ConfigSerializer.class)
    abstract Config getConfig();

    @tools.jackson.databind.annotation.JsonSerialize
    abstract String getLinterId();

    abstract LintSeverity getSeverity();

    @JsonSerialize(using = InclusionRuleSerializer.class)
    abstract InclusionRule getTableInclusionRule();

    abstract int getThreshold();

    abstract boolean isRunLinter();
  }

  @JsonSerialize(using = LinterConfigsSerializer.class)
  private abstract static class LinterConfigsMixin {}

  private static final ObjectMapper MAPPER;

  static {
    MAPPER =
        JsonMapper.builder()
            .addMixIn(LinterConfigs.class, LinterConfigsMixin.class)
            .addMixIn(LinterConfig.class, LinterConfigMixin.class)
            .addMixIn(InclusionRule.class, InclusionRuleMixin.class)
            .enable(tools.jackson.databind.SerializationFeature.INDENT_OUTPUT)
            .enable(tools.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
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
