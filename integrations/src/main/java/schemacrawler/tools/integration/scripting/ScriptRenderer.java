/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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

package schemacrawler.tools.integration.scripting;


import java.io.File;
import java.io.FileReader;
import java.io.Writer;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang.StringUtils;

import schemacrawler.schema.Catalog;
import schemacrawler.tools.integration.SchemaRenderer;

/**
 * Main executor for the scripting engine integration.
 * 
 * @author Sualeh Fatehi
 */
public final class ScriptRenderer
  extends SchemaRenderer
{

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.integration.TemplatedSchemaRenderer#render(java.lang.String,
   *      schemacrawler.schema.Schema, java.io.Writer)
   */
  @Override
  protected void render(final String scriptFileName,
                        final Catalog catalog,
                        final Writer writer)
    throws Exception
  {
    if (StringUtils.isBlank(scriptFileName))
    {
      throw new Exception("No script file provided");
    }
    final File scriptFile = new File(scriptFileName);
    if (!scriptFile.exists() || !scriptFile.canRead())
    {
      throw new Exception("Cannot read script file, " + scriptFileName);
    }

    final String ext = scriptFileName.lastIndexOf(".") == -1
                                                            ? ""
                                                            : scriptFileName
                                                              .substring(scriptFileName
                                                                           .lastIndexOf(".") + 1,
                                                                         scriptFileName
                                                                           .length());

    // Create a new instance of the engine
    final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    ScriptEngine scriptEngine = scriptEngineManager.getEngineByExtension(ext);
    if (scriptEngine == null)
    {
      scriptEngine = scriptEngineManager.getEngineByName("JavaScript");
    }
    if (scriptEngine == null)
    {
      throw new RuntimeException("Script engine not found");
    }

    // Set the context
    scriptEngine.put("catalog", catalog);

    // Evaluate the script
    scriptEngine.eval(new FileReader(scriptFile));
  }

}
