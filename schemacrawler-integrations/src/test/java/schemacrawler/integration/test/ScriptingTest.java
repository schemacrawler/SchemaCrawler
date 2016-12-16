/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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


import org.junit.Test;

import schemacrawler.schemacrawler.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.BaseExecutableTest;
import schemacrawler.tools.integration.scripting.ScriptExecutable;

public class ScriptingTest
  extends BaseExecutableTest
{

  @Test
  public void executableGroovy()
    throws Exception
  {
    executeExecutable(createScriptExecutable(),
                      "/plaintextschema.groovy",
                      "script_output.txt");
  }

  @Test
  public void executableJavaScript()
    throws Exception
  {
    executeExecutable(createScriptExecutable(),
                      "/plaintextschema.js",
                      "script_output.txt");
  }

  @Test
  public void executablePython()
    throws Exception
  {
    executeExecutable(createScriptExecutable(),
                      "/plaintextschema.py",
                      "script_output.txt");
  }

  @Test
  public void executableRuby()
    throws Exception
  {
    executeExecutable(createScriptExecutable(),
                      "/plaintextschema.rb",
                      "script_output_rb.txt");
  }

  private ScriptExecutable createScriptExecutable()
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    schemaCrawlerOptions
      .setSchemaInclusionRule(new RegularExpressionExclusionRule(".*\\.FOR_LINT"));

    final ScriptExecutable scriptExecutable = new ScriptExecutable();
    scriptExecutable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    return scriptExecutable;
  }

}
