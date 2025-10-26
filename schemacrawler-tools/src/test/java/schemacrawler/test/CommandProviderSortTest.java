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
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;
import schemacrawler.test.utility.testcommand.TestCommandProvider;
import schemacrawler.tools.executable.BaseCommandProvider;
import schemacrawler.tools.executable.CommandProvider;
import schemacrawler.tools.executable.CommandRegistry;
import schemacrawler.tools.executable.SchemaCrawlerCommand;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;
import us.fatehi.utility.property.PropertyName;

public class CommandProviderSortTest {

  private final class OperationCommandProvider extends BaseCommandProvider {
    private OperationCommandProvider() {
      super(List.of(new PropertyName("OperationCommandProvider", "OperationCommandProvider")));
    }

    @Override
    public SchemaCrawlerCommand<?> newSchemaCrawlerCommand(
        final String command, final Config config) {
      return null;
    }

    @Override
    public boolean supportsOutputFormat(final String command, final OutputOptions outputOptions) {
      return false;
    }
  }

  private final TestCommandProvider testCommandProvider = new TestCommandProvider();
  private final CommandProvider fallbackCommandProvider = new OperationCommandProvider();
  private final CommandProvider otherCommandProvider =
      new BaseCommandProvider(
          List.of(new PropertyName("OtherCommandProvider", "OtherCommandProvider"))) {

        @Override
        public SchemaCrawlerCommand<?> newSchemaCrawlerCommand(
            final String command, final Config config) {
          return null;
        }

        @Override
        public boolean supportsOutputFormat(
            final String command, final OutputOptions outputOptions) {
          return false;
        }
      };

  @Test
  public void sort() {
    assertThat(
        CommandRegistry.commandComparator.compare(testCommandProvider, testCommandProvider), is(0));
    assertThat(
        CommandRegistry.commandComparator.compare(testCommandProvider, otherCommandProvider),
        is(greaterThan(0)));
    assertThat(
        CommandRegistry.commandComparator.compare(otherCommandProvider, testCommandProvider),
        is(lessThan(0)));
  }

  @Test
  public void sortForFallBack() {
    assertThat(
        CommandRegistry.commandComparator.compare(fallbackCommandProvider, fallbackCommandProvider),
        is(0));
    assertThat(
        CommandRegistry.commandComparator.compare(testCommandProvider, fallbackCommandProvider),
        is(lessThan(0)));
    assertThat(
        CommandRegistry.commandComparator.compare(fallbackCommandProvider, testCommandProvider),
        is(greaterThan(0)));
  }

  @Test
  public void sortNull() {
    assertThrows(
        IllegalArgumentException.class,
        () -> CommandRegistry.commandComparator.compare(null, null));
    assertThrows(
        IllegalArgumentException.class,
        () -> CommandRegistry.commandComparator.compare(testCommandProvider, null));
    assertThrows(
        IllegalArgumentException.class,
        () -> CommandRegistry.commandComparator.compare(null, fallbackCommandProvider));
  }
}
