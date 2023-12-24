/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.command.script;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;

import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.schemacrawler.exceptions.IORuntimeException;
import us.fatehi.utility.ObjectToString;
import us.fatehi.utility.string.StringFormat;

/** Main executor for the script engine integration. */
abstract class AbstractScriptEngineExecutor extends AbstractScriptExecutor {

  private static final Logger LOGGER =
      Logger.getLogger(AbstractScriptEngineExecutor.class.getName());

  private static void logScriptEngineDetails(
      final Level level, final ScriptEngineFactory scriptEngineFactory) {
    if (!LOGGER.isLoggable(level)) {
      return;
    }

    LOGGER.log(
        level,
        String.format(
            "Using script engine%n%s %s (%s %s)%nScript engine names: %s%nSupported file extensions: %s",
            scriptEngineFactory.getEngineName(),
            scriptEngineFactory.getEngineVersion(),
            scriptEngineFactory.getLanguageName(),
            scriptEngineFactory.getLanguageVersion(),
            ObjectToString.toString(scriptEngineFactory.getNames()),
            ObjectToString.toString(scriptEngineFactory.getExtensions())));
  }

  protected ScriptEngine scriptEngine;

  public AbstractScriptEngineExecutor(final String scriptingLanguage) {
    super(scriptingLanguage);
  }

  /** {@inheritDoc} */
  @Override
  public void run() {

    obtainScriptEngine();
    requireNonNull(scriptEngine, "Script engine not found");
    logScriptEngineDetails(Level.CONFIG, scriptEngine.getFactory());

    requireNonNull(reader, "No reader provided");
    requireNonNull(writer, "No writer provided");

    LOGGER.log(Level.CONFIG, new StringFormat("Evaluating script"));
    try (final Reader reader = this.reader;
        final Writer writer = this.writer) {

      // Set up the context
      scriptEngine.getContext().setWriter(writer);
      for (final Entry<String, Object> contextValue : context.entrySet()) {
        scriptEngine.put(contextValue.getKey(), contextValue.getValue());
      }

      // Evaluate the script
      if (scriptEngine instanceof Compilable) {
        final CompiledScript script = ((Compilable) scriptEngine).compile(reader);
        final Object result = script.eval();
        LOGGER.log(Level.INFO, new StringFormat("Script execution result:%n%s", result));
      } else {
        scriptEngine.eval(reader);
      }
    } catch (final ScriptException e) {
      throw new ExecutionRuntimeException("Could not execute script", e);
    } catch (final IOException e) {
      throw new IORuntimeException("Could not read script", e);
    }
  }

  protected abstract void obtainScriptEngine();
}
