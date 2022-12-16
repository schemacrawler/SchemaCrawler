/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.ScriptEngineManager;

import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import us.fatehi.utility.string.StringFormat;

/** Main executor for the script engine integration. */
public final class ScriptEngineExecutor extends AbstractScriptEngineExecutor {

  private static final Logger LOGGER = Logger.getLogger(ScriptEngineExecutor.class.getName());

  public ScriptEngineExecutor(final String scriptingLanguage) {
    super(scriptingLanguage);
  }

  @Override
  public boolean canGenerate() {
    try {
      obtainScriptEngine();
      return scriptEngine != null;
    } catch (final Exception e) {
      LOGGER.log(
          Level.CONFIG,
          e,
          new StringFormat("Script engine not found for language <%s>", scriptingLanguage));
      return false;
    }
  }

  @Override
  protected void obtainScriptEngine() {
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
      throw new InternalRuntimeException(
          String.format("Script engine not found for language <%s>", scriptingLanguage));
    }
  }
}
