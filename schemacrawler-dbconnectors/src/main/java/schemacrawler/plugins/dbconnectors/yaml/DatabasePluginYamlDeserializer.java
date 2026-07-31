/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbconnectors.yaml;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.plugins.dbconnectors.model.DatabaseConnectorDefinition;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;
import us.fatehi.utility.ioresource.InputResource;
import us.fatehi.utility.string.StringFormat;

/** Parses YAML plugin definitions. */
public final class DatabasePluginYamlDeserializer {

  @JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
  private static record DatabaseConnectorDefinitionHolder(
      DatabaseConnectorDefinition databaseConnector) {}

  private static final Logger LOGGER =
      Logger.getLogger(DatabasePluginYamlDeserializer.class.getName());

  public DatabaseConnectorDefinition parse(final InputResource inputResource) {
    requireNonNull(inputResource, "No input resource provided");
    LOGGER.log(Level.FINE, new StringFormat("Parsing <%s>", inputResource));
    try (final Reader reader = inputResource.openNewInputReader(UTF_8)) {
      final DatabaseConnectorDefinitionHolder definition =
          JsonUtility.mapper
              .readerFor(DatabaseConnectorDefinitionHolder.class)
              .with(FAIL_ON_UNKNOWN_PROPERTIES)
              .readValue(reader);
      return definition.databaseConnector();
    } catch (final Exception e) {
      LOGGER.log(
          Level.WARNING,
          "Could not read database connector definition from <%s>".formatted(inputResource),
          e);
      return new DatabaseConnectorDefinition();
    }
  }
}
