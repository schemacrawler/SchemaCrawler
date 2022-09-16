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
import java.util.Optional;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import schemacrawler.testdb.TestDatabase;
import us.fatehi.utility.LoggingConfig;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSources;

final class TestDatabaseConnectionParameterResolver
    implements ParameterResolver, BeforeAllCallback, AfterAllCallback, AfterEachCallback {

  private static boolean isParameterConnection(final Parameter parameter) {
    return parameter.getType().isAssignableFrom(Connection.class);
  }

  private static boolean isParameterDatabaseConnectionInfo(final Parameter parameter) {
    return parameter.getType().equals(DatabaseConnectionInfo.class);
  }

  private static boolean isParameterDatabaseConnectionSource(final Parameter parameter) {
    return parameter.getType().isAssignableFrom(DatabaseConnectionSource.class);
  }

  private TestDatabase testDatabase;
  private DatabaseConnectionSource dataSource;

  @Override
  public void afterAll(final ExtensionContext context) throws Exception {
    if (dataSource != null) {
      dataSource.close();
      dataSource = null;
    }

    testDatabase.stop();
  }

  @Override
  public void afterEach(final ExtensionContext context) throws Exception {
    final String script = getDatabaseScript(context);
    if (!isBlank(script)) {
      if (dataSource != null) {
        dataSource.close();
        dataSource = null;
      }
    }
  }

  @Override
  public void beforeAll(final ExtensionContext context) throws Exception {
    // Turn off logging
    new LoggingConfig();

    testDatabase = TestDatabase.initialize();
  }

  @Override
  public Object resolveParameter(
      final ParameterContext parameterContext, final ExtensionContext extensionContext)
      throws ParameterResolutionException {

    final String script = getDatabaseScript(extensionContext);

    final Parameter parameter = parameterContext.getParameter();
    if (isBlank(script)) {
      final DataSource ds = newDataSource();
      this.dataSource = DatabaseConnectionSources.fromDataSource(ds);

      if (isParameterConnection(parameter)) {
        return dataSource.get();
      } else if (isParameterDatabaseConnectionInfo(parameter)) {
        return new DatabaseConnectionInfo(
            testDatabase.getHost(),
            testDatabase.getPort(),
            testDatabase.getDatabase(),
            testDatabase.getConnectionUrl());
      } else if (isParameterDatabaseConnectionSource(parameter)) {
        return dataSource;
      } else {
        throw new ParameterResolutionException("Could not resolve " + parameter);
      }
    } else {
      final DataSource ds = newEmbeddedDatabase(script);
      this.dataSource = DatabaseConnectionSources.fromDataSource(ds);

      if (isParameterConnection(parameter)) {
        return dataSource.get();
      } else if (isParameterDatabaseConnectionSource(parameter)) {
        return dataSource;
      } else {
        throw new ParameterResolutionException("Could not resolve " + parameter);
      }
    }
  }

  @Override
  public boolean supportsParameter(
      final ParameterContext parameterContext, final ExtensionContext extensionContext)
      throws ParameterResolutionException {
    final Parameter parameter = parameterContext.getParameter();

    final boolean hasConnection = isParameterConnection(parameter);
    final boolean hasDatabaseConnectionInfo = isParameterDatabaseConnectionInfo(parameter);
    final boolean hasDatabaseConnectionSource = isParameterDatabaseConnectionSource(parameter);

    return hasConnection || hasDatabaseConnectionInfo || hasDatabaseConnectionSource;
  }

  private String getDatabaseScript(final ExtensionContext extensionContext) {
    final WithTestDatabase withTestDatabase = locateAnnotation(extensionContext);
    final String script;
    if (withTestDatabase == null) {
      script = "";
    } else {
      script = withTestDatabase.script();
    }
    return script;
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

  private BasicDataSource newDataSource() {
    final BasicDataSource ds = new BasicDataSource();
    ds.setUrl(testDatabase.getConnectionUrl());
    ds.setUsername("sa");
    ds.setPassword("");
    return ds;
  }

  private EmbeddedDatabase newEmbeddedDatabase(final String script) {
    final EmbeddedDatabase db =
        new EmbeddedDatabaseBuilder()
            .generateUniqueName(true)
            .setScriptEncoding("UTF-8")
            .ignoreFailedDrops(true)
            .addScript(script)
            .build();
    return db;
  }
}
