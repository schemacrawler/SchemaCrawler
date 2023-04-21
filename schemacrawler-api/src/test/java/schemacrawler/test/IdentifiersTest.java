/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import schemacrawler.schemacrawler.Identifiers;

public class IdentifiersTest {

  private final Identifiers identifiers = Identifiers.STANDARD;

  @Test
  public void blank() {
    final String[] words =
        new String[] {
          "  ", "\t",
        };
    for (final String word : words) {
      assertThat(word, identifiers.isReservedWord(word), is(false));
      assertThat(word, identifiers.isToBeQuoted(word), is(true));
    }
  }

  @Test
  public void empty() {
    final String[] words =
        new String[] {
          "", null,
        };
    for (final String word : words) {
      assertThat(word, identifiers.isReservedWord(word), is(false));
      assertThat(word, identifiers.isToBeQuoted(word), is(false));
    }
  }

  @Test
  public void quotedIdentifiers() {
    final String[] words =
        new String[] {
          "1234", "w@w", "e.e", "१२३४५६७८९०", "Celebrity Updates", "Trail ", " leaD", "q2W", "W_w"
        };
    for (final String word : words) {
      assertThat(word, identifiers.isReservedWord(word), is(false));
      assertThat(word, identifiers.isToBeQuoted(word), is(true));
    }
  }

  @Test
  public void quotedNames() {
    final String[] names =
        new String[] {"one name", "\"UPDATE\"", "1234", "goodname", "\"goodname\""};
    final String[] quotedNames =
        new String[] {"\"one name\"", "\"UPDATE\"", "\"1234\"", "goodname", "\"goodname\""};
    for (int i = 0; i < names.length; i++) {
      final String name = names[i];
      final String quotedName = quotedNames[i];
      assertThat(quotedName, equalTo(identifiers.quoteName(name)));
    }
  }

  @Test
  public void sqlReservedWords() {
    final String[] words =
        new String[] {
          "update", "UPDATE",
        };
    for (final String word : words) {
      assertThat(word, identifiers.isReservedWord(word), is(true));
      assertThat(word, identifiers.isToBeQuoted(word), is(true));
    }
  }

  @Test
  public void unquotedIdentifiers() {
    final String[] words =
        new String[] {
          "qwer", "QWER", "Q2W", "q2w", "w_w", "W_W", "_W", "W_", "हम", "ह७म", "७म", "ह७", "हिंदी",
          "दी८दी"
        };
    for (final String word : words) {
      assertThat(word, identifiers.isReservedWord(word), is(false));
      assertThat(word, identifiers.isToBeQuoted(word), is(false));
    }
  }
}
