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

  private final static Pattern fileNamePattern =
    Pattern.compile(".*\\/(.*\\..*)");

  private List<String> sqlScripts;

  @Autowired
  private ResourceLoader resourceLoader;

  @BeforeEach
  public void setup()
    throws IOException
  {
    sqlScripts = loadResources("classpath*:/**/db/**/*.sql");
  }

  @Test
  public void testScripts()
    throws Exception
  {
    final List<String> scripts = loadResources("classpath*:/**/*.scripts.txt");
    final Set<String> failedScripts = new HashSet<>();
    for (final String scriptName : scripts)
    {
      final String scriptsResource = "/" + scriptName;
      try (
        final BufferedReader reader = new BufferedReader(new InputStreamReader(
          DatabaseScriptsTest.class.getResourceAsStream(scriptsResource),
          UTF_8));
      )
      {
        final List<String> lines = reader
          .lines()
          .collect(Collectors.toList());
        for (int i = 0; i < lines.size(); i++)
        {
          final String line = lines.get(i);
          if (isBlank(line))
          {
            break;
          }
          final String sqlResource = sqlScripts.get(i);
          if (!line.endsWith(sqlResource))
          {
            failedScripts.add(scriptName);
            System.out.println(
              "#,/db/books/" + sqlResource + " - " + line + " in "
              + scriptName);
            continue;
          }
        }
      }
      catch (final IOException e)
      {
        throw new RuntimeException(e.getMessage(), e);
      }
    }
    if (!failedScripts.isEmpty())
    {
      fail(failedScripts
             .stream()
             .collect(Collectors.joining("\n")));
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
