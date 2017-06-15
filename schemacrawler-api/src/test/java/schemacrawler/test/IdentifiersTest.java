/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import schemacrawler.utility.Identifiers;

public class IdentifiersTest
{

  private final Identifiers identifiers = Identifiers.identifiers().build();

  @Test
  public void blank()
  {
    final String[] words = new String[] { "  ", "\t", };
    for (final String word: words)
    {
      assertFalse(word, identifiers.isReservedWord(word));
      assertTrue(word, identifiers.isToBeQuoted(word));
    }
  }

  @Test
  public void empty()
  {
    final String[] words = new String[] { "", null, };
    for (final String word: words)
    {
      assertFalse(word, identifiers.isReservedWord(word));
      assertFalse(word, identifiers.isToBeQuoted(word));
    }
  }

  @Test
  public void quotedIdentifiers()
  {
    final String[] words = new String[] {
                                          "1234",
                                          "w@w",
                                          "e.e",
                                          "१२३४५६७८९०",
                                          "Global Counts",
                                          "Trail ",
                                          " leaD" };
    for (final String word: words)
    {
      assertFalse(word, identifiers.isReservedWord(word));
      assertTrue(word, identifiers.isToBeQuoted(word));
    }
  }

  @Test
  public void nameQuotedNames()
  {
    final String[] names = new String[] {
                                          "one name",
                                          "\"UPDATE\"",
                                          "\"goodname\"" };
    final String[] quotedNames = new String[] {
                                                "\"one name\"",
                                                "\"UPDATE\"",
                                                "goodname" };
    for (int i = 0; i < names.length; i++)
    {
      final String name = names[i];
      final String quotedName = quotedNames[i];
      assertEquals(quotedName, identifiers.nameQuotedName(name));
    }
  }

  @Test
  public void sqlReservedWords()
  {
    final String[] words = new String[] { "update", "UPDATE", };
    for (final String word: words)
    {
      assertTrue(word, identifiers.isReservedWord(word));
      assertTrue(word, identifiers.isToBeQuoted(word));
    }
  }

  @Test
  public void unquotedIdentifiers()
  {
    final String[] words = new String[] {
                                          "qwer",
                                          "Qwer",
                                          "qweR",
                                          "qwEr",
                                          "QWER",
                                          "Q2w",
                                          "q2W",
                                          "q2w",
                                          "w_w",
                                          "W_W",
                                          "_W",
                                          "W_",
                                          "हम",
                                          "ह७म",
                                          "७म",
                                          "ह७",
                                          "हिंदी",
                                          "दी८दी" };
    for (final String word: words)
    {
      assertFalse(word, identifiers.isReservedWord(word));
      assertFalse(word, identifiers.isToBeQuoted(word));
    }
  }

}
