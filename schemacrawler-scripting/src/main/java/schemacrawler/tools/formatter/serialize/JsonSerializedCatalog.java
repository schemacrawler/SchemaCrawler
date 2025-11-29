/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.formatter.serialize;

import schemacrawler.schema.Catalog;
import tools.jackson.databind.json.JsonMapper;

/** Decorates a database to allow for serialization to JSON serialization. */
public final class JsonSerializedCatalog extends BaseJacksonSerializedCatalog {

  public JsonSerializedCatalog(final Catalog catalog) {
    super(catalog);
  }

  @Override
  protected final JsonMapper.Builder newMapperBuilder() {
    return JsonMapper.builder();
  }
}
