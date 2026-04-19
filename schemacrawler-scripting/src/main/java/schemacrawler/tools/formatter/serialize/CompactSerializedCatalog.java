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

/** Decorates a database to allow for serialization to compact (non-indented) JSON. */
public final class CompactSerializedCatalog extends BaseJacksonSerializedCatalog {

  public CompactSerializedCatalog(final Catalog catalog) {
    super(catalog);
  }

  @Override
  protected boolean isIndented() {
    return false;
  }

  @Override
  protected JsonMapper.Builder newMapperBuilder() {
    return JsonMapper.builder();
  }
}
