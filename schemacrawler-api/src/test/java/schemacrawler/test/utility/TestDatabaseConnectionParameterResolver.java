/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.utility;

import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;
import static us.fatehi.utility.Utility.isBlank;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Parameter;
import java.sql.Connection;
import java.sql.SQLException;
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
import schemacrawler.testdb.TestDatabase;
import us.fatehi.test.utility.DataSourceTestUtility;
import us.fatehi.utility.LoggingConfig;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSources;

final class TestDatabaseConnectionParameterResolver
    implements ParameterResolver, BeforeAllCallback, AfterAllCallback, AfterEachCallback {
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

  private enum AnnotationType {
    classAnnotation,
    methodAnnotation
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
  private DataSource dataSource;

  @Override
  public void afterAll(final ExtensionContext context) throws Exception {
    closeDataSource();
    testDatabase.stop();
  }

  @Override
  public void afterEach(final ExtensionContext context) throws Exception {
    final AnnotationType annotationType = locateAnnotation(context).getAnnotationType();
    if (annotationType == AnnotationType.methodAnnotation) {
      closeDataSource();
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
    if (!isBlank(script)) {
      dataSource = DataSourceTestUtility.newEmbeddedDatabase(script);
    } else {
      dataSource = newDataSource();
    }

    final Parameter parameter = parameterContext.getParameter();
    if (isParameterConnection(parameter)) {
      return getConnection(parameter);
    }
    if (isParameterDatabaseConnectionSource(parameter)) {
      return DatabaseConnectionSources.fromDataSource(dataSource);
    }

    if (isBlank(script) && isParameterDatabaseConnectionInfo(parameter)) {
      return new DatabaseConnectionInfo(
          testDatabase.getHost(),
          testDatabase.getPort(),
          testDatabase.getDatabase(),
          testDatabase.getConnectionUrl());
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

  private void closeDataSource() throws IOException {
    if (dataSource != null) {
      if (dataSource instanceof final Closeable closeable) {
        closeable.close();
      }
      dataSource = null;
    }
  }

  private Connection getConnection(final Parameter parameter) throws ParameterResolutionException {
    if (dataSource == null) {
      throw new ParameterResolutionException("Data source is closed");
    }
    try {
      return dataSource.getConnection();
    } catch (final SQLException e) {
      throw new ParameterResolutionException(
          "Could not get connection for parameter <%s>".formatted(parameter), e);
    }
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
    } else {
      if (optionalClassAnnotation.isPresent()) {
        withTestDatabase = optionalClassAnnotation.get();
      } else {
        withTestDatabase = null;
      }
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
}
