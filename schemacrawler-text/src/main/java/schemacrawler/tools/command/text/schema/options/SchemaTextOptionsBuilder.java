/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.text.schema.options;

import schemacrawler.tools.options.Config;

public final class SchemaTextOptionsBuilder
    extends BaseSchemaTextOptionsBuilder<SchemaTextOptionsBuilder, SchemaTextOptions> {

  public static SchemaTextOptionsBuilder builder() {
    return new SchemaTextOptionsBuilder();
  }

  public static SchemaTextOptionsBuilder builder(final SchemaTextOptions options) {
    return new SchemaTextOptionsBuilder().fromOptions(options);
  }

  public static SchemaTextOptions newSchemaTextOptions() {
    return new SchemaTextOptionsBuilder().toOptions();
  }

  public static SchemaTextOptions newSchemaTextOptions(final Config config) {
    return new SchemaTextOptionsBuilder().fromConfig(config).toOptions();
  }

  private SchemaTextOptionsBuilder() {
    // Set default values, if any
  }

  @Override
  public SchemaTextOptions toOptions() {
    return new SchemaTextOptions(this);
  }
}
