/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline.utility;

import static com.typesafe.config.ConfigFactory.load;
import static com.typesafe.config.ConfigFactory.parseFileAnySyntax;
import static com.typesafe.config.ConfigFactory.parseResourcesAnySyntax;
import static com.typesafe.config.ConfigValueFactory.fromMap;
import static java.util.stream.Collectors.toMap;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigParseOptions;
import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
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
