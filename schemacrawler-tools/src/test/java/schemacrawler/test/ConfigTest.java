/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAndIs;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;

import java.time.DayOfWeek;
import java.util.Map;
import org.junit.jupiter.api.Test;
import schemacrawler.inclusionrule.RegularExpressionRule;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.ConfigUtility;

public class ConfigTest {

  @Test
  public void emptyConfig() {
    final Config config = ConfigUtility.newConfig();
    assertEmptyConfig(config);
  }

  @Test
  public void emptyConfig1a() {
    final Config config = ConfigUtility.fromConfig((Config) null);
    assertEmptyConfig(config);
  }

  @Test
  public void emptyConfig2() {
    final Config config = ConfigUtility.fromConfig(ConfigUtility.newConfig());
    assertEmptyConfig(config);
  }

  @Test
  public void emptyConfig2b() {
    final Config config = ConfigUtility.fromMap((Map<String, Object>) null);
    assertEmptyConfig(config);
  }

  @Test
  public void getBooleanValue() {
    final Config config = ConfigUtility.newConfig();

    assertThat(config.getBooleanValue("key"), is(false));

    config.put("key", "false");
    assertThat(config.getBooleanValue("key"), is(false));

    config.put("key", false);
    assertThat(config.getBooleanValue("key"), is(false));

    config.put("key", "true");
    assertThat(config.getBooleanValue("key"), is(true));

    config.put("key", true);
    assertThat(config.getBooleanValue("key"), is(true));

    config.put("key", "blah");
    assertThat(config.getBooleanValue("key"), is(false));
  }

  @Test
  public void getEnumValue() {
    final Config config = ConfigUtility.newConfig();

    assertThat(config.getEnumValue("key", DayOfWeek.MONDAY), is(DayOfWeek.MONDAY));

    config.put("key", DayOfWeek.FRIDAY);
    assertThat(config.getEnumValue("key", DayOfWeek.MONDAY), is(DayOfWeek.FRIDAY));

    config.put("key", DayOfWeek.FRIDAY.name());
    assertThat(config.getEnumValue("key", DayOfWeek.MONDAY), is(DayOfWeek.FRIDAY));

    config.put("key", "blah");
    assertThat(config.getEnumValue("key", DayOfWeek.MONDAY), is(DayOfWeek.MONDAY));
  }

  @Test
  public void getIntegerValue() {
    final Config config = ConfigUtility.newConfig();

    assertThat(config.getIntegerValue("key", -1), is(-1));

    config.put("key", 1);
    assertThat(config.getIntegerValue("key", -1), is(1));

    config.put("key", "1");
    assertThat(config.getIntegerValue("key", -1), is(1));

    config.put("key", "1.1");
    assertThat(config.getIntegerValue("key", -1), is(-1));

    config.put("key", "blah");
    assertThat(config.getIntegerValue("key", -1), is(-1));
  }

  @Test
  public void getOptionalInclusionRule() {
    final Config config = ConfigUtility.newConfig();

    assertThat(config.getOptionalInclusionRule("in", "ex"), is(isEmpty()));

    config.put("in", ".*");
    config.put("ex", "exc");

    assertThat(
        config.getOptionalInclusionRule("in", "ex"),
        is(isPresentAndIs(new RegularExpressionRule(".*", "exc"))));

    config.put("in", ".*");
    config.put("ex", null);

    assertThat(
        config.getOptionalInclusionRule("in", "ex"),
        is(isPresentAndIs(new RegularExpressionRule(".*", null))));

    config.put("in", null);
    config.put("ex", "exc");

    assertThat(
        config.getOptionalInclusionRule("in", "ex"),
        is(isPresentAndIs(new RegularExpressionRule(null, "exc"))));
  }

  @Test
  public void notEmptyConfig() {
    final Config config = ConfigUtility.newConfig();

    config.put("key", "value");
    assertNotEmptyConfig(config);
  }

  @Test
  public void notEmptyConfig1() {
    final Config config = ConfigUtility.fromMap(Map.of("key", "value"));
    assertNotEmptyConfig(config);
  }

  @Test
  public void notEmptyConfig2() {
    final Config map = ConfigUtility.newConfig();
    map.put("key", "value");

    final Config config = ConfigUtility.fromConfig(map);
    assertNotEmptyConfig(config);
  }

  @Test
  public void putEnumValue() {
    final Config config = ConfigUtility.newConfig();

    assertThat(config.containsKey("key"), is(false));

    config.put("key", DayOfWeek.MONDAY);

    assertThat(config.getStringValue("key", "<unknown>"), is("MONDAY"));

    config.put("key", null);

    assertThat(config.containsKey("key"), is(false));
  }

  @Test
  public void putStringValue() {
    final Config config = ConfigUtility.newConfig();

    assertThat(config.containsKey("key"), is(false));

    config.put("key", "value");

    assertThat(config.getStringValue("key", "<unknown>"), is("value"));

    config.put("key", null);

    assertThat(config.containsKey("key"), is(false));
  }

  private void assertEmptyConfig(final Config config) {
    assertThat(config.size(), is(0));
    assertThat(config.toString().replaceAll("\\R", ""), is("{}"));

    assertThat(config.containsKey("key"), is(false));
    assertThat(config.getStringValue("key", null), is(nullValue()));
  }

  private void assertNotEmptyConfig(final Config config) {
    assertThat(config.size(), is(1));
    assertThat(config.toString().replaceAll("\\R", ""), is("{  \"key\": \"value\"}"));

    assertThat(config.containsKey("key"), is(true));
    assertThat(config.getStringValue("key", null), is("value"));

    assertThat(config.containsKey("key1"), is(false));
    assertThat(config.getStringValue("key1", null), is(nullValue()));
  }
}
