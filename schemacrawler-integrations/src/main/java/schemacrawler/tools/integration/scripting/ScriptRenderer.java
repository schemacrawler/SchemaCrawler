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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.sql.Connection;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import schemacrawler.schema.Database;
import schemacrawler.tools.integration.SchemaRenderer;
import sf.util.FileUtility;

/**
 * Main executor for the scripting engine integration.
 * 
 * @author Sualeh Fatehi
 */
public final class ScriptRenderer
  extends SchemaRenderer
{

  public ScriptRenderer()
  {
    super(ScriptRenderer.class.getSimpleName());
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.integration.TemplatedSchemaRenderer#render(java.lang.String,
   *      schemacrawler.schema.Schema, java.io.Writer)
   */
  @Override
  protected void render(final Connection connection,
                        final String scriptFileName,
                        final Database database,
                        final Writer writer)
    throws Exception
  {
    if (sf.util.Utility.isBlank(scriptFileName))
    {
      throw new Exception("No script file provided");
    }
    final Reader reader;
    final File scriptFile = new File(scriptFileName);
    if (scriptFile.exists() && scriptFile.canRead())
    {
      reader = new FileReader(scriptFile);
    }
    else
    {
      final InputStream inputStream = ScriptRenderer.class
        .getResourceAsStream("/" + scriptFileName);
      if (inputStream != null)
      {
        reader = new InputStreamReader(inputStream);
      }
      else
      {
        throw new Exception("Cannot load script, " + scriptFileName);
      }
    }

    // Create a new instance of the engine
    final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    ScriptEngine scriptEngine = scriptEngineManager
      .getEngineByExtension(FileUtility.getFileExtension(scriptFile));
    if (scriptEngine == null)
    {
      scriptEngine = scriptEngineManager.getEngineByName("JavaScript");
    }
    if (scriptEngine == null)
    {
      throw new RuntimeException("Script engine not found");
    }

    // Set up the context
    scriptEngine.getContext().setWriter(writer);
    scriptEngine.put("database", database);
    scriptEngine.put("connection", connection);

    // Evaluate the script
    scriptEngine.eval(reader);
  }

}
