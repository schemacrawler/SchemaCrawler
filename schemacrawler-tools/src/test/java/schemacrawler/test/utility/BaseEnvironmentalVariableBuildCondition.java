/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static sf.util.Utility.isBlank;

import java.util.function.Supplier;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.JdbcDatabaseContainer;

abstract class BaseEnvironmentalVariableBuildCondition
  implements ExecutionCondition
{

  public <C extends JdbcDatabaseContainer> C getJdbcDatabaseContainer(final Supplier<C> createTestContainer)
  {
    if (createTestContainer != null && shouldExecute())
    {
      return createTestContainer.get();
    }
    else
    {
      return null;
    }
  }

  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(final ExtensionContext context)
  {
    if (!shouldExecute())
    {
      return ConditionEvaluationResult
        .disabled("Development build - disable long running tests");
    }

    return ConditionEvaluationResult.enabled("Complete build - run all tests");
  }

  protected abstract String getSystemBooleanVariable();

  private boolean shouldExecute()
  {
    final String systemBooleanValue = System
      .getProperty(getSystemBooleanVariable());
    final boolean shouldExecute =
      systemBooleanValue != null && isBlank(systemBooleanValue) || Boolean
        .parseBoolean(systemBooleanValue);
    return shouldExecute;
  }

}
