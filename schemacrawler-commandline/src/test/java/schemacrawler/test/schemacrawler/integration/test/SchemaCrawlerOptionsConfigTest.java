/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.test.schemacrawler.integration.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.LimitOptions;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.tools.commandline.utility.SchemaCrawlerOptionsConfig;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.PropertiesUtility;
import us.fatehi.utility.ioresource.ClasspathInputResource;

public class SchemaCrawlerOptionsConfigTest {

  @Test
  public void limitOptions() {
    final LimitOptionsBuilder builder = LimitOptionsBuilder.builder();
    final Config config = new Config(loadConfig("/limit.config.properties"));

    SchemaCrawlerOptionsConfig.fromConfig(builder, config);

    final LimitOptions limitOptions = builder.toOptions();

    assertThat(limitOptions.getTableTypes().toString(), is("[other table]"));
  }

  private Map<String, String> loadConfig(final String configResource) {
    try {
      final Properties properties =
          TestUtility.loadProperties(new ClasspathInputResource(configResource));
      return PropertiesUtility.propertiesMap(properties);
    } catch (final IOException e) {
      fail("Could not load " + configResource, e);
      return null;
    }
  }
}
