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

package schemacrawler.tools.integration.scripting;


import static schemacrawler.tools.iosource.InputResourceUtility.createInputResource;
import static sf.util.IOUtility.getFileExtension;
import static sf.util.Utility.isBlank;

import javax.script.*;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.logging.Level;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerRuntimeException;
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

  static final String COMMAND = "script";
  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(ScriptCommand.class.getName());

  public ScriptCommand()
  {
    super(COMMAND);
  }

  @Override
  public void checkAvailibility()
    throws Exception
  {
    getScriptEngine();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void execute()
    throws Exception
  {
    checkCatalog();

    final Charset inputCharset = outputOptions.getInputCharset();
    // The output format value is the script file or resource name
    final String outputFormatValue = outputOptions.getOutputFormatValue();

    final ScriptEngine scriptEngine = getScriptEngine();
    try (final Reader reader = createInputResource(outputFormatValue)
      .openNewInputReader(inputCharset);
      final Writer writer = outputOptions.openNewOutputWriter())
    {
      final CommandChain chain = new CommandChain(this);

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

  @Override
  public boolean usesConnection()
  {
    return true;
  }

  private ScriptEngine getScriptEngine()
    throws SchemaCrawlerException
  {
    final String scriptFileName = outputOptions.getOutputFormatValue();
    if (isBlank(scriptFileName))
    {
      throw new SchemaCrawlerRuntimeException(
        "Please specify a script to execute");
    }
    final String scriptExtension = getFileExtension(scriptFileName);

    final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    final ScriptEngine scriptEngine;
    if (isBlank(scriptExtension))
    {
      scriptEngine = scriptEngineManager.getEngineByName("nashorn");
    }
    else
    {
      scriptEngine = scriptEngineManager.getEngineByExtension(scriptExtension);
    }
    if (scriptEngine == null)
    {
      throw new SchemaCrawlerException("Script engine not found");
    }

    logScriptEngineDetails(Level.CONFIG, scriptEngine.getFactory());

    return scriptEngine;
  }

  private void logScriptEngineDetails(final Level level,
                                      final ScriptEngineFactory scriptEngineFactory)
  {
    if (!LOGGER.isLoggable(level))
    {
      return;
    }

    LOGGER.log(level,
               String.format(
                 "Using script engine%n%s %s (%s %s)%nScript engine names: %s%nSupported file extensions: %s",
                 scriptEngineFactory.getEngineName(),
                 scriptEngineFactory.getEngineVersion(),
                 scriptEngineFactory.getLanguageName(),
                 scriptEngineFactory.getLanguageVersion(),
                 ObjectToString.toString(scriptEngineFactory.getNames()),
                 ObjectToString.toString(scriptEngineFactory.getExtensions())));
  }

}
