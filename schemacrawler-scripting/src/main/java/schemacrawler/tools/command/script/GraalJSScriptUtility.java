/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.command.script;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;

import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;

public class GraalJSScriptUtility {

  public static GraalJSScriptEngine createGraalJSScriptEngine() {
    return GraalJSScriptEngine.create(
        null,
        Context.newBuilder("js").allowHostAccess(HostAccess.ALL).allowHostClassLookup(s -> true));
  }
}
