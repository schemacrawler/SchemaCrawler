/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.commandline.utility;

import static java.nio.charset.StandardCharsets.UTF_8;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptionsBuilder;

public final class OutputOptionsConfig {

  private static final String SC_INPUT_ENCODING = "schemacrawler.encoding.input";
  private static final String SC_OUTPUT_ENCODING = "schemacrawler.encoding.output";

  public static OutputOptionsBuilder fromConfig(
      final OutputOptionsBuilder providedBuilder, final Config config) {
    final OutputOptionsBuilder builder;
    if (providedBuilder == null) {
      builder = OutputOptionsBuilder.builder();
    } else {
      builder = providedBuilder;
    }

    final Config configProperties;
    if (config == null) {
      configProperties = new Config();
    } else {
      configProperties = config;
    }

    builder
        .withInputEncoding(configProperties.getStringValue(SC_INPUT_ENCODING, UTF_8.name()))
        .withOutputEncoding(configProperties.getStringValue(SC_OUTPUT_ENCODING, UTF_8.name()));

    return builder;
  }
}
