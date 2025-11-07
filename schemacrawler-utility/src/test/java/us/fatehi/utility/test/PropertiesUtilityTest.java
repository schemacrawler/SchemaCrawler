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

import java.nio.file.Path;
import java.util.Properties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import us.fatehi.utility.PropertiesUtility;

public class PropertiesUtilityTest {

  @AfterEach
  public void clearSystemProperties() {
    System.setProperties(new Properties());
  }

  @Test
  public void noSystemConfigurationProperty() {
    final String value = PropertiesUtility.getSystemConfigurationProperty("key", "defaultValue");
    assertThat(value, is("defaultValue"));
  }

  @Test
  public void withBothConfigurationProperty() {
    System.setProperty("PATH", "value");
    final String value = PropertiesUtility.getSystemConfigurationProperty("PATH", "defaultValue");
    assertThat(value, is("value"));
  }

  @Test
  public void withNonStringSystemConfigurationProperty() {
    System.getProperties().put("key", Path.of("."));
    final String value = PropertiesUtility.getSystemConfigurationProperty("key", "defaultValue");
    assertThat(value, is("."));
  }

  @Test
  public void withEnvConfigurationProperty() {
    final String value = PropertiesUtility.getSystemConfigurationProperty("PATH", "defaultValue");
    assertThat(value, is(not(emptyString())));
  }

  @Test
  public void withSystemConfigurationProperty() {
    System.setProperty("key", "value");
    final String value = PropertiesUtility.getSystemConfigurationProperty("key", "defaultValue");
    assertThat(value, is("value"));
  }
}
