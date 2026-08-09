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
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.scribe.renderer.JsonUtility.yamlMapper;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;

public class FrontMatterRecordValidationTest {

  @Test
  public void generatedRecordValidatesActorPrefix() {
    assertThrows(
        NullPointerException.class,
        () -> new OkfGeneratedRecord(null, parse("2026-06-25T09:00:00Z")));
    assertDoesNotThrow(
        () -> new OkfGeneratedRecord(new SchemaCrawlerActor(), parse("2026-06-25T09:00:00Z")));
  }

  @Test
  public void verifiedRecordValidatesPresence() {
    assertThrows(
        NullPointerException.class,
        () -> new OkfVerifiedRecord(OkfTrustTier.machine_confirmed, null));
    assertDoesNotThrow(
        () -> new OkfVerifiedRecord(OkfTrustTier.machine_confirmed, new SchemaCrawlerActor()));
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

  @Test
  public void recordsSerializeByAndAtOnly() {
    final OkfGeneratedRecord generated =
        new OkfGeneratedRecord(new SchemaCrawlerActor(), parse("2026-06-25T09:00:00Z"));
    final OkfVerifiedRecord verified =
        new OkfVerifiedRecord(OkfTrustTier.machine_confirmed, new SchemaCrawlerActor());

    final JsonNode generatedJson = yamlMapper.valueToTree(generated);
    final JsonNode verifiedJson = yamlMapper.valueToTree(verified);

    assertThat(yamlMapper.treeToValue(generatedJson.get("by"), String.class), is(notNullValue()));
    assertThat(generatedJson.get("at"), is(notNullValue()));
    assertThat(generatedJson.get("actor"), is(nullValue()));

    assertThat(yamlMapper.treeToValue(verifiedJson.get("by"), String.class), is(notNullValue()));
    assertThat(verifiedJson.get("at"), is(notNullValue()));
    assertThat(verifiedJson.get("trust_tier"), is(nullValue()));
    assertThat(verifiedJson.get("actor"), is(nullValue()));
  }
}
