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

import static us.fatehi.utility.Utility.isClassAvailable;

import java.io.Writer;
import java.nio.charset.Charset;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;

import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import us.fatehi.utility.ioresource.InputResource;

/** Main executor for the GraalVM JavaScript integration. */
public final class GraalVMJavaScriptExecutor extends AbstractScriptEngineExecutor {

  public GraalVMJavaScriptExecutor(
      final String scriptingLanguage,
      final Charset inputCharset,
      final InputResource scriptResource,
      final Writer writer) {
    super(scriptingLanguage, inputCharset, scriptResource, writer);
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
  protected void obtainScriptEngine() throws SchemaCrawlerException {
    scriptEngine =
        GraalJSScriptEngine.create(
            null,
            Context.newBuilder("js")
                .allowHostAccess(HostAccess.ALL)
                .allowHostClassLookup(s -> true)
                .option("js.ecmascript-version", "2021"));
  }
}
