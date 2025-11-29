/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.commandline.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.options.Config;

public class SchemaCrawlerStateTest {

  @Test
  public void baseConfigNull() throws Exception {
    final ShellState state = new ShellState();

    assertThat(state.getConfig().size(), is(0));
    // Assert internal field
    final Config baseConfigurationBefore = getBaseConfiguration(state);
    assertThat(baseConfigurationBefore, is(nullValue()));

    // TEST
    state.setBaseConfig(null);

    assertThat(state.getConfig().size(), is(0));
    // Assert internal field
    final Config baseConfigurationAfter = getBaseConfiguration(state);
    assertThat(baseConfigurationAfter.size(), is(0));
  }

  private Config getBaseConfiguration(final ShellState state)
      throws NoSuchFieldException, IllegalAccessException {
    final Field f = state.getClass().getDeclaredField("baseConfig");
    f.setAccessible(true);
    return (Config) f.get(state);
  }
}
