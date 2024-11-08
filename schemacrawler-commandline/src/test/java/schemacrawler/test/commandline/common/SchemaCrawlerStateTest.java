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
