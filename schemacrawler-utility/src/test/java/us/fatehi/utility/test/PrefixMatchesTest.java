/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package us.fatehi.utility.test;

import static java.util.Objects.requireNonNull;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import us.fatehi.utility.PrefixMatches;

public class PrefixMatchesTest {

  @Test
  public void prefixMatches_boundaries() {
    PrefixMatches matchkeys;
    List<String> withoutPrefix;

    // 1.
    assertThrows(NullPointerException.class, () -> new PrefixMatches(null, "_"));

    // 2.
    matchkeys = new PrefixMatches(Collections.emptyList(), "_");
    assertThat(matchkeys.toString(), is("{}"));

    withoutPrefix = matchkeys.get("key0");
    assertThat(withoutPrefix, containsInAnyOrder("key0"));

    // 3.
    matchkeys = new PrefixMatches(keys("key1"), "_");
    assertThat(matchkeys.toString(), is("{key1=[key1]}"));

    withoutPrefix = matchkeys.get("key0");
    assertThat(withoutPrefix, containsInAnyOrder("key0"));

    withoutPrefix = matchkeys.get("key1");
    assertThat(withoutPrefix, containsInAnyOrder("key1"));
  }

  @Test
  public void prefixMatches_mixed_prefixes() {
    List<String> withoutPrefix;

    final PrefixMatches matchkeys =
        new PrefixMatches(keys("pfx1_pfx2_key1", "pfx1_pfx2_key2", "pfx1_key3"), "_");

    withoutPrefix = matchkeys.get("key0");
    assertThat(withoutPrefix, containsInAnyOrder("key0"));

    withoutPrefix = matchkeys.get("pfx1_pfx2_key1");
    assertThat(withoutPrefix, containsInAnyOrder("key1", "pfx2_key1", "pfx1_pfx2_key1"));

    withoutPrefix = matchkeys.get("pfx1_pfx2_key2");
    assertThat(withoutPrefix, containsInAnyOrder("key2", "pfx2_key2", "pfx1_pfx2_key2"));

    withoutPrefix = matchkeys.get("pfx1_key3");
    assertThat(withoutPrefix, containsInAnyOrder("key3", "pfx1_key3"));
  }

  @Test
  public void prefixMatches_no_prefix() {
    List<String> withoutPrefix;

    final PrefixMatches matchkeys = new PrefixMatches(keys("key1", "key2"), "_");

    withoutPrefix = matchkeys.get("key0");
    assertThat(withoutPrefix, containsInAnyOrder("key0"));

    withoutPrefix = matchkeys.get("key1");
    assertThat(withoutPrefix, containsInAnyOrder("key1"));

    withoutPrefix = matchkeys.get("key2");
    assertThat(withoutPrefix, containsInAnyOrder("key2"));
  }

  @Test
  public void prefixMatches_plurals() {
    List<String> withoutPrefix;

    final PrefixMatches matchkeys = new PrefixMatches(keys("cats", "buffaloes", "giraffes"), "_");

    withoutPrefix = matchkeys.get("key0");
    assertThat(withoutPrefix, containsInAnyOrder("key0"));

    withoutPrefix = matchkeys.get("cats");
    assertThat(withoutPrefix, containsInAnyOrder("cat"));

    withoutPrefix = matchkeys.get("buffaloes");
    assertThat(withoutPrefix, containsInAnyOrder("buffalo"));

    withoutPrefix = matchkeys.get("giraffes");
    assertThat(withoutPrefix, containsInAnyOrder("giraffe"));
  }

  @Test
  public void prefixMatches_same_prefixes() {
    List<String> withoutPrefix;

    final PrefixMatches matchkeys =
        new PrefixMatches(keys("pfx1_pfx2_key1", "pfx1_pfx2_key2", "pfx1_pfx2_key3"), "_");

    withoutPrefix = matchkeys.get("key0");
    assertThat(withoutPrefix, containsInAnyOrder("key0"));

    withoutPrefix = matchkeys.get("pfx1_pfx2_key1");
    assertThat(withoutPrefix, containsInAnyOrder("key1", "pfx2_key1", "pfx1_pfx2_key1"));

    withoutPrefix = matchkeys.get("pfx1_pfx2_key2");
    assertThat(withoutPrefix, containsInAnyOrder("key2", "pfx2_key2", "pfx1_pfx2_key2"));

    withoutPrefix = matchkeys.get("pfx1_pfx2_key3");
    assertThat(withoutPrefix, containsInAnyOrder("key3", "pfx2_key3", "pfx1_pfx2_key3"));
  }

  private List<String> keys(final String... keyNames) {
    requireNonNull(keyNames);
    return Arrays.asList(keyNames);
  }
}
