/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.formatter.serialize;

import static com.fasterxml.jackson.core.StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import schemacrawler.schema.Catalog;

/** Decorates a database to allow for serialization to JSON serialization. */
public final class JsonSerializedCatalog extends BaseJacksonSerializedCatalog {

  public JsonSerializedCatalog(final Catalog catalog) {
    super(catalog);
  }

  @Override
  protected ObjectMapper newObjectMapper() {
    return JsonMapper.builder().enable(INCLUDE_SOURCE_IN_LOCATION).build();
  }
}
