package schemacrawler.test;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import schemacrawler.utility.Identifiers;

public class IdentifiersTest
{

  private final Identifiers reservedWords = Identifiers.identifiers().build();

  @Test
  public void blank()
  {
    final String[] words = new String[] { "  ", "\t", };
    for (final String word: words)
    {
      assertFalse(word, reservedWords.isReservedWord(word));
      assertTrue(word, reservedWords.isToBeQuoted(word));
    }
  }

  @Test
  public void empty()
  {
    final String[] words = new String[] { "", null, };
    for (final String word: words)
    {
      assertFalse(word, reservedWords.isReservedWord(word));
      assertFalse(word, reservedWords.isToBeQuoted(word));
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
      assertFalse(word, reservedWords.isReservedWord(word));
      assertTrue(word, reservedWords.isToBeQuoted(word));
    }
  }

  @Test
  public void sqlReservedWords()
  {
    final String[] words = new String[] { "update", "UPDATE", };
    for (final String word: words)
    {
      assertTrue(word, reservedWords.isReservedWord(word));
      assertTrue(word, reservedWords.isToBeQuoted(word));
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
      assertFalse(word, reservedWords.isReservedWord(word));
      assertFalse(word, reservedWords.isToBeQuoted(word));
    }
  }

}
