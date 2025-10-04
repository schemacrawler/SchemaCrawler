/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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
          "Script engine not found for language <%s>".formatted(scriptingLanguage));
    }
  }
}
