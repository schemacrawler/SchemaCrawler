/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Comparator.naturalOrder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.BufferedReader;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static us.fatehi.utility.Utility.isBlank;
import us.fatehi.utility.ioresource.ClasspathInputResource;

public class DatabaseScriptsTest {

  private static class DatabaseScriptSection {

    private final Pattern scriptNamePattern =
        Pattern.compile("((\\d{2})_([a-z_]+))(_(\\d{2})_([a-z_]+))?_[A-Z].sql");

    private final int[] section;
    private final String[] name;

    DatabaseScriptSection(final String script) {
      final Matcher matcher = scriptNamePattern.matcher(script);
      if (!matcher.matches()) {
        throw new IllegalArgumentException(script);
      }

      section = new int[2];
      name = new String[2];

      section[0] = Integer.parseInt(matcher.group(2));
      name[0] = matcher.group(3);

      if (matcher.group(4) != null) {
        section[1] = Integer.parseInt(matcher.group(5));
        name[1] = matcher.group(6);
      }
    }

    @Override
    public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null || getClass() != obj.getClass()) {
        return false;
      }
      final DatabaseScriptSection other = (DatabaseScriptSection) obj;
      return Arrays.equals(name, other.name) && Arrays.equals(section, other.section);
    }

    @Override
    public int hashCode() {
      return Objects.hash(Arrays.hashCode(name), Arrays.hashCode(section));
    }

    public boolean matches(final String line) {
      if (line != null) {
        return line.contains(toString());
      }
      return false;
    }

    @Override
    public String toString() {
      if (section[1] == 0) {
        return String.format("%02d_%s", section[0], name[0]);
      }
      return String.format("%02d_%s_%02d_%s", section[0], name[0], section[1], name[1]);
    }
  }

  private static final Pattern fileNamePattern = Pattern.compile(".*\\/(.*\\..*)");

  private Collection<DatabaseScriptSection> booksDatabaseScriptSections;

  @Test
  public void booksDatabaseScripts() throws Exception {
    final List<String> scripts = loadResources("**/*.scripts.txt");
    assertThat(scripts, hasSize(14));
    final List<String> failedScripts = new ArrayList<>();
    for (final String scriptName : scripts) {
      final Map<DatabaseScriptSection, Integer> scriptSectionsCounts = makeScriptSectionsCounts();
      try (final BufferedReader reader =
          new ClasspathInputResource(scriptName).openNewInputReader(UTF_8)) {
        final List<String> lines =
            reader.lines().filter(line -> !isBlank(line)).collect(Collectors.toList());
        assertThat(lines, is(not(empty())));
        for (final String line : lines) {
          for (final DatabaseScriptSection databaseScriptSection : scriptSectionsCounts.keySet()) {
            if (databaseScriptSection.matches(line)) {
              scriptSectionsCounts.merge(databaseScriptSection, 1, Integer::sum);
            }
          }
        }
      }

      for (final Map.Entry<DatabaseScriptSection, Integer> databaseScriptSectionIntegerEntry :
          scriptSectionsCounts.entrySet()) {
        final int count = databaseScriptSectionIntegerEntry.getValue();
        if (count != 1) {
          final String error;
          if (count < 1) {
            error = "missing";
          } else {
            error = "duplicate";
          }
          final String message =
              String.format(
                  "%s: %s %s",
                  scriptName, error, databaseScriptSectionIntegerEntry.getKey().toString());
          failedScripts.add(message);
        }
      }
    }

    if (!failedScripts.isEmpty()) {
      Collections.sort(failedScripts);
      fail("\n" + String.join("\n", failedScripts));
    }
  }

  @BeforeEach
  public void setup() throws Exception {
    booksDatabaseScriptSections = makeScriptSections("**/db/books/*.sql");
    assertThat(booksDatabaseScriptSections.size(), is(34));
  }

  private String getScriptName(final String path) {
    final String scriptName;
    final Matcher matcher = fileNamePattern.matcher(path);
    if (matcher.matches()) {
      scriptName = matcher.group(1);
    } else {
      scriptName = null;
    }
    return scriptName;
  }

  private List<String> loadResources(final String pattern) throws Exception {

    final PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);

    final URI uri = ClassLoader.getSystemResource("").toURI();
    final Path resourcesPath = Paths.get(uri).resolve("../../src/main/resources");
    System.out.println(resourcesPath);
    try (final Stream<Path> paths = Files.walk(resourcesPath)) {
      final List<String> resources =
          paths
              .filter(pathMatcher::matches)
              .map(path -> resourcesPath.relativize(path))
              .map(Path::toString)
              .map(path -> path.replace('\\', '/'))
              .collect(Collectors.toList());
      resources.sort(naturalOrder());
      return resources;
    }
  }

  private Collection<DatabaseScriptSection> makeScriptSections(final String pattern)
      throws Exception {
    final Set<DatabaseScriptSection> scripts = new HashSet<>();
    for (final String path : loadResources(pattern)) {
      final String scriptName = getScriptName(path);
      final DatabaseScriptSection databaseScriptSection = new DatabaseScriptSection(scriptName);
      scripts.add(databaseScriptSection);
    }
    return scripts;
  }

  private Map<DatabaseScriptSection, Integer> makeScriptSectionsCounts() {
    final Map<DatabaseScriptSection, Integer> scriptSectionsCounts = new HashMap<>();
    for (final DatabaseScriptSection databaseScriptSection : booksDatabaseScriptSections) {
      scriptSectionsCounts.put(databaseScriptSection, 0);
    }
    return scriptSectionsCounts;
  }
}
