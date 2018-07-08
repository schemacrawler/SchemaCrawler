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

package schemacrawler.tools.integration.scripting;


import static sf.util.IOUtility.getFileExtension;
import static sf.util.Utility.isBlank;

import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.logging.Level;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import schemacrawler.schemacrawler.SchemaCrawlerCommandLineException;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.executable.BaseSchemaCrawlerCommand;
import schemacrawler.tools.executable.CommandChain;
import sf.util.ObjectToString;
import sf.util.SchemaCrawlerLogger;

/**
 * Main executor for the scripting engine integration.
 *
 * @author Sualeh Fatehi
 */
public final class ScriptCommand
  extends BaseSchemaCrawlerCommand
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(ScriptCommand.class.getName());

  static final String COMMAND = "script";

  public ScriptCommand()
  {
    super(COMMAND);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void execute()
    throws Exception
  {
    // Null checks are done before execution

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
      logScriptEngineDetails(Level.FINER, engineFactory);

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
    logScriptEngineDetails(Level.CONFIG, scriptEngineFactory);

    final CommandChain chain = new CommandChain(this);

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

  private void logScriptEngineDetails(final Level level,
                                      final ScriptEngineFactory scriptEngineFactory)
  {
    if (!LOGGER.isLoggable(level))
    {
      return;
    }

    LOGGER
      .log(level,
           String
             .format("Using script engine%n%s %s (%s %s)%nScript engine names: %s%nSupported file extensions: %s",
                     scriptEngineFactory.getEngineName(),
                     scriptEngineFactory.getEngineVersion(),
                     scriptEngineFactory.getLanguageName(),
                     scriptEngineFactory.getLanguageVersion(),
                     ObjectToString.toString(scriptEngineFactory.getNames()),
                     ObjectToString
                       .toString(scriptEngineFactory.getExtensions())));
  }

}
