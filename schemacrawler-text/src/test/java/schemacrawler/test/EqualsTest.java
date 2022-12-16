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
package schemacrawler.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Description;

import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;

public class EqualsTest {

  @Test
  @Description("Ensure that equals is inherited from Object")
  public void schemaTextOptions() {
    final SchemaTextOptions newSchemaTextOptions1 = SchemaTextOptionsBuilder.newSchemaTextOptions();
    final SchemaTextOptions newSchemaTextOptions2 = SchemaTextOptionsBuilder.newSchemaTextOptions();

    assertThat(newSchemaTextOptions1.equals(newSchemaTextOptions1), is(true));
    assertThat(newSchemaTextOptions1.equals(newSchemaTextOptions2), is(false));
  }
}
