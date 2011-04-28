/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2011, Sualeh Fatehi.
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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import schemacrawler.schema.Database;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.executable.BaseExecutable;
import sf.util.FileUtility;
import sf.util.ObjectToString;
import sf.util.Utility;

/**
 * Main executor for the scripting engine integration.
 * 
 * @author Sualeh Fatehi
 */
public final class ScriptExecutable
  extends BaseExecutable
{

  private static final long serialVersionUID = -2232328675306451328L;

  private static final Logger LOGGER = Logger.getLogger(ScriptExecutable.class
    .getName());

  public ScriptExecutable()
  {
    super("script");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void executeOn(final Database database,
                                 final Connection connection)
    throws Exception
  {
    final String scriptFileName = outputOptions.getOutputFormatValue();
    if (Utility.isBlank(scriptFileName))
    {
      throw new SchemaCrawlerException("No script file provided");
    }
    final Reader reader;
    final File scriptFile = new File(scriptFileName);
    if (scriptFile.exists() && scriptFile.canRead())
    {
      reader = new FileReader(scriptFile);
    }
    else
    {
      final InputStream inputStream = ScriptExecutable.class
        .getResourceAsStream("/" + scriptFileName);
      if (inputStream != null)
      {
        reader = new InputStreamReader(inputStream);
      }
      else
      {
        throw new SchemaCrawlerException("Cannot load script, "
                                         + scriptFileName);
      }
    }

    // Create a new instance of the engine
    final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    final List<ScriptEngineFactory> engineFactories = scriptEngineManager
      .getEngineFactories();
    ScriptEngineFactory scriptEngineFactory = null;
    ScriptEngineFactory javaScriptEngineFactory = null;
    for (final ScriptEngineFactory engineFactory: engineFactories)
    {
      LOGGER.log(Level.FINER, String
        .format("Evaluating script engine: %s %s (%s %s)",
                engineFactory.getEngineName(),
                engineFactory.getEngineVersion(),
                engineFactory.getLanguageName(),
                engineFactory.getLanguageVersion()));
      final List<String> extensions = engineFactory.getExtensions();
      if (extensions.contains(FileUtility.getFileExtension(scriptFile)))
      {
        scriptEngineFactory = engineFactory;
        break;
      }
      if (engineFactory.getLanguageName().equalsIgnoreCase("JavaScript"))
      {
        javaScriptEngineFactory = engineFactory;
      }
    }
    if (scriptEngineFactory == null)
    {
      scriptEngineFactory = javaScriptEngineFactory;
    }
    if (scriptEngineFactory == null)
    {
      throw new SchemaCrawlerException("Script engine not found");
    }

    if (LOGGER.isLoggable(Level.CONFIG))
    {
      LOGGER
        .log(Level.CONFIG,
             String
               .format("Using script engine\n%s %s (%s %s)\nScript engine names: %s\nSupported file extensions: %s",
                       scriptEngineFactory.getEngineName(),
                       scriptEngineFactory.getEngineVersion(),
                       scriptEngineFactory.getLanguageName(),
                       scriptEngineFactory.getLanguageVersion(),
                       ObjectToString.toString(scriptEngineFactory.getNames()),
                       ObjectToString.toString(scriptEngineFactory
                         .getExtensions())));
    }

    final ScriptEngine scriptEngine = scriptEngineFactory.getScriptEngine();

    final Writer writer = outputOptions.openOutputWriter();

    // Set up the context
    scriptEngine.getContext().setWriter(writer);
    scriptEngine.put("database", database);
    scriptEngine.put("connection", connection);

    // Evaluate the script
    scriptEngine.eval(reader);

    outputOptions.closeOutputWriter(writer);
  }
}
