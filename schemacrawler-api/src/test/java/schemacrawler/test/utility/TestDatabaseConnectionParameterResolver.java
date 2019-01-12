/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import schemacrawler.testdb.TestDatabase;

public class TestDatabaseConnectionParameterResolver
  implements ParameterResolver
{

  private final static TestDatabase testDatabase = TestDatabase.initialize();

  private boolean isParameterConnection(final Parameter parameter)
  {
    return parameter.getType().equals(Connection.class);
  }

  private boolean isParameterConnectionUrl(final Parameter parameter)
  {
    return parameter.isNamePresent()
           && parameter.getName().equals("connectionUrl")
           && parameter.getType().equals(String.class);
  }

  private boolean isParameterDatabase(final Parameter parameter)
  {
    return parameter.isNamePresent() && parameter.getName().equals("database")
           && parameter.getType().equals(String.class);
  }

  private boolean isParameterPort(final Parameter parameter)
  {
    return parameter.isNamePresent() && parameter.getName().equals("port")
           && parameter.getType().equals(int.class);
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
      else if (isParameterPort(parameter))
      {
        return testDatabase.getPort();
      }
      else if (isParameterDatabase(parameter))
      {
        return testDatabase.getDatabase();
      }
      else if (isParameterConnectionUrl(parameter))
      {
        return testDatabase.getConnectionUrl();
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

  @Override
  public boolean supportsParameter(final ParameterContext parameterContext,
                                   final ExtensionContext extensionContext)
    throws ParameterResolutionException
  {
    boolean hasConnection;
    boolean hasPort;
    boolean hasDatabase;
    boolean hasConnectionUrl;
    final Parameter parameter = parameterContext.getParameter();

    hasConnection = isParameterConnection(parameter);
    hasPort = isParameterPort(parameter);
    hasDatabase = isParameterDatabase(parameter);
    hasConnectionUrl = isParameterConnectionUrl(parameter);

    return hasConnection || hasPort || hasDatabase || hasConnectionUrl;
  }

}
