/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbconnectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import schemacrawler.plugins.dbconnectors.model.AdditionalOptionDefinition;
import schemacrawler.plugins.dbconnectors.model.CommandlineOptionType;
import schemacrawler.plugins.dbconnectors.model.DatabaseConnectorDefinition;
import schemacrawler.plugins.dbconnectors.yaml.DatabasePluginYamlDeserializer;
import us.fatehi.utility.ioresource.FileInputResource;

class DatabasePluginYamlDeserializerTest {

  private final DatabasePluginYamlDeserializer deserializer = new DatabasePluginYamlDeserializer();

  @Test
  void fallsBackToUnknownOnConflictingLimit(@TempDir final Path tempDirectory) throws IOException {
    final String yaml =
        """
        database-connector:
          database-server-type:
            server: unit
            name: Unit DB
          url-template: "jdbc:unit:${database}"
          additional-options:
            - name: setting
              type: string
              default: value
          limit:
            include-schemas: "A"
            exclude-schemas: "B"
        """;

    final Path yamlFile = yamlFile(tempDirectory, yaml);
    final DatabaseConnectorDefinition definition =
        deserializer.parse(new FileInputResource(yamlFile));

    assertNull(definition.databaseServerType().server());
  }

  @Test
  void fallsBackToUnknownOnUnknownField(@TempDir final Path tempDirectory) throws IOException {
    final String yaml =
        """
        database-connector:
          database-server-type:
            server: unit
            name: Unit DB
          url-template: "jdbc:unit:${database}"
          unexpected: true
          additional-options:
            - name: setting
              type: String
              default: value
        """;

    final Path yamlFile = yamlFile(tempDirectory, yaml);
    final DatabaseConnectorDefinition definition =
        deserializer.parse(new FileInputResource(yamlFile));

    assertNull(definition.databaseServerType().server());
  }

  @Test
  void parsesValidYaml(@TempDir final Path tempDirectory) throws IOException {
    final String yaml =
        """
         database-connector:
           database-server-type:
             server: unit-test
             name: Unit Test DB
           url-template: "jdbc:unit://${host}:${port}/${database}"
           allowed-driver-properties:
             - setting
           standard-options:
             port:
               default: 1234
           additional-options:
             - name: setting
               type: string
               default: value
               help:
                 - Help!
           schema-retrieval:
             supports-catalogs: null
             supports-schemas: true
           limit:
             exclude-schemas: "INFORMATION_SCHEMA"
        """;

    final Path yamlFile = yamlFile(tempDirectory, yaml);
    final DatabaseConnectorDefinition definition =
        deserializer.parse(new FileInputResource(yamlFile));

    assertEquals("unit-test", definition.databaseServerType().server());
    assertEquals("Unit Test DB", definition.databaseServerType().name());
    assertEquals("jdbc:unit://${host}:${port}/${database}", definition.urlTemplate());

    assertEquals("1234", definition.standardOptions().port().stringDefault());

    final AdditionalOptionDefinition additionalOption = definition.additionalOptions().get(0);
    assertEquals("setting", additionalOption.name());
    assertEquals(CommandlineOptionType.STRING, additionalOption.type());
    assertEquals("value", additionalOption.defaultValue());
    assertEquals("value", additionalOption.stringDefault());

    assertNull(definition.schemaRetrieval().supportsCatalogs());
    assertEquals(Boolean.TRUE, definition.schemaRetrieval().supportsSchemas());
  }

  private Path yamlFile(final Path tempDirectory, final String yaml) throws IOException {
    final Path yamlFilePath = tempDirectory.resolve("plugin.yaml");
    Files.writeString(yamlFilePath, yaml);
    return yamlFilePath;
  }
}
