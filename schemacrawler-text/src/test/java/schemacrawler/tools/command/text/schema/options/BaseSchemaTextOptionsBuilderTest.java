package schemacrawler.tools.command.text.schema.options;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import schemacrawler.tools.options.Config;

/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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
public class BaseSchemaTextOptionsBuilderTest {

  @Test
  public void coverage() {
    final Config config = SchemaTextOptionsBuilder.builder().sortTableColumns().toConfig();
    final SchemaTextOptions options =
        SchemaTextOptionsBuilder.builder().sortTableColumns().toOptions();

    SchemaTextOptionsBuilder builder;

    // From config
    builder = SchemaTextOptionsBuilder.builder();
    assertThat(builder.toOptions().isAlphabeticalSortForTableColumns(), is(false));
    builder.fromConfig(config);
    assertThat(builder.toOptions().isAlphabeticalSortForTableColumns(), is(true));
    builder.fromConfig(null);
    assertThat(builder.toOptions().isAlphabeticalSortForTableColumns(), is(true));

    // From options
    builder = SchemaTextOptionsBuilder.builder();
    assertThat(builder.toOptions().isAlphabeticalSortForTableColumns(), is(false));
    builder.fromOptions(options);
    assertThat(builder.toOptions().isAlphabeticalSortForTableColumns(), is(true));
    builder.fromOptions(null);
    assertThat(builder.toOptions().isAlphabeticalSortForTableColumns(), is(true));
  }

  @Test
  public void hideRowCounts() {
    final Config config = SchemaTextOptionsBuilder.builder().hideRowCounts().toConfig();
    final SchemaTextOptions options =
        SchemaTextOptionsBuilder.builder().hideRowCounts().toOptions();

    SchemaTextOptionsBuilder builder;

    // On and off
    builder = SchemaTextOptionsBuilder.builder();
    assertThat(builder.toOptions().isHideTableRowCounts(), is(false));
    builder.hideRowCounts();
    assertThat(builder.toOptions().isHideTableRowCounts(), is(true));
    builder.hideRowCounts(false);
    assertThat(builder.toOptions().isHideTableRowCounts(), is(false));
    builder.hideRowCounts(true);
    assertThat(builder.toOptions().isHideTableRowCounts(), is(true));

    // From config
    builder = SchemaTextOptionsBuilder.builder();
    assertThat(builder.toOptions().isHideTableRowCounts(), is(false));
    builder.fromConfig(config);
    assertThat(builder.toOptions().isHideTableRowCounts(), is(true));
    builder.fromConfig(null);
    assertThat(builder.toOptions().isHideTableRowCounts(), is(true));

    // From options
    builder = SchemaTextOptionsBuilder.builder();
    assertThat(builder.toOptions().isHideTableRowCounts(), is(false));
    builder.fromOptions(options);
    assertThat(builder.toOptions().isHideTableRowCounts(), is(true));
    builder.fromOptions(null);
    assertThat(builder.toOptions().isHideTableRowCounts(), is(true));
  }

  @Test
  public void noAlternateKeyNames() {
    final Config config = SchemaTextOptionsBuilder.builder().noAlternateKeyNames().toConfig();
    final SchemaTextOptions options =
        SchemaTextOptionsBuilder.builder().noAlternateKeyNames().toOptions();

    SchemaTextOptionsBuilder builder;

    // On and off
    builder = SchemaTextOptionsBuilder.builder();
    assertThat(builder.toOptions().isHideAlternateKeyNames(), is(false));
    builder.noAlternateKeyNames();
    assertThat(builder.toOptions().isHideAlternateKeyNames(), is(true));
    builder.noAlternateKeyNames(false);
    assertThat(builder.toOptions().isHideAlternateKeyNames(), is(false));
    builder.noAlternateKeyNames(true);
    assertThat(builder.toOptions().isHideAlternateKeyNames(), is(true));

    // From config
    builder = SchemaTextOptionsBuilder.builder();
    assertThat(builder.toOptions().isHideAlternateKeyNames(), is(false));
    builder.fromConfig(config);
    assertThat(builder.toOptions().isHideAlternateKeyNames(), is(true));
    builder.fromConfig(null);
    assertThat(builder.toOptions().isHideAlternateKeyNames(), is(true));

    // From options
    builder = SchemaTextOptionsBuilder.builder();
    assertThat(builder.toOptions().isHideAlternateKeyNames(), is(false));
    builder.fromOptions(options);
    assertThat(builder.toOptions().isHideAlternateKeyNames(), is(true));
    builder.fromOptions(null);
    assertThat(builder.toOptions().isHideAlternateKeyNames(), is(true));
  }
}
