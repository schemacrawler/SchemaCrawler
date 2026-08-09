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
import schemacrawler.scribe.okf.frontmatter.okf.GeneratedRecord;
import schemacrawler.scribe.okf.frontmatter.okf.LifecycleStatus;
import schemacrawler.scribe.okf.frontmatter.okf.TrustTier;
import schemacrawler.scribe.okf.frontmatter.okf.VerifiedRecord;
import tools.jackson.databind.JsonNode;

public class FrontMatterRecordValidationTest {

  @Test
  public void generatedRecordValidatesActorPrefix() {
    assertThrows(
        NullPointerException.class, () -> new GeneratedRecord(null, parse("2026-06-25T09:00:00Z")));
    assertDoesNotThrow(
        () -> new GeneratedRecord(new SchemaCrawlerActor(), parse("2026-06-25T09:00:00Z")));
  }

  @Test
  public void verifiedRecordValidatesPresence() {
    assertThrows(
        NullPointerException.class, () -> new VerifiedRecord(TrustTier.machine_confirmed, null));
    assertDoesNotThrow(
        () -> new VerifiedRecord(TrustTier.machine_confirmed, new SchemaCrawlerActor()));
  }

  @Test
  public void verifiedByRoundTripsFromString() {
    assertThat(TrustTier.fromString("machine-confirmed"), is(TrustTier.machine_confirmed));
    assertThrows(IllegalArgumentException.class, () -> TrustTier.fromString("bogus"));
  }

  @Test
  public void statusRoundTripsFromString() {
    assertThat(LifecycleStatus.fromString("stable"), is(LifecycleStatus.stable));
  }

  @Test
  public void recordsSerializeByAndAtOnly() {
    final GeneratedRecord generated =
        new GeneratedRecord(new SchemaCrawlerActor(), parse("2026-06-25T09:00:00Z"));
    final VerifiedRecord verified =
        new VerifiedRecord(TrustTier.machine_confirmed, new SchemaCrawlerActor());

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
