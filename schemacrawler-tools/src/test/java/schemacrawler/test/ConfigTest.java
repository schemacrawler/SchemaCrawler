package schemacrawler.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.nullValue;

import org.apache.commons.collections.keyvalue.DefaultMapEntry;
import org.junit.jupiter.api.Test;

import schemacrawler.tools.options.Config;

public class ConfigTest {

  @Test
  public void emptyConfig() {
    final Config config = new Config();

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

  @Test
  public void notEmptyConfig() {
    final Config config = new Config();
    config.put("key", "value");

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
