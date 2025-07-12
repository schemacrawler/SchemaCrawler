/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.formatter.serialize;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import schemacrawler.schema.Catalog;

/** Decorates a database to allow for serialization to YAML serialization. */
public final class YamlSerializedCatalog extends BaseJacksonSerializedCatalog {

  public YamlSerializedCatalog(final Catalog catalog) {
    super(catalog);
  }

  @Override
  protected ObjectMapper newObjectMapper() {
    return new ObjectMapper(new YAMLFactory());
  }
}
