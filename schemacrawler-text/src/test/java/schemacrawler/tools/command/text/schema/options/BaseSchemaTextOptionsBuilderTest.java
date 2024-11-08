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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.tools.command.text.schema.options.HideDatabaseObjectNamesType.hideAlternateKeyNames;
import static schemacrawler.tools.command.text.schema.options.HideDatabaseObjectNamesType.hideRoutineSpecificNames;
import static schemacrawler.tools.command.text.schema.options.HideDatabaseObjectNamesType.hideTriggerNames;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.options.Config;

public class BaseSchemaTextOptionsBuilderTest {

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
    assertThat(builder.toOptions().is(hideAlternateKeyNames), is(false));
    builder.noAlternateKeyNames();
    assertThat(builder.toOptions().is(hideAlternateKeyNames), is(true));
    builder.noAlternateKeyNames(false);
    assertThat(builder.toOptions().is(hideAlternateKeyNames), is(false));
    builder.noAlternateKeyNames(true);
    assertThat(builder.toOptions().is(hideAlternateKeyNames), is(true));

    // From config
    builder = SchemaTextOptionsBuilder.builder();
    assertThat(builder.toOptions().is(hideAlternateKeyNames), is(false));
    builder.fromConfig(config);
    assertThat(builder.toOptions().is(hideAlternateKeyNames), is(true));
    builder.fromConfig(null);
    assertThat(builder.toOptions().is(hideAlternateKeyNames), is(true));

