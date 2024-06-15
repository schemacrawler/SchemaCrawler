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

package schemacrawler.test.utility;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;
import java.io.IOException;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.hsqldb.jdbc.JDBCDataSource;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import static us.fatehi.utility.Utility.isBlank;
import schemacrawler.testdb.TestDatabase;
import us.fatehi.utility.LoggingConfig;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSources;

final class TestDatabaseConnectionParameterResolver
    implements ParameterResolver, BeforeAllCallback, AfterAllCallback, AfterEachCallback {
  private enum AnnotationType {
    classAnnotation,
    methodAnnotation
  }

  private static class AnnotationInfo {

    private final String script;
    private final AnnotationType annotationType;

    public AnnotationInfo(final String script, final AnnotationType annotationType) {
      this.script = script;
      this.annotationType = annotationType;
    }

    public AnnotationType getAnnotationType() {
      return annotationType;
    }

    public String getScript() {
      return script;
    }
  }

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
    final AnnotationType annotationType = locateAnnotation(context).getAnnotationType();
    if (annotationType == AnnotationType.methodAnnotation) {
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

    final String script = locateAnnotation(extensionContext).getScript();
    final Parameter parameter = parameterContext.getParameter();
    if (!isBlank(script)) {
      final DataSource ds = newEmbeddedDatabase(script);
      dataSource = DatabaseConnectionSources.fromDataSource(ds);

      if (isParameterConnection(parameter)) {
        return dataSource.get();
      }
      if (isParameterDatabaseConnectionSource(parameter)) {
        return dataSource;
      }
      throw new ParameterResolutionException("Could not resolve " + parameter);
    }
    final DataSource ds = newDataSource();
    dataSource = DatabaseConnectionSources.fromDataSource(ds);

    if (isParameterConnection(parameter)) {
      return dataSource.get();
    }
    if (isParameterDatabaseConnectionInfo(parameter)) {
      return new DatabaseConnectionInfo(
          testDatabase.getHost(),
          testDatabase.getPort(),
          testDatabase.getDatabase(),
          testDatabase.getConnectionUrl());
    }
    if (isParameterDatabaseConnectionSource(parameter)) {
      return dataSource;
    }
    throw new ParameterResolutionException("Could not resolve " + parameter);
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

  private AnnotationInfo locateAnnotation(final ExtensionContext extensionContext) {
    final Optional<WithTestDatabase> optionalMethodAnnotation =
        findAnnotation(extensionContext.getTestMethod(), WithTestDatabase.class);
    final Optional<WithTestDatabase> optionalClassAnnotation =
        findAnnotation(extensionContext.getTestClass(), WithTestDatabase.class);

    final AnnotationType annotationType;
    final WithTestDatabase withTestDatabase;
    if (optionalMethodAnnotation.isPresent()) {
      withTestDatabase = optionalMethodAnnotation.get();
      annotationType = AnnotationType.methodAnnotation;
    } else if (optionalClassAnnotation.isPresent()) {
      withTestDatabase = optionalClassAnnotation.get();
      annotationType = AnnotationType.classAnnotation;
    } else {
      withTestDatabase = null;
      annotationType = AnnotationType.classAnnotation;
    }
    final String script;
    if (withTestDatabase != null) {
      script = withTestDatabase.script();
    } else {
      script = null;
    }
    return new AnnotationInfo(script, annotationType);
  }

  private BasicDataSource newDataSource() {
    final BasicDataSource ds = new BasicDataSource();
    ds.setUrl(testDatabase.getConnectionUrl());
    ds.setUsername("sa");
    ds.setPassword("");
    return ds;
  }

  private DataSource newEmbeddedDatabase(final String script) {
    try {
      // Create data source
      final String randomDatabaseName = RandomStringUtils.randomAlphabetic(7);
      final JDBCDataSource hsqlDataSource = new JDBCDataSource();
      hsqlDataSource.setDatabase("jdbc:hsqldb:mem:" + randomDatabaseName);
      // Read script
      final String sql = IOUtils.resourceToString(script, StandardCharsets.UTF_8);
      String[] statements = sql.split(";");
      // Create a QueryRunner to execute the SQL statements
      final QueryRunner runner = new QueryRunner(hsqlDataSource);
      for (String statement : statements) {
          statement = statement.trim();
          System.out.println(statement);
          if (!statement.isEmpty()) {
              runner.update(statement);
          }
      }
      return hsqlDataSource;
    } catch (IOException | SQLException e) {
      return fail("Could not create a data source", e);
    }
  }
}
