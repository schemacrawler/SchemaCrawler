package schemacrawler.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.nullValue;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.keyvalue.DefaultMapEntry;
import org.junit.jupiter.api.Test;

import schemacrawler.tools.options.Config;

public class ConfigTest {

  @Test
  public void emptyConfig() {
    final Config config = new Config();
    assertEmptyConfig(config);
  }

  @Test
  public void emptyConfig1() {
    final Config config = new Config(new HashMap<>());
    assertEmptyConfig(config);
  }

  @Test
  public void emptyConfig1a() {
    final Config config = new Config((Config) null);
    assertEmptyConfig(config);
  }

  @Test
  public void emptyConfig2() {
    final Config config = new Config(new Config());
    assertEmptyConfig(config);
  }

  @Test
  public void emptyConfig2b() {
    final Config config = new Config((Map) null);
    assertEmptyConfig(config);
  }

  @Test
  public void notEmptyConfig() {
    final Config config = new Config();

    config.put("key", "value");
    assertNotEmptyConfig(config);

    config.remove("key");
    assertEmptyConfig(config);

    config.put("key", "value");
    assertNotEmptyConfig(config);

    config.clear();
    assertEmptyConfig(config);
  }

  @Test
  public void notEmptyConfig1() {
    final Map<String, Object> map = new HashMap<>();
    map.put("key", "value");

    final Config config = new Config(map);
    assertNotEmptyConfig(config);
  }

  @Test
  public void notEmptyConfig2() {
    final Config map = new Config();
    map.put("key", "value");

    final Config config = new Config(map);
    assertNotEmptyConfig(config);
  }

  @Test
  public void setBooleanValue() {
    final Config config = new Config();

    assertThat(config.hasValue("key"), is(false));

    config.setBooleanValue("key", false);

    assertThat(config.get("key"), is(Boolean.FALSE.toString()));
  }

  @Test
  public void setEnumValue() {
    final Config config = new Config();

    assertThat(config.hasValue("key"), is(false));

    config.setEnumValue("key", DayOfWeek.MONDAY);

    assertThat(config.get("key"), is("MONDAY"));

    config.setEnumValue("key", null);

    assertThat(config.hasValue("key"), is(false));
  }

  @Test
  public void setStringValue() {
    final Config config = new Config();

    assertThat(config.hasValue("key"), is(false));

    config.setStringValue("key", "value");

    assertThat(config.get("key"), is("value"));

    config.setStringValue("key", null);

    assertThat(config.hasValue("key"), is(false));
  }

  private void assertEmptyConfig(final Config config) {
    assertThat(config.size(), is(0));
    assertThat(config.isEmpty(), is(true));
    assertThat(config.toString(), is(""));

    assertThat(config.hasValue("key"), is(false));
    assertThat(config.get("key"), is(nullValue()));
    assertThat(config.containsKey("key"), is(false));
    assertThat(config.containsValue("value"), is(false));

    assertThat(config.keySet(), empty());
    assertThat(config.values(), empty());
    assertThat(config.entrySet(), empty());
  }

  private void assertNotEmptyConfig(final Config config) {
    assertThat(config.size(), is(1));
    assertThat(config.isEmpty(), is(false));
    assertThat(config.toString(), is(System.lineSeparator() + "key: value"));

    assertThat(config.hasValue("key"), is(true));
    assertThat(config.get("key"), is("value"));
    assertThat(config.containsKey("key"), is(true));
    assertThat(config.containsValue("value"), is(true));

    assertThat(config.hasValue("key1"), is(false));
    assertThat(config.get("key1"), is(nullValue()));
    assertThat(config.containsKey("key1"), is(false));
    assertThat(config.containsValue("value1"), is(false));

    assertThat(config.keySet(), containsInAnyOrder("key"));
    assertThat(config.values(), containsInAnyOrder("value"));
    assertThat(config.entrySet(), containsInAnyOrder(new DefaultMapEntry("key", "value")));
  }
}
