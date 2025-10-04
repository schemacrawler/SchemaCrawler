/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.lint.config;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.Reader;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schemacrawler.exceptions.ConfigurationException;
import schemacrawler.tools.command.lint.options.LintOptions;
import us.fatehi.utility.UtilityMarker;
import us.fatehi.utility.ioresource.InputResource;
import us.fatehi.utility.ioresource.InputResourceUtility;
import us.fatehi.utility.string.StringFormat;

@UtilityMarker
public final class LinterConfigUtility {

  public static final Logger LOGGER = Logger.getLogger(LinterConfigUtility.class.getName());

  /**
   * Obtain linter configuration from a system property
   *
   * @return LinterConfigs
   */
  public static LinterConfigs readLinterConfigs(final LintOptions lintOptions) {
    final LinterConfigs linterConfigs = new LinterConfigs(lintOptions.getConfig());
    final String linterConfigsFile = lintOptions.getLinterConfigs();
    if (!isBlank(linterConfigsFile)) {
      final InputResource inputResource =
          InputResourceUtility.createInputResource(linterConfigsFile)
              .orElseThrow(
                  () ->
                      new ConfigurationException(
                          "Could not load linter configs from file <%s>"
                              .formatted(linterConfigsFile)));
      try (final Reader reader = inputResource.openNewInputReader(UTF_8)) {
        final List<LinterConfig> linterConfigsList = readLinterConfigs(reader);
        for (final LinterConfig linterConfig : linterConfigsList) {
          linterConfigs.add(linterConfig);
        }
      } catch (final Exception e) {
        throw new ConfigurationException(
            "Could not load linter configs from file <%s>".formatted(linterConfigsFile), e);
      }
    }
    return linterConfigs;
  }

  private static List<LinterConfig> readLinterConfigs(final Reader reader) {
    requireNonNull(reader, "No input provided");

    try {
      final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
      final CollectionType listType =
          mapper.getTypeFactory().constructCollectionType(List.class, LinterConfig.class);

      final List<LinterConfig> linterConfigs = mapper.readValue(reader, listType);
      Collections.sort(linterConfigs);
      LOGGER.log(Level.CONFIG, new StringFormat("Read <%d> linter configs", linterConfigs.size()));

      return linterConfigs;
    } catch (final Exception e) {
      throw new ConfigurationException("Could not read linter configs", e);
    }
  }

  private LinterConfigUtility() {}
}
