/*
 * ======================================================================== SchemaCrawler
 * http://www.schemacrawler.com Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>. All
 * rights reserved. ------------------------------------------------------------------------
 * 
 * SchemaCrawler is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * SchemaCrawler and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0, GNU General Public License v3 or GNU Lesser General Public License v3.
 * 
 * You may elect to redistribute this code under any of these licenses.
 * 
 * The Eclipse Public License is available at: http://www.eclipse.org/legal/epl-v10.html
 * 
 * The GNU General Public License v3 and the GNU Lesser General Public License v3 are available at:
 * http://www.gnu.org/licenses/
 * 
 * ========================================================================
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
import static us.fatehi.utility.Utility.isBlank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;

public class DatabaseScriptsTest {

  private class DatabaseScriptSection {

    private final Pattern scriptNamePattern = Pattern.compile("(\\d\\d)_(.*)_(\\d\\d)_[A-Z].sql");

    private final String name;
    private final int section;
    private final int subSection;

    DatabaseScriptSection(final String script) {
      final Matcher matcher = scriptNamePattern.matcher(script);
      if (!matcher.matches()) {
        throw new IllegalArgumentException(script);
      }
      section = Integer.valueOf(matcher.group(1));
      name = matcher.group(2);
      subSection = Integer.valueOf(matcher.group(3));
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      final DatabaseScriptSection that = (DatabaseScriptSection) o;
      return section == that.section && subSection == that.subSection && name.equals(that.name);
    }

    @Override
    public int hashCode() {
      return Objects.hash(name, section, subSection);
    }

    public boolean matches(final String line) {
      if (line != null) {
        return line.contains(toString());
      } else {
        return false;
      }
    }

    @Override
    public String toString() {
      return String.format("%02d_%s_%02d", section, name, subSection);
    }
  }

  private static final Pattern fileNamePattern = Pattern.compile(".*\\/(.*\\..*)");

  private Collection<DatabaseScriptSection> booksDatabaseScriptSections;

  @Autowired
  private ResourceLoader resourceLoader;

  @Test
  public void booksDatabaseScripts() throws Exception {
    final List<String> scripts = loadResources("classpath*:/**/*.scripts.txt");
    assertThat(scripts, hasSize(15));
    final List<String> failedScripts = new ArrayList<>();
    for (final String scriptName : scripts) {
      final Map<DatabaseScriptSection, Integer> scriptSectionsCounts = makeScriptSectionsCounts();
      final String scriptsResource = "/" + scriptName;
      try (final BufferedReader reader = new BufferedReader(new InputStreamReader(
          DatabaseScriptsTest.class.getResourceAsStream(scriptsResource), UTF_8))) {
        final List<String> lines =
            reader.lines().filter(line -> !isBlank(line)).collect(Collectors.toList());
        assertThat(lines, is(not(empty())));
        final int i = 0;
        for (final String line : lines) {
          for (final DatabaseScriptSection databaseScriptSection : scriptSectionsCounts.keySet()) {
            if (databaseScriptSection.matches(line)) {
              scriptSectionsCounts.merge(databaseScriptSection, 1, Integer::sum);
            }
          }
        }
      } catch (final IOException e) {
        throw new RuntimeException(e.getMessage(), e);
      }

      for (final Map.Entry<DatabaseScriptSection, Integer> databaseScriptSectionIntegerEntry : scriptSectionsCounts
          .entrySet()) {
        final int count = databaseScriptSectionIntegerEntry.getValue();
        if (count != 1) {
          final String error;
          if (count < 1) {
            error = "missing";
          } else {
            error = "duplicate";
          }
          final String message = String.format("%s: %s %s", scriptName, error,
              databaseScriptSectionIntegerEntry.getKey().toString());
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
  public void setup() throws IOException {
    booksDatabaseScriptSections = makeScriptSections("classpath*:/**/db/books/*.sql");
    assertThat(booksDatabaseScriptSections.size(), is(29));
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

  private List<String> loadResources(final String pattern) throws IOException {
    final Resource[] resources =
        ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources(pattern);
    final List<String> scripts = new ArrayList<>();
    for (final Resource classpathResource : resources) {
      final String scriptName = getScriptName(classpathResource.getURL().getPath());
      scripts.add(scriptName);
    }
    scripts.sort(naturalOrder());
    return scripts;
  }

  private Collection<DatabaseScriptSection> makeScriptSections(final String pattern)
      throws IOException {
    final Set<DatabaseScriptSection> scripts = new HashSet<>();
    for (final String scriptName : loadResources(pattern)) {
      scripts.add(new DatabaseScriptSection(scriptName));
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
