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

package schemacrawler.tools.commandline.command;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

public class AvailableScriptEngines implements Iterable<ScriptEngineFactory> {

  private static final Logger LOGGER = Logger.getLogger(AvailableScriptEngines.class.getName());

  private static List<ScriptEngineFactory> availableScriptEngines() {
    final List<ScriptEngineFactory> availableScriptEngines = new ArrayList<>();
    try {
      final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
      availableScriptEngines.addAll(scriptEngineManager.getEngineFactories());
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not list script engines", e);
    }
    return availableScriptEngines;
  }

  private final List<ScriptEngineFactory> availableScriptEngines;

  public AvailableScriptEngines() {
    availableScriptEngines = availableScriptEngines();
  }

  @Override
  public Iterator<ScriptEngineFactory> iterator() {
    return availableScriptEngines.iterator();
  }

  public void print(final PrintStream out) {
    if (out == null) {
      return;
    }

    out.println();
    out.println("Available script engines:");
    for (final ScriptEngineFactory scriptEngine : availableScriptEngines) {
      out.printf(
          " %-20s %-15s file extensions: %s%n",
          scriptEngine.getEngineName(),
          scriptEngine.getEngineVersion(),
          scriptEngine.getExtensions());
    }
  }

  public int size() {
    return availableScriptEngines.size();
  }

  @Override
  public String toString() {
    return "AvailableScriptEngines " + availableScriptEngines;
  }
}
