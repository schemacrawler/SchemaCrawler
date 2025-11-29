/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.formatter.serialize;

import schemacrawler.schema.Catalog;
import tools.jackson.dataformat.yaml.YAMLMapper;

/** Decorates a database to allow for serialization to YAML serialization. */
public final class YamlSerializedCatalog extends BaseJacksonSerializedCatalog {

  public YamlSerializedCatalog(final Catalog catalog) {
    super(catalog);
  }

  @Override
  protected final YAMLMapper.Builder newMapperBuilder() {
    return YAMLMapper.builder();
  }
}
