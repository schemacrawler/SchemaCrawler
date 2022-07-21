/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;
import static us.fatehi.utility.Utility.isBlank;

import java.lang.reflect.Parameter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import schemacrawler.testdb.TestDatabase;
import us.fatehi.utility.LoggingConfig;

final class TestDatabaseConnectionParameterResolver
    implements ParameterResolver, BeforeAllCallback {

  private static final TestDatabase testDatabase = TestDatabase.initialize();

  private static boolean isParameterConnection(final Parameter parameter) {
    return parameter.getType().equals(Connection.class);
  }

  private static boolean isParameterDatabaseConnectionInfo(final Parameter parameter) {
    return parameter.getType().equals(DatabaseConnectionInfo.class);
  }

  @Override
  public void beforeAll(final ExtensionContext context) throws Exception {
    // Turn off logging
    new LoggingConfig();
  }

  @Override
  public Object resolveParameter(
      final ParameterContext parameterContext, final ExtensionContext extensionContext)
      throws ParameterResolutionException {
    try {
      final WithTestDatabase withTestDatabase = locateAnnotation(extensionContext);
      final String script;
      if (withTestDatabase == null) {
        script = "";
      } else {
        script = withTestDatabase.script();
      }

      final Parameter parameter = parameterContext.getParameter();
      if (isBlank(script)) {
        if (isParameterConnection(parameter)) {
          return testDatabase.getConnection();
        } else if (isParameterDatabaseConnectionInfo(parameter)) {
          return new DatabaseConnectionInfo(
              testDatabase.getHost(),
              testDatabase.getPort(),
              testDatabase.getDatabase(),
              testDatabase.getConnectionUrl());
        } else {
          throw new ParameterResolutionException("Could not resolve " + parameter);
        }
      } else {
        if (isParameterConnection(parameter)) {
          final EmbeddedDatabase db =
              new EmbeddedDatabaseBuilder()
                  .generateUniqueName(true)
                  .setScriptEncoding("UTF-8")
                  .ignoreFailedDrops(true)
                  .addScript(script)
                  .build();

          final Connection connection = db.getConnection();
          return connection;
        } else {
          throw new ParameterResolutionException("Could not resolve " + parameter);
        }
      }
    } catch (final SQLException e) {
      throw new ParameterResolutionException("", e);
    }
  }

  @Override
  public boolean supportsParameter(
      final ParameterContext parameterContext, final ExtensionContext extensionContext)
      throws ParameterResolutionException {
    final boolean hasConnection;
    final boolean hasDatabaseConnectionInfo;
    final Parameter parameter = parameterContext.getParameter();

    hasConnection = isParameterConnection(parameter);
    hasDatabaseConnectionInfo = isParameterDatabaseConnectionInfo(parameter);

    return hasConnection || hasDatabaseConnectionInfo;
  }

  private WithTestDatabase locateAnnotation(final ExtensionContext extensionContext) {
    final Optional<WithTestDatabase> optionalMethodAnnotation =
        findAnnotation(extensionContext.getTestMethod(), WithTestDatabase.class);
    final Optional<WithTestDatabase> optionalClassAnnotation =
        findAnnotation(extensionContext.getTestClass(), WithTestDatabase.class);

    final WithTestDatabase withTestDatabase;
    if (optionalMethodAnnotation.isPresent()) {
      withTestDatabase = optionalMethodAnnotation.get();
    } else if (optionalClassAnnotation.isPresent()) {
      withTestDatabase = optionalClassAnnotation.get();
    } else {
      withTestDatabase = null;
    }
    return withTestDatabase;
  }
}
