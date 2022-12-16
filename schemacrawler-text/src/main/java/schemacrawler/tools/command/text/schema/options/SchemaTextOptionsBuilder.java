/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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
