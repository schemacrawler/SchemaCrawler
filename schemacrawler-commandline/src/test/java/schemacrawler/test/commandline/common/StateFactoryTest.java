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
