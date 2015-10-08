/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
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
