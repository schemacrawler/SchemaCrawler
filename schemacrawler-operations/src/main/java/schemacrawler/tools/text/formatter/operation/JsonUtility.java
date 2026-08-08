/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.text.formatter.operation;

import static tools.jackson.core.StreamReadFeature.IGNORE_UNDEFINED;
import static tools.jackson.core.StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION;
import static tools.jackson.core.StreamWriteFeature.IGNORE_UNKNOWN;
import static tools.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
import static tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static tools.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS;
import static tools.jackson.databind.MapperFeature.SORT_PROPERTIES_ALPHABETICALLY;
import static tools.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static tools.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS;
import static tools.jackson.databind.SerializationFeature.USE_EQUALITY_FOR_OBJECT_ID;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.json.JsonMapper;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
class JsonUtility {

  public static JsonMapper.Builder newJsonMapperBuilder() {
    return (JsonMapper.Builder) configureMapper(JsonMapper.builder());
  }

  /** Applies the standard base configuration to any {@link MapperBuilder}. */
  private static MapperBuilder<?, ?> configureMapper(final MapperBuilder<?, ?> mapperBuilder) {
    // De-serialization
    mapperBuilder.enable(INCLUDE_SOURCE_IN_LOCATION, IGNORE_UNDEFINED);
    mapperBuilder.enable(FAIL_ON_UNKNOWN_PROPERTIES);
    mapperBuilder.disable(FAIL_ON_NULL_FOR_PRIMITIVES);
    // Serialization
    mapperBuilder.enable(IGNORE_UNKNOWN);
    mapperBuilder.enable(ORDER_MAP_ENTRIES_BY_KEYS, INDENT_OUTPUT, USE_EQUALITY_FOR_OBJECT_ID);
    mapperBuilder.enable(SORT_PROPERTIES_ALPHABETICALLY, ACCEPT_CASE_INSENSITIVE_ENUMS);
    // Omit null and empty values in output
    mapperBuilder.changeDefaultPropertyInclusion(
        incl -> incl.withValueInclusion(JsonInclude.Include.NON_EMPTY));

    return mapperBuilder;
  }

  private JsonUtility() {
    // Prevent instantiation
  }
}
