/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.command.script;

import static us.fatehi.utility.Utility.isClassAvailable;

/** Main executor for the GraalVM JavaScript integration. */
public final class GraalJSScriptExecutor extends AbstractScriptEngineExecutor {

  public GraalJSScriptExecutor(final String scriptingLanguage) {
    super(scriptingLanguage);
    System.setProperty("polyglot.engine.WarnInterpreterOnly", "false");
  }

  @Override
  public boolean canGenerate() {
    return (scriptingLanguage.equalsIgnoreCase("js")
            || scriptingLanguage.equalsIgnoreCase("javascript"))
        && isClassAvailable("org.graalvm.polyglot.Context")
        && isClassAvailable("com.oracle.truffle.js.scriptengine.GraalJSScriptEngine");
  }

  @Override
  protected void obtainScriptEngine() {
    scriptEngine = GraalJSScriptUtility.createGraalJSScriptEngine();
  }
}
