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
import static us.fatehi.utility.ioresource.PropertiesMap.empty;
import static us.fatehi.utility.ioresource.PropertiesMap.fromProperties;

import java.util.Map;
import java.util.Properties;
import org.junit.jupiter.api.Test;

public class PropertiesMapTest {

  @Test
  public void emptyPropertiesMap() {
    final Map<String, String> propertiesMap1 = empty().toMap();
    assertThat(propertiesMap1.isEmpty(), is(true));
  }

  @Test
  public void withPropertiesMap() {
    final Properties properties = new Properties();
    properties.setProperty("key", "value");
    final Map<String, String> propertiesMap1 = fromProperties(properties).toMap();
    assertThat(propertiesMap1.size(), is(1));
    assertThat(propertiesMap1.get("key"), is("value"));
  }
}
