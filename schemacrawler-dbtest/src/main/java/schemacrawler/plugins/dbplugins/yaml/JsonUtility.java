/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbplugins.yaml;

import static java.util.Objects.requireNonNull;
import static tools.jackson.core.StreamReadFeature.IGNORE_UNDEFINED;
import static tools.jackson.core.StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION;
import static tools.jackson.core.StreamWriteFeature.IGNORE_UNKNOWN;
import static tools.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
import static tools.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS;
import static tools.jackson.databind.MapperFeature.SORT_PROPERTIES_ALPHABETICALLY;
import static tools.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static tools.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS;
import static tools.jackson.databind.SerializationFeature.USE_EQUALITY_FOR_OBJECT_ID;
import static tools.jackson.dataformat.yaml.YAMLWriteFeature.WRITE_DOC_START_MARKER;
import static us.fatehi.utility.Utility.isBlank;

import schemacrawler.schemacrawler.MetadataRetrievalStrategy;
import schemacrawler.schemacrawler.SchemaInfoMetadataRetrievalStrategy;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.dataformat.yaml.YAMLMapper;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public class JsonUtility {

  public static final ObjectMapper mapper =
      newConfiguredObjectMapper(YAMLMapper.builder().disable(WRITE_DOC_START_MARKER));

  private static JacksonModule getEnumMapper() {
    final SimpleModule module =
        new SimpleModule("Retrieval strategy map key and value serializaation");
    module.addKeySerializer(
        SchemaInfoMetadataRetrievalStrategy.class,
        new ValueSerializer<SchemaInfoMetadataRetrievalStrategy>() {

          @Override
          public void serialize(
              final SchemaInfoMetadataRetrievalStrategy value,
              final JsonGenerator gen,
              final SerializationContext ctxt)
              throws JacksonException {
            if (value == null) {
              gen.writeNull();
            } else {
              gen.writeString(value.getKey());
            }
          }
        });
    module.addKeyDeserializer(
        SchemaInfoMetadataRetrievalStrategy.class,
        new KeyDeserializer() {

          @Override
          public Object deserializeKey(final String key, final DeserializationContext ctxt)
              throws JacksonException {
            return SchemaInfoMetadataRetrievalStrategy.valueOfFromKey(key);
          }
        });
    module.addKeySerializer(
        MetadataRetrievalStrategy.class,
        new ValueSerializer<MetadataRetrievalStrategy>() {

          @Override
          public void serialize(
              final MetadataRetrievalStrategy value,
              final JsonGenerator gen,
              final SerializationContext ctxt)
              throws JacksonException {
            if (value == null) {
              gen.writeNull();
            } else {
              gen.writeString(value.name().replace('_', '-'));
            }
          }
        });
    module.addKeyDeserializer(
        MetadataRetrievalStrategy.class,
        new KeyDeserializer() {

          @Override
          public Object deserializeKey(final String key, final DeserializationContext ctxt)
              throws JacksonException {
            if (isBlank(key)) {
              return MetadataRetrievalStrategy.none;
            }
            return MetadataRetrievalStrategy.valueOf(key.replace('-', '_'));
          }
        });
    return module;
  }

  private static ObjectMapper newConfiguredObjectMapper(
      final MapperBuilder<? extends ObjectMapper, ?> mapperBuilder) {

    requireNonNull(mapperBuilder, "No mapper builder provided");
    // De-serialization
    mapperBuilder.enable(INCLUDE_SOURCE_IN_LOCATION, IGNORE_UNDEFINED);
    mapperBuilder.disable(FAIL_ON_NULL_FOR_PRIMITIVES);
    // Serialization
    mapperBuilder.enable(IGNORE_UNKNOWN);
    mapperBuilder.enable(ORDER_MAP_ENTRIES_BY_KEYS, INDENT_OUTPUT, USE_EQUALITY_FOR_OBJECT_ID);
    mapperBuilder.enable(SORT_PROPERTIES_ALPHABETICALLY, ACCEPT_CASE_INSENSITIVE_ENUMS);

    mapperBuilder.disable(ORDER_MAP_ENTRIES_BY_KEYS);

    mapperBuilder.addModule(getEnumMapper());

    final ObjectMapper objectMapper = mapperBuilder.build();
    return objectMapper;
  }

  private JsonUtility() {
    // Prevent instantiation
  }
}
