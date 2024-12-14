/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.tools.command.text.schema.options;

import us.fatehi.utility.property.PropertyName;

/** Enumeration for level of schema text output detail. */
public enum SchemaTextDetailType {
  brief(
      "Shows basic schema information, "
          + "for tables, views and routines, columns, "
          + "primary keys, and foreign keys"),
  schema(
      "Shows the commonly needed detail of the schema, "
          + "including details of tables, views and routines, columns, "
          + "primary keys, indexes, foreign keys, and triggers"),
  details(
      "Shows maximum possible detail of the schema, "
          + "including privileges, and details of privileges, triggers, "
          + "and check constraints"),
  list("Shows a list of schema objects");

  private final String description;

  SchemaTextDetailType(final String description) {
    this.description = description;
  }

  public PropertyName toPropertyName() {
    return new PropertyName(name(), description);
  }
}
