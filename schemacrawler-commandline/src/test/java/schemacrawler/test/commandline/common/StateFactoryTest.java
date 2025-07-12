/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.test.commandline.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;

import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.commandline.state.StateFactory;

public class StateFactoryTest {

  @Test
  public void stateFactory() throws Exception {
    final ShellState state = new ShellState();
    final StateFactory stateFactory = new StateFactory(state);
    final String string = stateFactory.create(null);

    assertThat(string, is(nullValue()));
  }

  @Test
  public void stateFactoryString() throws Exception {
    final ShellState state = new ShellState();
    final StateFactory stateFactory = new StateFactory(state);
    final String string = stateFactory.create(String.class);

    assertThat(string, not(nullValue()));
    assertThat(string.isEmpty(), is(true));
  }
}
