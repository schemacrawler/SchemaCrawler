/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.not;

import java.util.Map;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import us.fatehi.utility.PropertiesUtility;

public class PropertiesUtilityTest {

  @Test
  public void emptyPropertiesMap() {
    final Map<String, String> propertiesMap1 = PropertiesUtility.propertiesMap(new Properties());
    assertThat(propertiesMap1.isEmpty(), is(true));
  }

  @Test
  public void noSystemConfigurationProperty() {
    final String value = PropertiesUtility.getSystemConfigurationProperty("key", "defaultValue");
    assertThat(value, is("defaultValue"));
  }

  @Test
  public void nullPropertiesMap() {
    final Map<String, String> propertiesMap1 = PropertiesUtility.propertiesMap(null);
    assertThat(propertiesMap1.isEmpty(), is(true));
  }

  @Test
  public void withBothConfigurationProperty() {
    System.setProperty("PATH", "value");
    final String value = PropertiesUtility.getSystemConfigurationProperty("PATH", "defaultValue");
    assertThat(value, is("value"));

    System.clearProperty("PATH");
  }

  @Test
  public void withEnvConfigurationProperty() {
    final String value = PropertiesUtility.getSystemConfigurationProperty("PATH", "defaultValue");
    assertThat(value, is(not(emptyString())));
  }

  @Test
  public void withPropertiesMap() {
    final Properties properties = new Properties();
    properties.setProperty("key", "value");
    final Map<String, String> propertiesMap1 = PropertiesUtility.propertiesMap(properties);
    assertThat(propertiesMap1.size(), is(1));
    assertThat(propertiesMap1.get("key"), is("value"));
  }

  @Test
  public void withSystemConfigurationProperty() {
    System.setProperty("key", "value");
    final String value = PropertiesUtility.getSystemConfigurationProperty("key", "defaultValue");
    assertThat(value, is("value"));

    System.clearProperty("key");
  }
}
