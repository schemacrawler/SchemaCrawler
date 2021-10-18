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

import java.io.Writer;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.ScriptEngineManager;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import us.fatehi.utility.ioresource.InputResource;
import us.fatehi.utility.string.StringFormat;

/** Main executor for the script engine integration. */
public final class ScriptEngineExecutor extends AbstractScriptEngineExecutor {

  private static final Logger LOGGER = Logger.getLogger(ScriptEngineExecutor.class.getName());

  public ScriptEngineExecutor(
      final String scriptingLanguage,
      final Charset inputCharset,
      final InputResource scriptResource,
      final Writer writer) {
    super(scriptingLanguage, inputCharset, scriptResource, writer);
  }

  @Override
  public boolean canGenerate() {
    try {
      obtainScriptEngine();
      return scriptEngine != null;
    } catch (final SchemaCrawlerException e) {
      LOGGER.log(Level.CONFIG, "Script engine not found for language, " + scriptingLanguage, e);
      return false;
    }
  }

  @Override
  protected void obtainScriptEngine() throws SchemaCrawlerException {
    final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    LOGGER.log(Level.CONFIG, new StringFormat("Using script language <%s>", scriptingLanguage));
    try {
      scriptEngine = scriptEngineManager.getEngineByName(scriptingLanguage);
    } catch (final Exception e) {
      LOGGER.log(Level.FINE, "Script engine not found for language, " + scriptingLanguage);
    }

    if (scriptEngine == null) {
      scriptEngine = scriptEngineManager.getEngineByExtension(scriptingLanguage);
    }

    if (scriptEngine == null) {
      throw new SchemaCrawlerException(
          "Script engine not found for language, " + scriptingLanguage);
    }
  }
}
