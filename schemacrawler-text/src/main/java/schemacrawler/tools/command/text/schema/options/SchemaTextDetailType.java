/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.text.schema.options;

import us.fatehi.utility.property.PropertyName;

/** Enumeration for level of schema text output detail. */
public enum SchemaTextDetailType {
  brief(
      "Show basic schema information, "
          + "for tables, views and routines, columns, "
          + "primary keys, and foreign keys"),
  schema(
      "Show the commonly needed detail of the schema, "
          + "including details of tables, views and routines, columns, "
          + "primary keys, indexes, foreign keys, and triggers"),
  details(
      "Show maximum possible detail of the schema, "
          + "including privileges, and details of privileges, triggers, "
          + "and check constraints"),
  list("Show a list of schema objects");

  private final String description;

  SchemaTextDetailType(final String description) {
    this.description = description;
  }

  public PropertyName toPropertyName() {
    return new PropertyName(name(), description);
  }
}
