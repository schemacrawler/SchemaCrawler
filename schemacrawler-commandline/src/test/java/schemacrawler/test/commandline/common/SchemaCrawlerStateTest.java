/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.lang.reflect.Field;
import java.util.Map;

import org.junit.jupiter.api.Test;

import schemacrawler.tools.commandline.state.SchemaCrawlerShellState;

public class SchemaCrawlerStateTest {

  @Test
  public void baseConfigNull() throws Exception {
    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();

    assertThat(state.getBaseConfiguration(), is(anEmptyMap()));
    // Assert internal field
    final Map<String, String> baseConfigurationBefore = getBaseConfiguration(state);
    assertThat(baseConfigurationBefore, is(nullValue()));

    // TEST
    state.setBaseConfiguration(null);

    assertThat(state.getBaseConfiguration(), is(anEmptyMap()));
    // Assert internal field
    final Map<String, String> baseConfigurationAfter = getBaseConfiguration(state);
    assertThat(baseConfigurationAfter, is(anEmptyMap()));
  }

  private Map<String, String> getBaseConfiguration(final SchemaCrawlerShellState state)
      throws NoSuchFieldException, IllegalAccessException {
    final Field f = state.getClass().getDeclaredField("baseConfiguration");
    f.setAccessible(true);
    return (Map<String, String>) f.get(state);
  }
}
