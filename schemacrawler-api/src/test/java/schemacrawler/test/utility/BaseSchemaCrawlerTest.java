/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.test.utility;


import static java.util.Objects.requireNonNull;
import static sf.util.Utility.applyApplicationLogLevel;

import java.util.logging.Level;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInfo;

import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;

public abstract class BaseSchemaCrawlerTest
{

  private final static SchemaCrawlerOptions schemaCrawlerOptionsWithMaximumSchemaInfoLevel = SchemaCrawlerOptionsBuilder
    .builder().withSchemaInfoLevel(SchemaInfoLevelBuilder.maximum())
    .toOptions();

  @BeforeAll
  public static void setApplicationLogLevel()
    throws Exception
  {
    applyApplicationLogLevel(Level.OFF);
  }

  protected SchemaCrawlerOptions schemaCrawlerOptionsWithMaximumSchemaInfoLevel()
  {
    return schemaCrawlerOptionsWithMaximumSchemaInfoLevel;
  }

  protected String currentMethodName(final TestInfo testInfo)
  {
    requireNonNull(testInfo, "No test info provided");
    return testInfo.getTestMethod().map(method -> method.getName())
      .orElseThrow(() -> new RuntimeException("Could not find test method"));
  }

  protected String currentMethodFullName(final TestInfo testInfo)
  {
    requireNonNull(testInfo, "No test info provided");
    return testInfo.getTestMethod()
      .map(method -> String.format("%s.%s",
                                   method.getDeclaringClass().getSimpleName(),
                                   method.getName()))
      .orElseThrow(() -> new RuntimeException("Could not find test method"));
  }

}