    // From options
    builder = SchemaTextOptionsBuilder.builder();
    assertThat(builder.toOptions().is(hideAlternateKeyNames), is(false));
    builder.fromOptions(options);
    assertThat(builder.toOptions().is(hideAlternateKeyNames), is(true));
    builder.fromOptions(null);
    assertThat(builder.toOptions().is(hideAlternateKeyNames), is(true));
  }

  @Test
  public void noRoutineSpecificNames() {
    final Config config = SchemaTextOptionsBuilder.builder().noRoutineSpecificNames().toConfig();
    final SchemaTextOptions options =
        SchemaTextOptionsBuilder.builder().noRoutineSpecificNames().toOptions();

    SchemaTextOptionsBuilder builder;

    // On and off
    builder = SchemaTextOptionsBuilder.builder();
    assertThat(builder.toOptions().is(hideRoutineSpecificNames), is(false));
    builder.noRoutineSpecificNames();
    assertThat(builder.toOptions().is(hideRoutineSpecificNames), is(true));
    builder.noRoutineSpecificNames(false);
    assertThat(builder.toOptions().is(hideRoutineSpecificNames), is(false));
    builder.noRoutineSpecificNames(true);
    assertThat(builder.toOptions().is(hideRoutineSpecificNames), is(true));

    // From config
    builder = SchemaTextOptionsBuilder.builder();
    assertThat(builder.toOptions().is(hideRoutineSpecificNames), is(false));
    builder.fromConfig(config);
    assertThat(builder.toOptions().is(hideRoutineSpecificNames), is(true));
    builder.fromConfig(null);
    assertThat(builder.toOptions().is(hideRoutineSpecificNames), is(true));

    // From options
    builder = SchemaTextOptionsBuilder.builder();
    assertThat(builder.toOptions().is(hideRoutineSpecificNames), is(false));
    builder.fromOptions(options);
    assertThat(builder.toOptions().is(hideRoutineSpecificNames), is(true));
    builder.fromOptions(null);
    assertThat(builder.toOptions().is(hideRoutineSpecificNames), is(true));
  }

  @Test
  public void noTriggerActionStatements() {
    final Config config = SchemaTextOptionsBuilder.builder().noTriggerActionStatements().toConfig();
    final SchemaTextOptions options =
        SchemaTextOptionsBuilder.builder().noTriggerActionStatements().toOptions();

    SchemaTextOptionsBuilder builder;

    // On and off
    builder = SchemaTextOptionsBuilder.builder();
    assertThat(builder.toOptions().isHideTriggerActionStatements(), is(false));
    builder.noTriggerActionStatements();
    assertThat(builder.toOptions().isHideTriggerActionStatements(), is(true));
    builder.noTriggerActionStatements(false);
    assertThat(builder.toOptions().isHideTriggerActionStatements(), is(false));
    builder.noTriggerActionStatements(true);
    assertThat(builder.toOptions().isHideTriggerActionStatements(), is(true));

    // From config
    builder = SchemaTextOptionsBuilder.builder();
    assertThat(builder.toOptions().isHideTriggerActionStatements(), is(false));
    builder.fromConfig(config);
    assertThat(builder.toOptions().isHideTriggerActionStatements(), is(true));
    builder.fromConfig(null);
    assertThat(builder.toOptions().isHideTriggerActionStatements(), is(true));

    // From options
    builder = SchemaTextOptionsBuilder.builder();
    assertThat(builder.toOptions().isHideTriggerActionStatements(), is(false));
    builder.fromOptions(options);
    assertThat(builder.toOptions().isHideTriggerActionStatements(), is(true));
    builder.fromOptions(null);
    assertThat(builder.toOptions().isHideTriggerActionStatements(), is(true));
  }

  @Test
  public void noTriggerNames() {
    final Config config = SchemaTextOptionsBuilder.builder().noTriggerNames().toConfig();
    final SchemaTextOptions options =
        SchemaTextOptionsBuilder.builder().noTriggerNames().toOptions();

    SchemaTextOptionsBuilder builder;

    // On and off
    builder = SchemaTextOptionsBuilder.builder();
    assertThat(builder.toOptions().is(hideTriggerNames), is(false));
    builder.noTriggerNames();
    assertThat(builder.toOptions().is(hideTriggerNames), is(true));
    builder.noTriggerNames(false);
    assertThat(builder.toOptions().is(hideTriggerNames), is(false));
    builder.noTriggerNames(true);
    assertThat(builder.toOptions().is(hideTriggerNames), is(true));

    // From config
    builder = SchemaTextOptionsBuilder.builder();
    assertThat(builder.toOptions().is(hideTriggerNames), is(false));
    builder.fromConfig(config);
    assertThat(builder.toOptions().is(hideTriggerNames), is(true));
    builder.fromConfig(null);
    assertThat(builder.toOptions().is(hideTriggerNames), is(true));

    // From options
    builder = SchemaTextOptionsBuilder.builder();
    assertThat(builder.toOptions().is(hideTriggerNames), is(false));
    builder.fromOptions(options);
    assertThat(builder.toOptions().is(hideTriggerNames), is(true));
    builder.fromOptions(null);
    assertThat(builder.toOptions().is(hideTriggerNames), is(true));
  }

  @Test
  public void portable() {

    SchemaTextOptionsBuilder builder;
    SchemaTextOptions options;

    builder = SchemaTextOptionsBuilder.builder();
    options = builder.toOptions();
    assertPortableNames(options, false);
    assertPortableBroad(options, false);

    builder.portable(PortableType.broad);
    options = builder.toOptions();
    assertPortableNames(options, true);
    assertPortableBroad(options, true);

    builder.portable(PortableType.names);
    options = builder.toOptions();
    assertPortableNames(options, true);
    assertPortableBroad(options, false);

    builder.portable(PortableType.none);
    options = builder.toOptions();
    assertPortableNames(options, false);
    assertPortableBroad(options, false);

    builder.portable(null);
    options = builder.toOptions();
    assertPortableNames(options, false);
    assertPortableBroad(options, false);
  }

  @Test
  public void portableNames() {

    SchemaTextOptionsBuilder builder;
    SchemaTextOptions options;

    // On and off
    builder = SchemaTextOptionsBuilder.builder();
    options = builder.toOptions();
    assertPortableNames(options, false);

    builder.portableNames();
    options = builder.toOptions();
    assertPortableNames(options, true);

    builder.portableNames(false);
    options = builder.toOptions();
    assertPortableNames(options, false);

    builder.portableNames(true);
    options = builder.toOptions();
    assertPortableNames(options, true);
  }

  @Test
  public void sorts() {
    final Config config =
        SchemaTextOptionsBuilder.builder()
            .sortTableColumns()
            .sortIndexes()
            .sortForeignKeys()
            .toConfig();
    final SchemaTextOptions options =
        SchemaTextOptionsBuilder.builder()
            .sortTableColumns()
            .sortIndexes()
            .sortForeignKeys()
            .toOptions();

    SchemaTextOptionsBuilder builder;

    // On and off
    builder = SchemaTextOptionsBuilder.builder();
    assertThat(builder.toOptions().isAlphabeticalSortForTableColumns(), is(false));
    assertThat(builder.toOptions().isAlphabeticalSortForIndexes(), is(false));
    assertThat(builder.toOptions().isAlphabeticalSortForForeignKeys(), is(false));
    builder.sortTableColumns().sortIndexes().sortForeignKeys();
    assertThat(builder.toOptions().isAlphabeticalSortForTableColumns(), is(true));
    assertThat(builder.toOptions().isAlphabeticalSortForIndexes(), is(true));
    assertThat(builder.toOptions().isAlphabeticalSortForForeignKeys(), is(true));
    builder.sortTableColumns(false).sortIndexes(false).sortForeignKeys(false);
    assertThat(builder.toOptions().isAlphabeticalSortForTableColumns(), is(false));
    assertThat(builder.toOptions().isAlphabeticalSortForIndexes(), is(false));
    assertThat(builder.toOptions().isAlphabeticalSortForForeignKeys(), is(false));
    builder.sortTableColumns(true).sortIndexes(true).sortForeignKeys(true);
    assertThat(builder.toOptions().isAlphabeticalSortForTableColumns(), is(true));
    assertThat(builder.toOptions().isAlphabeticalSortForIndexes(), is(true));
    assertThat(builder.toOptions().isAlphabeticalSortForForeignKeys(), is(true));

    // From config
    builder = SchemaTextOptionsBuilder.builder();
    assertThat(builder.toOptions().isAlphabeticalSortForTableColumns(), is(false));
    assertThat(builder.toOptions().isAlphabeticalSortForIndexes(), is(false));
    assertThat(builder.toOptions().isAlphabeticalSortForForeignKeys(), is(false));
    builder.fromConfig(config);
    assertThat(builder.toOptions().isAlphabeticalSortForTableColumns(), is(true));
    assertThat(builder.toOptions().isAlphabeticalSortForIndexes(), is(true));
    assertThat(builder.toOptions().isAlphabeticalSortForForeignKeys(), is(true));
    builder.fromConfig(null);
    assertThat(builder.toOptions().isAlphabeticalSortForTableColumns(), is(true));
    assertThat(builder.toOptions().isAlphabeticalSortForIndexes(), is(true));
    assertThat(builder.toOptions().isAlphabeticalSortForForeignKeys(), is(true));

    // From options
    builder = SchemaTextOptionsBuilder.builder();
    assertThat(builder.toOptions().isAlphabeticalSortForTableColumns(), is(false));
    assertThat(builder.toOptions().isAlphabeticalSortForIndexes(), is(false));
    assertThat(builder.toOptions().isAlphabeticalSortForForeignKeys(), is(false));
    builder.fromOptions(options);
    assertThat(builder.toOptions().isAlphabeticalSortForTableColumns(), is(true));
    assertThat(builder.toOptions().isAlphabeticalSortForIndexes(), is(true));
    assertThat(builder.toOptions().isAlphabeticalSortForForeignKeys(), is(true));
    builder.fromOptions(null);
    assertThat(builder.toOptions().isAlphabeticalSortForTableColumns(), is(true));
    assertThat(builder.toOptions().isAlphabeticalSortForIndexes(), is(true));
    assertThat(builder.toOptions().isAlphabeticalSortForForeignKeys(), is(true));
  }

  private void assertPortableNames(final SchemaTextOptions options, final boolean value) {
    assertThat(options.isShowUnqualifiedNames(), is(value));
    for (final HideDatabaseObjectNamesType hideDatabaseObjectNamesType :
        HideDatabaseObjectNamesType.values()) {
      assertThat(options.is(hideDatabaseObjectNamesType), is(value));
    }
  }

  private void assertPortableBroad(final SchemaTextOptions options, final boolean value) {
    assertThat(options.isShowStandardColumnTypeNames(), is(value));
    assertThat(options.isHideTriggerActionStatements(), is(value));
  }
}
