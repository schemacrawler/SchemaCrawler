/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbconnectors;

import static java.util.Objects.requireNonNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import schemacrawler.plugins.dbconnectors.model.DatabaseConnectorDefinition;
import schemacrawler.plugins.dbconnectors.yaml.DatabasePluginYamlDeserializer;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorOptions;
import schemacrawler.tools.registry.BasePluginRegistry;
import us.fatehi.utility.IOUtility;
import us.fatehi.utility.ioresource.ClasspathInputResource;
import us.fatehi.utility.ioresource.FileInputResource;
import us.fatehi.utility.property.PropertyName;

/** Loads bundled and filesystem YAML plugins. */
public final class MultiDatabaseConnectorRegistry extends BasePluginRegistry {

  private static final class SimpleDatabaseConnector extends DatabaseConnector {

    private static DatabaseConnectorOptions toOptions(
        final DatabaseConnectorDefinition definition) {
      requireNonNull(definition, "No database plugin definition provided");
      return new DatabaseConnectorDefinitionAdapter(definition).toDatabaseConnectorOptions();
    }

    public SimpleDatabaseConnector(final DatabaseConnectorDefinition definition) {
      super(toOptions(definition));
    }
  }

  private static final Logger LOGGER =
      Logger.getLogger(MultiDatabaseConnectorRegistry.class.getName());

  private static MultiDatabaseConnectorRegistry registrySingleton;

  private static final String CLASS_PATH_ROOT = "dbconnectors";

  private static final DatabasePluginYamlDeserializer DESERIALIZER =
      new DatabasePluginYamlDeserializer();

  public static MultiDatabaseConnectorRegistry getRegistry() {
    if (registrySingleton == null) {
      registrySingleton = new MultiDatabaseConnectorRegistry();
      registrySingleton.log();
    }
    return registrySingleton;
  }

  private static List<DatabaseConnectorDefinition> loadClasspathDefinitions() {
    final List<String> connectorResources =
        List.of(
            "access.yaml",
            "cassandra.yaml",
            "clickhouse.yaml",
            "duckdb.yaml",
            "h2.yaml",
            "snowflake.yaml",
            "trino.yaml");

    final List<DatabaseConnectorDefinition> result = new ArrayList<>();

    for (final String connectorResource : connectorResources) {
      final String resource = "%s/%s".formatted(CLASS_PATH_ROOT, connectorResource);
      final ClasspathInputResource inputResource = new ClasspathInputResource(resource);
      try {
        final DatabaseConnectorDefinition databaseConnectorDefinition =
            DESERIALIZER.parse(inputResource);
        result.add(databaseConnectorDefinition);
      } catch (final Exception e) {
        LOGGER.log(
            Level.WARNING,
            "Could not load connector definition from resource <%s>".formatted(inputResource),
            e);
      }
    }

    return result;
  }

  private static List<DatabaseConnectorDefinition> loadDirectoryDefinitions(final Path directory) {
    if (directory == null || !Files.isDirectory(directory)) {
      return List.of();
    }
    try (final Stream<Path> stream = Files.list(directory)) {
      return stream
          .filter(file -> "yaml".equals(IOUtility.getFileExtension(file)))
          .map(FileInputResource::new)
          .map(
              inputResource -> {
                try {
                  final DatabaseConnectorDefinition databaseConnectorDefinition =
                      DESERIALIZER.parse(inputResource);
                  return Optional.<DatabaseConnectorDefinition>of(databaseConnectorDefinition);
                } catch (final Exception e) {
                  LOGGER.log(
                      Level.WARNING,
                      "Could not load connector definition from resource <%s>"
                          .formatted(inputResource),
                      e);
                  return Optional.<DatabaseConnectorDefinition>empty();
                }
              })
          .flatMap(Optional::stream) // keep only present values
          .collect(Collectors.toList());
    } catch (final Exception e) {
      LOGGER.log(
          Level.WARNING,
          "Could not load database connectors from the directory <%s>".formatted(directory),
          e);
    }
    return List.of();
  }

  private static List<SimpleDatabaseConnector> loadSimpleDatabasePluginRegistry() {
    final List<DatabaseConnectorDefinition> loadedDefinitions =
        new ArrayList<>(loadClasspathDefinitions());
    loadedDefinitions.addAll(loadDirectoryDefinitions(Paths.get(".")));
    return toDatabaseConnectors(loadedDefinitions);
  }

  private static List<SimpleDatabaseConnector> toDatabaseConnectors(
      final List<DatabaseConnectorDefinition> loadedDefinitions) {
    final List<SimpleDatabaseConnector> databaseConnectors = new ArrayList<>();
    final Set<String> servers = new HashSet<>();
    for (final DatabaseConnectorDefinition definition : loadedDefinitions) {
      final String server = definition.databaseServerType().server();
      if (!servers.add(server)) {
        LOGGER.log(Level.WARNING, "Already loaded <%s>".formatted(server));
        continue;
      }
      databaseConnectors.add(new SimpleDatabaseConnector(definition));
    }
    return databaseConnectors;
  }

  private final List<SimpleDatabaseConnector> databaseConnectorRegistry;

  private MultiDatabaseConnectorRegistry() {
    super("Simple Database Plugins");
    databaseConnectorRegistry = loadSimpleDatabasePluginRegistry();
  }

  public Collection<DatabaseConnector> getDatabaseConnectors() {
    return List.copyOf(databaseConnectorRegistry);
  }

  @Override
  public Collection<PropertyName> getRegisteredPlugins() {
    final List<PropertyName> registeredPlugins = new ArrayList<>();
    for (final SimpleDatabaseConnector factory : databaseConnectorRegistry) {
      registeredPlugins.add(
          new PropertyName(
              factory.getDatabaseServerType().getDatabaseSystemIdentifier(),
              factory.getDatabaseServerType().getDatabaseSystemName()));
    }
    Collections.sort(registeredPlugins);
    return registeredPlugins;
  }
}
