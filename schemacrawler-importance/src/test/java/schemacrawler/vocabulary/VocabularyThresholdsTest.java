/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.vocabulary;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class VocabularyThresholdsTest {

  @Test
  public void defaultValuesMatchTheDesign() {
    final VocabularyThresholds defaults = VocabularyThresholds.DEFAULT;
    assertThat(defaults.minObjectCount(), is(3));
    assertThat(defaults.prefixShare(), is(0.3));
    assertThat(defaults.minNamesForPrefixCheck(), is(10));
    assertThat(defaults.genericTableShare(), is(0.25));
    assertThat(defaults.minTermCount(), is(10));
    assertThat(defaults.minTableCoverage(), is(0.5));
  }

  @Test
  public void countsMustBeAtLeastOne() {
    assertThrows(
        IllegalArgumentException.class, () -> new VocabularyThresholds(0, 0.3, 10, 0.25, 10, 0.5));
    assertThrows(
        IllegalArgumentException.class, () -> new VocabularyThresholds(3, 0.3, 0, 0.25, 10, 0.5));
    assertThrows(
        IllegalArgumentException.class, () -> new VocabularyThresholds(3, 0.3, 10, 0.25, 0, 0.5));
  }

  @Test
  public void sharesMustBeBetweenZeroAndOne() {
    assertThrows(
        IllegalArgumentException.class, () -> new VocabularyThresholds(3, -0.1, 10, 0.25, 10, 0.5));
    assertThrows(
        IllegalArgumentException.class, () -> new VocabularyThresholds(3, 0.3, 10, 1.1, 10, 0.5));
    assertThrows(
        IllegalArgumentException.class,
        () -> new VocabularyThresholds(3, 0.3, 10, 0.25, 10, Double.NaN));
  }

  @Test
  public void boundaryValuesAreAllowed() {
    final VocabularyThresholds thresholds = new VocabularyThresholds(1, 0, 1, 1, 1, 1);
    assertThat(thresholds.prefixShare(), is(0.0));
    assertThat(thresholds.genericTableShare(), is(1.0));
  }
}
