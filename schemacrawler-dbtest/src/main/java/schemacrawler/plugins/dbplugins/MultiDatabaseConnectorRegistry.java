/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbplugins;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.requireNotBlank;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import schemacrawler.plugins.dbplugins.model.DatabaseConnectorDefinition;
import schemacrawler.plugins.dbplugins.yaml.DatabasePluginYamlDeserializer;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.registry.BasePluginRegistry;
import us.fatehi.utility.ioresource.ClasspathInputResource;
import us.fatehi.utility.ioresource.FileInputResource;
import us.fatehi.utility.property.PropertyName;

/** Loads bundled and filesystem YAML plugins. */
public final class MultiDatabaseConnectorRegistry extends BasePluginRegistry {

  private record LoadedConnectorDefinition(
      String sourceDescription, DatabaseConnectorDefinition definition) {

    public LoadedConnectorDefinition {
      requireNotBlank(sourceDescription, "No source description provided");
      requireNonNull(definition, "No definition provided");
    }

    public boolean isValid() {
      return !"unknown".equals(definition.databaseServerType().server());
    }
  }

  private static final class SimpleDatabaseConnector extends DatabaseConnector {

    public SimpleDatabaseConnector(final DatabaseConnectorDefinition definition) {
      super(
          DatabaseConnectorDefinitionAdapter.toDatabaseConnectorOptions(
              requireNonNull(definition, "No database plugin definition provided")));
    }
  }

  private static final Logger LOGGER =
      Logger.getLogger(MultiDatabaseConnectorRegistry.class.getName());

  private static MultiDatabaseConnectorRegistry singleton;
  private static final String CLASS_PATH_ROOT = "schemacrawler-dbplugins";

  private static final DatabasePluginYamlDeserializer DESERIALIZER =
      new DatabasePluginYamlDeserializer();

  public static MultiDatabaseConnectorRegistry getInstance() {
    if (singleton == null) {
      singleton = new MultiDatabaseConnectorRegistry();
      singleton.log();
    }
    return singleton;
  }

  private static List<LoadedConnectorDefinition> loadClasspathDefinitions() {
    final List<String> connectorResources =
        List.of(
            "access.yaml",
            "cassandra.yaml",
            "clickhouse.yaml",
            "duckdb.yaml",
            "h2.yaml",
            "snowflake.yaml",
            "trino.yaml");
    try (final Stream<String> stream = connectorResources.stream()) {
      return stream
          .map(resource -> "%s/%s".formatted(CLASS_PATH_ROOT, resource))
          .map(ClasspathInputResource::new)
          .map(
              inputResource ->
                  new LoadedConnectorDefinition(
                      inputResource.toString(), DESERIALIZER.parse(inputResource)))
          .filter(LoadedConnectorDefinition::isValid)
          .collect(Collectors.toList());
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not load database connectors from the classpath", e);
    }
    return List.of();
  }

  private static List<LoadedConnectorDefinition> loadDirectoryDefinitions(final Path directory) {
    final List<LoadedConnectorDefinition> loadedDefinitions = new ArrayList<>();
    if (directory == null || !Files.isDirectory(directory)) {
      return loadedDefinitions;
    }
    try (final Stream<Path> stream = Files.list(directory)) {
      return stream
          .map(FileInputResource::new)
          .map(
              inputResource ->
                  new LoadedConnectorDefinition(
                      inputResource.toString(), DESERIALIZER.parse(inputResource)))
          .filter(LoadedConnectorDefinition::isValid)
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
    final List<LoadedConnectorDefinition> loadedDefinitions =
        new ArrayList<>(loadClasspathDefinitions());
    loadedDefinitions.addAll(loadDirectoryDefinitions(Paths.get(".")));
    return toDatabaseConnectors(loadedDefinitions);
  }

  private static List<SimpleDatabaseConnector> toDatabaseConnectors(
      final List<LoadedConnectorDefinition> loadedDefinitions) {
    final List<SimpleDatabaseConnector> databaseConnectors = new ArrayList<>();
    final Set<String> servers = new HashSet<>();
    for (final LoadedConnectorDefinition loadedDefinition : loadedDefinitions) {
      final DatabaseConnectorDefinition definition = loadedDefinition.definition();
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
