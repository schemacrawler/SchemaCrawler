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

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.requireNotBlank;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import us.fatehi.utility.ioresource.InputResource;
import us.fatehi.utility.string.StringFormat;

abstract class AbstractScriptExecutor implements ScriptExecutor {

  private static final Logger LOGGER = Logger.getLogger(AbstractScriptExecutor.class.getName());

  protected final Charset inputCharset;
  protected final InputResource scriptResource;
  protected final Writer writer;
  protected final String scriptingLanguage;
  protected Map<String, Object> context;

  protected AbstractScriptExecutor(
      final String scriptingLanguage,
      final Charset inputCharset,
      final InputResource scriptResource,
      final Writer writer) {
    this.scriptingLanguage = requireNotBlank(scriptingLanguage, "No scripting language provided");
    this.inputCharset = requireNonNull(inputCharset, "No input encoding provided");
    this.scriptResource = requireNonNull(scriptResource, "No script input resource provided");
    this.writer = requireNonNull(writer, "No output writer provided");
    context = Collections.emptyMap();
  }

  @Override
  public void setContext(final Map<String, Object> context) {
    if (context == null) {
      this.context = Collections.emptyMap();
    }
    this.context = new HashMap<>(context);
  }

  protected void executeWithScriptEngine(final ScriptEngine scriptEngine)
      throws ScriptException, IOException {
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
  }
}
