/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static org.junit.Assert.fail;
import static sf.util.Utility.isBlank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;

public class DatabaseScriptsTest
{

  private Resource[] resources;

  @Autowired
  private ResourceLoader resourceLoader;

  @Before
  public void setup()
    throws IOException
  {
    resources = loadResources("classpath*:/**/db/**/*.sql");
  }

  @Test
  public void testScripts()
    throws Exception
  {
    final Resource[] scripts = loadResources("classpath*:/**/*.scripts.txt");
    final Set<String> failedScripts = new HashSet<>();
    for (final Resource script: scripts)
    {
      final String scriptName = script.getFile().getName();
      final String scriptsResource = "/" + scriptName;
      try (
          final BufferedReader reader = new BufferedReader(new InputStreamReader(DatabaseScriptsTest.class
            .getResourceAsStream(scriptsResource), UTF_8));)
      {
        final List<String> lines = reader.lines().collect(Collectors.toList());
        for (int i = 0; i < lines.size(); i++)
        {
          final String line = lines.get(i);
          if (isBlank(line)) break;
          final String sqlResource = resources[i].getFile().getName();
          if (!line.endsWith(sqlResource))
          {
            failedScripts.add(scriptName);
            System.out.println("#,/db/books/" + sqlResource + " - " + line
                               + " in " + scriptName);
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
      fail(failedScripts.stream().collect(Collectors.joining("\n")));
    }
  }

  private Resource[] loadResources(final String pattern)
    throws IOException
  {
    return ResourcePatternUtils.getResourcePatternResolver(resourceLoader)
      .getResources(pattern);
  }

}
