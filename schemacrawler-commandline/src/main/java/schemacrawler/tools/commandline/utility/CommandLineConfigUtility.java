/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.commandline.utility;

import static com.typesafe.config.ConfigFactory.load;
import static com.typesafe.config.ConfigFactory.parseFileAnySyntax;
import static com.typesafe.config.ConfigFactory.parseResourcesAnySyntax;
import static com.typesafe.config.ConfigValueFactory.fromMap;
import static java.util.stream.Collectors.toMap;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigParseOptions;

import java.util.logging.Logger;

public class CommandLineConfigUtility {

  private static final Logger LOGGER = Logger.getLogger(CommandLineConfigUtility.class.getName());

  public static Map<String, Object> loadConfig() {

    // Invalidate config caches, so config can be loaded multiple times in the shell
    ConfigFactory.invalidateCaches();

    final Config config = loadConfig("schemacrawler.config");
    final Config colormapConfig = loadConfig("schemacrawler.colormap");

    final Config totalConfig =
        config
            .withValue("schemacrawler.format.color_map", fromMap(colormapConfig.root().unwrapped()))
            .withFallback(load())
            .resolve();
    LOGGER.log(Level.FINE, () -> totalConfig.root().render());

    final Map<String, Object> configMap =
        totalConfig.entrySet().stream()
            .filter(entry -> entry.getValue() != null)
            .collect(toMap(Entry::getKey, entry -> entry.getValue().unwrapped()));

    return configMap;
  }

  private static Config loadConfig(final String baseName) {
    final ConfigParseOptions configParseOptions =
        ConfigParseOptions.defaults().setAllowMissing(true);
    final Config config =
        parseFileAnySyntax(new File(baseName), configParseOptions)
            .withFallback(parseResourcesAnySyntax(baseName, configParseOptions));
    LOGGER.log(Level.CONFIG, () -> config.root().render());
    return config;
  }

  private CommandLineConfigUtility() {
    // Prevent instantiation
  }
}
