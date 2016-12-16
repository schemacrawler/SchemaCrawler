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

package schemacrawler.tools.integration.scripting;


import static sf.util.Utility.isBlank;

import java.io.Reader;
import java.io.Writer;
import java.sql.Connection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerCommandLineException;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.executable.BaseStagedExecutable;
import schemacrawler.tools.executable.CommandChainExecutable;
import sf.util.ObjectToString;
import sf.util.StringFormat;

/**
 * Main executor for the scripting engine integration.
 *
 * @author Sualeh Fatehi
 */
public final class ScriptExecutable
  extends BaseStagedExecutable
{

  private static final Logger LOGGER = Logger
    .getLogger(ScriptExecutable.class.getName());

  static final String COMMAND = "script";

  public static String getFileExtension(final String scriptFileName)
  {
    final String ext;
    if (scriptFileName != null)
    {
      ext = scriptFileName.lastIndexOf('.') == -1? "": scriptFileName
        .substring(scriptFileName.lastIndexOf('.') + 1,
                   scriptFileName.length());
    }
    else
    {
      ext = "";
    }
    return ext;
  }

  public ScriptExecutable()
  {
    super(COMMAND);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void executeOn(final Catalog catalog,
                              final Connection connection)
    throws Exception
  {

    final String scriptFileName = outputOptions.getOutputFormatValue();
    if (isBlank(scriptFileName))
    {
      throw new SchemaCrawlerCommandLineException("Please specify a script to execute");
    }

    // Create a new instance of the engine
    final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    final List<ScriptEngineFactory> engineFactories = scriptEngineManager
      .getEngineFactories();
    ScriptEngineFactory scriptEngineFactory = null;
    ScriptEngineFactory javaScriptEngineFactory = null;
    for (final ScriptEngineFactory engineFactory: engineFactories)
    {
      LOGGER.log(Level.FINER,
                 new StringFormat("Evaluating script engine: %s %s (%s %s)",
                                  engineFactory.getEngineName(),
                                  engineFactory.getEngineVersion(),
                                  engineFactory.getLanguageName(),
                                  engineFactory.getLanguageVersion()));
      final List<String> extensions = engineFactory.getExtensions();
      if (extensions.contains(getFileExtension(scriptFileName)))
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
      LOGGER.log(Level.CONFIG,
                 new StringFormat("Using script engine%n%s %s (%s %s)%nScript engine names: %s%nSupported file extensions: %s",
                                  scriptEngineFactory.getEngineName(),
                                  scriptEngineFactory.getEngineVersion(),
                                  scriptEngineFactory.getLanguageName(),
                                  scriptEngineFactory.getLanguageVersion(),
                                  ObjectToString
                                    .toString(scriptEngineFactory.getNames()),
                                  ObjectToString.toString(scriptEngineFactory
                                    .getExtensions())));
    }

    final CommandChainExecutable chain = new CommandChainExecutable();
    chain.setSchemaCrawlerOptions(schemaCrawlerOptions);
    chain.setAdditionalConfiguration(additionalConfiguration);

    final ScriptEngine scriptEngine = scriptEngineFactory.getScriptEngine();
    try (final Reader reader = outputOptions.openNewInputReader();
        final Writer writer = outputOptions.openNewOutputWriter();)
    {
      // Set up the context
      scriptEngine.getContext().setWriter(writer);
      scriptEngine.put("catalog", catalog);
      scriptEngine.put("connection", connection);
      scriptEngine.put("chain", chain);

      // Evaluate the script
      if (scriptEngine instanceof Compilable)
      {
        final CompiledScript script = ((Compilable) scriptEngine)
          .compile(reader);
        script.eval();
      }
      else
      {
        scriptEngine.eval(reader);
      }
    }

  }

}
