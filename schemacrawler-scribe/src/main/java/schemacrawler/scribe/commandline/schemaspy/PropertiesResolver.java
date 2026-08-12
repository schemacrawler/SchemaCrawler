/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.commandline.schemaspy;

import static us.fatehi.utility.Utility.isBlank;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

final class PropertiesResolver {
  private static final String PROPERTY_PREFIX = "schemaspy.";

  private record OptionSpec(String propertyKey, boolean takesValue, String... names) {}

  private static final List<OptionSpec> PROPERTY_OPTION_SPECS =
      List.of(
          new OptionSpec("dbhelp", false, "-dbhelp", "-dbHelp"),
          new OptionSpec("configfile", true, "-configFile"),
          new OptionSpec("o", true, "-o"),
          new OptionSpec("t", true, "-t"),
          new OptionSpec("db", true, "-db"),
          new OptionSpec("host", true, "-host"),
          new OptionSpec("port", true, "-port"),
          new OptionSpec("u", true, "-u"),
          new OptionSpec("p", true, "-p"),
          new OptionSpec("password", true, "-password"),
          new OptionSpec("pfp", false, "-pfp"),
          new OptionSpec("sso", false, "-sso"),
          new OptionSpec("connprops", true, "-connprops"),
          new OptionSpec("dp", true, "-dp"),
          new OptionSpec("loadjars", false, "-loadjars"),
          new OptionSpec("cat", true, "-cat"),
          new OptionSpec("s", true, "-s"),
          new OptionSpec("schemas", true, "-schemas"),
          new OptionSpec("all", false, "-all"),
          new OptionSpec("schemaspec", true, "-schemaSpec"),
          new OptionSpec("dbthreads", true, "-dbthreads"),
          new OptionSpec("norows", false, "-norows"),
          new OptionSpec("noviews", false, "-noviews"),
          new OptionSpec("i", true, "-i"),
          new OptionSpec("include", true, "-i"),
          new OptionSpec("exclude", true, "-I"),
          new OptionSpec("meta", true, "-meta"),
          new OptionSpec("nohtml", false, "-nohtml"),
          new OptionSpec("noviz", false, "-noviz"),
          new OptionSpec("loglevel", true, "-loglevel"),
          new OptionSpec("debug", false, "-debug"),
          new OptionSpec("locale", true, "--locale"));

  private static final Map<String, OptionSpec> PROPERTY_OPTION_BY_KEY =
      PROPERTY_OPTION_SPECS.stream()
          .collect(
              Collectors.toMap(
                  spec -> spec.propertyKey, spec -> spec, (a, b) -> a, LinkedHashMap::new));

  static String[] resolveEffectiveArgs(final String... args) {
    final Path configFile = resolveConfigFile(args);
    if (configFile == null) {
      return args;
    }

    final List<String> propertyArgs = readArgumentsFromProperties(configFile, args);
    if (propertyArgs.isEmpty()) {
      return args;
    }

    final List<String> effectiveArgs = new ArrayList<>(propertyArgs.size() + args.length);
    effectiveArgs.addAll(propertyArgs);
    effectiveArgs.addAll(Arrays.asList(args));
    return effectiveArgs.toArray(String[]::new);
  }

  private static String extractOptionName(final String argument) {
    if (argument == null || !argument.startsWith("-")) {
      return null;
    }
    final int equalsIndex = argument.indexOf('=');
    if (equalsIndex > 0) {
      return argument.substring(0, equalsIndex);
    }
    return argument;
  }

  private static String extractOptionValue(final String[] args, final String optionName) {
    for (int i = 0; i < args.length; i++) {
      final String argument = args[i];
      if (argument == null) {
        continue;
      }
      if (argument.startsWith(optionName + "=")) {
        return argument.substring((optionName + "=").length());
      }
      if (argument.equals(optionName) && i + 1 < args.length && !args[i + 1].startsWith("-")) {
        return args[i + 1];
      }
    }
    return null;
  }

  private static boolean hasCliOverride(final Set<String> cliOptions, final OptionSpec optionSpec) {
    return Arrays.stream(optionSpec.names).anyMatch(cliOptions::contains);
  }

  private static boolean isTrueValue(final String value, final String key) {
    if ("true".equalsIgnoreCase(value)) {
      return true;
    }
    if ("false".equalsIgnoreCase(value)) {
      return false;
    }
    throw new IllegalArgumentException(
        "Invalid boolean value <%s> for property <%s>".formatted(value, key));
  }

  private static List<String> readArgumentsFromProperties(
      final Path configFile, final String[] cliArgs) {
    final Properties properties = new Properties();
    try (InputStream inputStream = Files.newInputStream(configFile)) {
      properties.load(inputStream);
    } catch (IOException e) {
      throw new IllegalArgumentException(
          "Could not read properties from <%s>".formatted(configFile), e);
    }

    final Set<String> cliOptions =
        Arrays.stream(cliArgs)
            .map(PropertiesResolver::extractOptionName)
            .filter(name -> name != null && name.startsWith("-"))
            .collect(Collectors.toSet());

    final List<String> propertyArgs = new ArrayList<>();
    final Map<String, String> normalizedProperties = new LinkedHashMap<>();
    for (String key : properties.stringPropertyNames()) {
      normalizedProperties.put(normalizePropertyKey(key), properties.getProperty(key));
    }

    for (OptionSpec optionSpec : PROPERTY_OPTION_SPECS) {
      final String rawValue = normalizedProperties.get(optionSpec.propertyKey);
      if (rawValue == null || hasCliOverride(cliOptions, optionSpec)) {
        continue;
      }
      final String normalizedValue = rawValue.trim();
      if (isBlank(normalizedValue)) {
        continue;
      }
      if (optionSpec.takesValue) {
        propertyArgs.add(optionSpec.names[0]);
        propertyArgs.add(normalizedValue);
      } else if (isTrueValue(normalizedValue, optionSpec.propertyKey)) {
        propertyArgs.add(optionSpec.names[0]);
      }
    }

    for (String key : normalizedProperties.keySet()) {
      if (!PROPERTY_OPTION_BY_KEY.containsKey(key)) {
        System.err.println("Ignoring unsupported property key <" + key + ">");
      }
    }
    return propertyArgs;
  }

  private static Path resolveConfigFile(final String[] args) {
    final String configFilePath = extractOptionValue(args, "-configFile");
    if (!isBlank(configFilePath)) {
      final Path explicitConfigPath = Paths.get(configFilePath).toAbsolutePath().normalize();
      if (!Files.isRegularFile(explicitConfigPath)) {
        throw new IllegalArgumentException(
            "Could not find schemaspy properties file <%s>".formatted(explicitConfigPath));
      }
      return explicitConfigPath;
    }

    final Path defaultConfigPath =
        Paths.get(System.getProperty("user.dir"), "schemaspy.properties")
            .toAbsolutePath()
            .normalize();
    if (Files.isRegularFile(defaultConfigPath)) {
      return defaultConfigPath;
    }
    return null;
  }

  private static String normalizePropertyKey(final String key) {
    String normalizedKey = key.trim().toLowerCase(Locale.ROOT);
    if (normalizedKey.startsWith(PROPERTY_PREFIX)) {
      normalizedKey = normalizedKey.substring(PROPERTY_PREFIX.length());
    }
    return normalizedKey;
  }

  private PropertiesResolver() {
    // Utility class
  }
}
