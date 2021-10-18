/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import us.fatehi.utility.ObjectToString;
import us.fatehi.utility.ioresource.InputResource;
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

  public AbstractScriptEngineExecutor(
      final String scriptingLanguage,
      final Charset inputCharset,
      final InputResource scriptResource,
      final Writer writer) {
    super(scriptingLanguage, inputCharset, scriptResource, writer);
  }
  /** {@inheritDoc} */
  @Override
  public Boolean call() throws Exception {

    obtainScriptEngine();
    if (scriptEngine == null) {
      return false;
    }
    logScriptEngineDetails(Level.CONFIG, scriptEngine.getFactory());

    LOGGER.log(Level.CONFIG, new StringFormat("Evaluating script, ", scriptResource));
    try (final Reader reader = scriptResource.openNewInputReader(inputCharset);
        final Writer writer = this.writer) {

      // Set up the context
      scriptEngine.getContext().setWriter(writer);
      for (final Entry<String, Object> entry : context.entrySet()) {
        scriptEngine.put(entry.getKey(), entry.getValue());
      }

      // Evaluate the script
      if (scriptEngine instanceof Compilable) {
        final CompiledScript script = ((Compilable) scriptEngine).compile(reader);
        final Object result = script.eval();
        LOGGER.log(Level.INFO, new StringFormat("Script execution result:%n%s", result));
      } else {
        scriptEngine.eval(reader);
      }
    }

    return true;
  }

  protected abstract void obtainScriptEngine() throws SchemaCrawlerException;
}
