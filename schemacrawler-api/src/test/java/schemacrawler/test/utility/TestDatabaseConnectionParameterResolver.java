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


import java.lang.reflect.Parameter;
import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import schemacrawler.testdb.TestDatabase;
import us.fatehi.utility.LoggingConfig;

public class TestDatabaseConnectionParameterResolver
  implements ParameterResolver, BeforeAllCallback
{

  private final static TestDatabase testDatabase = TestDatabase.initialize();

  private static boolean isParameterConnection(final Parameter parameter)
  {
    return parameter
      .getType()
      .equals(Connection.class);
  }

  private static boolean isParameterDatabaseConnectionInfo(final Parameter parameter)
  {
    return parameter
      .getType()
      .equals(DatabaseConnectionInfo.class);
  }

  @Override
  public void beforeAll(final ExtensionContext context)
    throws Exception
  {
    // Turn off logging
    new LoggingConfig();
  }

  @Override
  public boolean supportsParameter(final ParameterContext parameterContext,
                                   final ExtensionContext extensionContext)
    throws ParameterResolutionException
  {
    final boolean hasConnection;
    final boolean hasDatabaseConnectionInfo;
    final Parameter parameter = parameterContext.getParameter();

    hasConnection = isParameterConnection(parameter);
    hasDatabaseConnectionInfo = isParameterDatabaseConnectionInfo(parameter);

    return hasConnection || hasDatabaseConnectionInfo;
  }

  @Override
  public Object resolveParameter(final ParameterContext parameterContext,
                                 final ExtensionContext extensionContext)
    throws ParameterResolutionException
  {
    try
    {
      final Parameter parameter = parameterContext.getParameter();
      if (isParameterConnection(parameter))
      {
        return testDatabase.getConnection();
      }
      else if (isParameterDatabaseConnectionInfo(parameter))
      {
        return new DatabaseConnectionInfo(testDatabase.getHost(),
                                          testDatabase.getPort(),
                                          testDatabase.getDatabase(),
                                          testDatabase.getConnectionUrl());
      }
      else
      {
        throw new ParameterResolutionException("Could not resolve "
                                               + parameter);
      }
    }
    catch (final SQLException e)
    {
      throw new ParameterResolutionException("", e);
    }
  }

}
