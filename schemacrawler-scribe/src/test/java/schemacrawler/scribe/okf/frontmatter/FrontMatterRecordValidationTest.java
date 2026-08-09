/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf.frontmatter;

import static java.time.Instant.parse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class FrontMatterRecordValidationTest {

  @Test
  public void generatedRecordValidatesActorPrefix() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new OkfGeneratedRecord(" ", parse("2026-06-25T09:00:00Z")));
    assertDoesNotThrow(
        () ->
            new OkfGeneratedRecord(
                SchemaCrawlerActor.schemaCrawlerActor(), parse("2026-06-25T09:00:00Z")));
  }

  @Test
  public void verifiedRecordValidatesPresence() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new OkfVerifiedRecord(" ", parse("2026-06-25T09:00:00Z")));
    assertDoesNotThrow(
        () ->
            new OkfVerifiedRecord(
                OkfTrustTier.machine_confirmed, SchemaCrawlerActor.schemaCrawlerActor()));
  }

  @Test
  public void verifiedByRoundTripsFromString() {
    assertThat(OkfTrustTier.fromString("machine-confirmed"), is(OkfTrustTier.machine_confirmed));
    assertThrows(IllegalArgumentException.class, () -> OkfTrustTier.fromString("bogus"));
  }

  @Test
  public void statusRoundTripsFromString() {
    assertThat(OkfStatus.fromString("stable"), is(OkfStatus.stable));
  }
}
