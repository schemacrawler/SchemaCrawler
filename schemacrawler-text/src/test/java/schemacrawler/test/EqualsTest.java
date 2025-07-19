/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;

public class EqualsTest {

  @Test
  @DisplayName("Ensure that equals is inherited from Object")
  public void schemaTextOptions() {
    final SchemaTextOptions newSchemaTextOptions1 = SchemaTextOptionsBuilder.newSchemaTextOptions();
    final SchemaTextOptions newSchemaTextOptions2 = SchemaTextOptionsBuilder.newSchemaTextOptions();

    assertThat(newSchemaTextOptions1.equals(newSchemaTextOptions1), is(true));
    assertThat(newSchemaTextOptions1.equals(newSchemaTextOptions2), is(false));
  }
}
