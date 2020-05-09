/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
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
import static sf.util.Utility.isBlank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

public class DatabaseScriptsTest
{

  private final static Pattern fileNamePattern = Pattern.compile(".*\\/(.*\\..*)");

  private List<String> booksDatabaseSqlResources;

  @Autowired
  private ResourceLoader resourceLoader;

  @BeforeEach
  public void setup()
    throws IOException
  {
    booksDatabaseSqlResources = loadResources("classpath*:/**/db/books/*.sql");
    assertThat(booksDatabaseSqlResources, hasSize(74));
  }

  @Test
  public void booksDatabaseScripts()
    throws Exception
  {
    final List<String> scripts = loadResources("classpath*:/**/*.scripts.txt");
    assertThat(scripts, hasSize(14));
    final Set<String> failedScripts = new HashSet<>();
    for (final String scriptName : scripts)
    {
      System.out.println(scriptName);
      final String scriptsResource = "/" + scriptName;
      try (
        final BufferedReader reader = new BufferedReader(new InputStreamReader(DatabaseScriptsTest.class.getResourceAsStream(
          scriptsResource), UTF_8))
      )
      {
        final List<String> lines = reader
          .lines()
          .filter(line -> !isBlank(line))
          .collect(Collectors.toList());
        assertThat(lines, is(not(empty())));
        int i = 0;
        for (final String sqlResource : booksDatabaseSqlResources)
        {
          if (!lines
            .get(i)
            .endsWith(sqlResource))
          {
            final String message = String.format("%s - line %d - missing %s", scriptName, (i + 1), sqlResource);
            failedScripts.add(message);
            break;
          }
          i++;
        }
      }
      catch (final IOException e)
      {
        throw new RuntimeException(e.getMessage(), e);
      }
    }
    if (!failedScripts.isEmpty())
    {
      fail("\n" + String.join("\n", failedScripts));
    }
  }

  private String getScriptName(final String path)
  {
    final String scriptName;
    final Matcher matcher = fileNamePattern.matcher(path);
    if (matcher.matches())
    {
      scriptName = matcher.group(1);
    }
    else
    {
      scriptName = null;
    }
    return scriptName;
  }

  private List<String> loadResources(final String pattern)
    throws IOException
  {
    final Resource[] resources = ResourcePatternUtils
      .getResourcePatternResolver(resourceLoader)
      .getResources(pattern);
    final List<String> scripts = new ArrayList<>();
    for (final Resource classpathResource : resources)
    {
      final String scriptName = getScriptName(classpathResource
                                                .getURL()
                                                .getPath());
      scripts.add(scriptName);
    }
    scripts.sort(naturalOrder());
    return scripts;
  }

}
