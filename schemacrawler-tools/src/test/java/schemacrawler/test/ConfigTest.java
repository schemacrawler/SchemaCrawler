package schemacrawler.test;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAndIs;
import static org.hamcrest.CoreMatchers.containsString;
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

import schemacrawler.inclusionrule.RegularExpressionRule;
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
  public void getBooleanValue() {
    final Config config = new Config();

    assertThat(config.getBooleanValue("key"), is(false));

    config.putBooleanValue("key", false);

    assertThat(config.getBooleanValue("key"), is(false));

    config.putBooleanValue("key", true);

    assertThat(config.getBooleanValue("key"), is(true));

    config.put("key", "blah");

    assertThat(config.getBooleanValue("key"), is(false));
  }

  @Test
  public void getDoubleValue() {
    final Config config = new Config();

    assertThat(config.getDoubleValue("key", -1), is(-1d));

    config.put("key", "1.1");

    assertThat(config.getDoubleValue("key", -1), is(1.1));

    config.put("key", "blah");

    assertThat(config.getDoubleValue("key", -1), is(-1d));
  }

  @Test
  public void getEnumValue() {
    final Config config = new Config();

    assertThat(config.getEnumValue("key", DayOfWeek.MONDAY), is(DayOfWeek.MONDAY));

    config.put("key", DayOfWeek.FRIDAY.name());

    assertThat(config.getEnumValue("key", DayOfWeek.MONDAY), is(DayOfWeek.FRIDAY));

    config.put("key", "blah");

    assertThat(config.getEnumValue("key", DayOfWeek.MONDAY), is(DayOfWeek.MONDAY));
  }

  @Test
  public void getIntegerValue() {
    final Config config = new Config();

    assertThat(config.getIntegerValue("key", -1), is(-1));

    config.put("key", "1");

    assertThat(config.getIntegerValue("key", -1), is(1));

    config.put("key", "1.1");

    assertThat(config.getIntegerValue("key", -1), is(-1));

    config.put("key", "blah");

    assertThat(config.getIntegerValue("key", -1), is(-1));
  }

  @Test
  public void getLongValue() {
    final Config config = new Config();

    assertThat(config.getLongValue("key", -1), is(-1L));

    config.put("key", "1");

    assertThat(config.getLongValue("key", -1), is(1L));

    config.put("key", "1.1");

    assertThat(config.getLongValue("key", -1), is(-1L));

    config.put("key", "blah");

    assertThat(config.getLongValue("key", -1), is(-1L));
  }

  @Test
  public void getOptionalInclusionRule() {
    final Config config = new Config();

    assertThat(config.getOptionalInclusionRule("in", "ex"), is(isEmpty()));

    config.putStringValue("in", ".*");
    config.putStringValue("ex", "exc");

    assertThat(
        config.getOptionalInclusionRule("in", "ex"),
        is(isPresentAndIs(new RegularExpressionRule(".*", "exc"))));

    config.putStringValue("in", ".*");
    config.putStringValue("ex", null);

    assertThat(
        config.getOptionalInclusionRule("in", "ex"),
        is(isPresentAndIs(new RegularExpressionRule(".*", null))));

    config.putStringValue("in", null);
    config.putStringValue("ex", "exc");

    assertThat(
        config.getOptionalInclusionRule("in", "ex"),
        is(isPresentAndIs(new RegularExpressionRule(null, "exc"))));
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
  public void putBooleanValue() {
    final Config config = new Config();

    assertThat(config.hasValue("key"), is(false));

    config.putBooleanValue("key", false);

    assertThat(config.get("key"), is(Boolean.FALSE.toString()));
  }

  @Test
  public void putEnumValue() {
    final Config config = new Config();

    assertThat(config.hasValue("key"), is(false));

    config.putEnumValue("key", DayOfWeek.MONDAY);

    assertThat(config.get("key"), is("MONDAY"));

    config.putEnumValue("key", null);

    assertThat(config.hasValue("key"), is(false));
  }

  @Test
  public void putStringValue() {
    final Config config = new Config();

    assertThat(config.hasValue("key"), is(false));

    config.putStringValue("key", "value");

    assertThat(config.get("key"), is("value"));

    config.putStringValue("key", null);

    assertThat(config.hasValue("key"), is(false));
  }

  private void assertEmptyConfig(final Config config) {
    assertThat(config.size(), is(0));
    assertThat(config.isEmpty(), is(true));
    assertThat(config.toString(), containsString("{}"));

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
    assertThat(config.toString(), containsString("{key=value}" + System.lineSeparator()));

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
