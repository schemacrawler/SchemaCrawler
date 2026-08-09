/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.command.options;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Locale;
import org.junit.jupiter.api.Test;

public class ScribeOptionsTest {

  @Test
  public void equalsAndHashCode() {
    final ScribeOptions options = new ScribeOptions("My Report", true, Locale.FRENCH, true);
    final ScribeOptions sameOptions = new ScribeOptions("My Report", true, Locale.FRENCH, true);
    final ScribeOptions differentOptions =
        new ScribeOptions("Other Report", true, Locale.FRENCH, true);

    assertThat(options.equals(options), is(true));
    assertThat(options.equals(sameOptions), is(true));
    assertThat(options.hashCode(), is(sameOptions.hashCode()));
    assertThat(options.equals(differentOptions), is(false));
  }
}
