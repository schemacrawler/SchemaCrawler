/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import us.fatehi.utility.readconfig.SystemPropertiesConfig;

public class SystemPropertiesTest {

  @AfterEach
  public void clearSystemProperties() {
    // Setting to null restores System properties
    System.setProperties(null);
  }

  @Test
  public void noSystemConfigurationProperty() {
    final String value = new SystemPropertiesConfig().getStringValue("key", "defaultValue");
    assertThat(value, is("defaultValue"));
  }

  @Test
  public void withNonStringSystemConfigurationProperty() {
    final String key = "PATH";
    System.getProperties().put(key, Path.of("."));
    final Object objectValue = System.getProperties().get(key);
    assertThat(objectValue.toString(), is("."));
    final String value = new SystemPropertiesConfig().getStringValue(key, "defaultValue");
    assertThat(value, is("."));
  }

  @Test
  public void withSystemConfigurationProperty() {
    System.setProperty("key", "value");
    final String value = new SystemPropertiesConfig().getStringValue("key", "defaultValue");
    assertThat(value, is("value"));
  }
}
