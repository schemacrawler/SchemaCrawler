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
package schemacrawler.test.utility;

import static org.junit.jupiter.api.condition.OS.LINUX;
import static org.junit.jupiter.api.extension.ConditionEvaluationResult.disabled;
import static org.junit.jupiter.api.extension.ConditionEvaluationResult.enabled;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

final class HeavyDatabaseExtension implements ExecutionCondition {

  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(final ExtensionContext context) {

    final boolean overrideForDev = System.getProperty("usetestcontainers") != null;

    if (noHeavyDb()) {
      return disabled("Disable heavy database tests since \"heavydb\" is not set");
    } else if (overrideForDev) {
      return enabled("Override the test of Testcontainers for databases");
    } else if (LINUX.isCurrentOs()) {
      return enabled(
          "Enable heavy database tests on Linux, since GitHub Actions only supports Docker on this platform");
    } else {
      // Disable by default
      return disabled("Disable heavy database tests since conditions are not met");
    }
  }

  private boolean noHeavyDb() {
    final String heavydb = System.getProperty("heavydb");
    return heavydb == null
        || heavydb.toLowerCase().equals("false") // this is not the same as the Boolean check
        || heavydb.toLowerCase().equals("no");
  }
}
