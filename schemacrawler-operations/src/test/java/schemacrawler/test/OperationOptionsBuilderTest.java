/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;
import schemacrawler.tools.command.text.operation.options.OperationOptions;
import schemacrawler.tools.command.text.operation.options.OperationOptionsBuilder;
import schemacrawler.tools.command.text.operation.options.OperationType;
import schemacrawler.tools.options.Config;

public class OperationOptionsBuilderTest {

  @Test
  public void builderNotNull() {
    assertThat(OperationOptionsBuilder.builder(), is(notNullValue()));
  }

  @Test
  public void fromConfigNull() {
    // Should return builder unchanged without throwing
    final OperationOptionsBuilder builder =
        OperationOptionsBuilder.builder().withCommand("count").fromConfig(null);
    assertThat(builder, is(notNullValue()));
  }

  @Test
  public void fromConfigRoundtrip() {
    final OperationOptionsBuilder original =
        OperationOptionsBuilder.builder().withCommand("count").showLobs(true).maxRows(7);
    final Config config = original.toConfig();

    final OperationOptionsBuilder restored = OperationOptionsBuilder.builder().withCommand("count");
    restored.fromConfig(config);
    final OperationOptions options = restored.toOptions();

    assertThat(options.isShowLobs(), is(true));
    assertThat(options.getMaxRows(), is(7));
  }

  @Test
  public void fromOptionsNull() {
    // Should not throw
    final OperationOptionsBuilder builder =
        OperationOptionsBuilder.builder().withCommand("count").fromOptions(null);
    assertThat(builder, is(notNullValue()));
  }

  @Test
  public void fromOptionsRoundtrip() {
    final OperationOptions original =
        OperationOptionsBuilder.builder().withCommand("dump").showLobs(true).maxRows(3).toOptions();

    final OperationOptionsBuilder restored =
        OperationOptionsBuilder.builder().withCommand("dump").fromOptions(original);
    final OperationOptions copy = restored.toOptions();

    assertThat(copy.isShowLobs(), is(true));
    assertThat(copy.getMaxRows(), is(3));
  }

  @Test
  public void maxRowsNegativeWrapsToMax() {
    final OperationOptionsBuilder builder =
        OperationOptionsBuilder.builder().withCommand("count").maxRows(-1);
    final OperationOptions options = builder.toOptions();
    assertThat(options.getMaxRows(), is(Integer.MAX_VALUE));
  }

  @Test
  public void maxRowsPositive() {
    final OperationOptionsBuilder builder =
        OperationOptionsBuilder.builder().withCommand("count").maxRows(10);
    final OperationOptions options = builder.toOptions();
    assertThat(options.getMaxRows(), is(10));
  }

  @Test
  public void showLobsFalse() {
    final OperationOptionsBuilder builder =
        OperationOptionsBuilder.builder().withCommand("count").showLobs(false);
    final OperationOptions options = builder.toOptions();
    assertThat(options.isShowLobs(), is(false));
  }

  @Test
  public void showLobsNoArg() {
    final OperationOptionsBuilder builder =
        OperationOptionsBuilder.builder().withCommand("count").showLobs();
    final OperationOptions options = builder.toOptions();
    assertThat(options.isShowLobs(), is(true));
  }

  @Test
  public void showLobsTrue() {
    final OperationOptionsBuilder builder =
        OperationOptionsBuilder.builder().withCommand("count").showLobs(true);
    final OperationOptions options = builder.toOptions();
    assertThat(options.isShowLobs(), is(true));
  }

  @Test
  public void toConfigContainsKeys() {
    final OperationOptionsBuilder builder =
        OperationOptionsBuilder.builder().withCommand("count").showLobs(true).maxRows(5);
    final Config config = builder.toConfig();
    assertThat(config, is(notNullValue()));
  }

  @Test
  public void withCommandCount() {
    final OperationOptionsBuilder builder = OperationOptionsBuilder.builder().withCommand("count");
    final OperationOptions options = builder.toOptions();
    assertThat(options.getOperation(), is(OperationType.count));
  }

  @Test
  public void withCommandDump() {
    final OperationOptionsBuilder builder = OperationOptionsBuilder.builder().withCommand("dump");
    final OperationOptions options = builder.toOptions();
    assertThat(options.getOperation(), is(OperationType.dump));
  }

  @Test
  public void withCommandTablesampleForcesMaxRows() {
    final OperationOptionsBuilder builder =
        OperationOptionsBuilder.builder().withCommand("tablesample").maxRows(100);
    final OperationOptions options = builder.toOptions();
    // tablesample forces maxRows to 10 in toOptions()
    assertThat(options.getMaxRows(), is(10));
    assertThat(options.getOperation(), is(OperationType.tablesample));
  }
}
