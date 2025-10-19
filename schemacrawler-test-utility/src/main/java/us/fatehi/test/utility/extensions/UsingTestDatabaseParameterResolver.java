/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.test.utility.extensions;

import static java.util.Objects.requireNonNull;
import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;

import com.zaxxer.hikari.HikariDataSource;
import java.io.Closeable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import javax.sql.DataSource;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import schemacrawler.testdb.TestDatabase;
import us.fatehi.test.utility.DataSourceTestUtility;
import us.fatehi.test.utility.DatabaseConnectionInfo;

public class UsingTestDatabaseParameterResolver
    implements ParameterResolver,
        BeforeAllCallback,
        BeforeEachCallback,
        AfterAllCallback,
        AfterEachCallback {

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

    public boolean hasScript() {
      return script != null && !script.isBlank();
    }
  }

  private enum AnnotationType {
    classAnnotation,
    methodAnnotation
  }

  private static String getScript(final Annotation annotation) {
    try {
      final Method method = annotation.annotationType().getMethod("script");
      return String.valueOf(method.invoke(annotation));
    } catch (final Exception e) {
      return "";
    }
  }

  private static boolean isParameterConnection(final Parameter parameter) {
    return parameter.getType().isAssignableFrom(Connection.class);
  }

  private static boolean isParameterDatabaseConnectionInfo(final Parameter parameter) {
    return parameter.getType().equals(DatabaseConnectionInfo.class);
  }

  private final Class<? extends Annotation> annotationClass;

  private TestDatabase testDatabase;
  private DataSource dataSource;

  public UsingTestDatabaseParameterResolver() {
    this(UsingTestDatabase.class);
  }

  protected UsingTestDatabaseParameterResolver(final Class<? extends Annotation> annotationClass) {
    this.annotationClass = requireNonNull(annotationClass, "No annotation class provided");
  }

  @Override
  public void afterAll(final ExtensionContext context) throws Exception {
    closeDataSource();
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
    createDataSource(context);
  }

  @Override
  public void beforeEach(final ExtensionContext context) throws Exception {
    final AnnotationType annotationType = locateAnnotation(context).getAnnotationType();
    if (annotationType == AnnotationType.methodAnnotation) {
      createDataSource(context);
    }
  }

  @Override
  public Object resolveParameter(
      final ParameterContext parameterContext, final ExtensionContext context)
      throws ParameterResolutionException {
    final Parameter parameter = parameterContext.getParameter();
    if (isParameterConnection(parameter)) {
      return getConnection(parameter);
    }
    if (isParameterDatabaseConnectionInfo(parameter)) {
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
      final ParameterContext parameterContext, final ExtensionContext context)
      throws ParameterResolutionException {

    final Parameter parameter = parameterContext.getParameter();

    final boolean hasConnection = isParameterConnection(parameter);
    final boolean hasDatabaseConnectionInfo = isParameterDatabaseConnectionInfo(parameter);
    return hasConnection || hasDatabaseConnectionInfo;
  }

  protected DataSource getDataSource() {
    return dataSource;
  }

  private void closeDataSource() throws IOException {
    if (dataSource != null) {
      if (dataSource instanceof final Closeable closeable) {
        closeable.close();
      }
      dataSource = null;
    }

    if (testDatabase != null) {
      testDatabase.stop();
      testDatabase = null;
    }
  }

  private void createDataSource(final ExtensionContext context) {
    if (testDatabase == null) {
      testDatabase = TestDatabase.initialize();
    }

    if (dataSource != null && !((HikariDataSource) dataSource).isClosed()) {
      return;
    }
    final AnnotationInfo annotationInfo = locateAnnotation(context);
    if (annotationInfo.hasScript()) {
      dataSource = DataSourceTestUtility.newEmbeddedDatabase(annotationInfo.getScript());
    } else {
      dataSource = newDataSource();
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

  private AnnotationInfo locateAnnotation(final ExtensionContext context) {
    final Optional<? extends Annotation> optionalMethodAnnotation =
        findAnnotation(context.getTestMethod(), annotationClass);
    final Optional<? extends Annotation> optionalClassAnnotation =
        findAnnotation(context.getTestClass(), annotationClass);

    final AnnotationType annotationType;
    final Annotation testDatabaseAnnotation;
    if (optionalMethodAnnotation.isPresent()) {
      testDatabaseAnnotation = optionalMethodAnnotation.get();
      annotationType = AnnotationType.methodAnnotation;
    } else {
      if (optionalClassAnnotation.isPresent()) {
        testDatabaseAnnotation = optionalClassAnnotation.get();
      } else {
        testDatabaseAnnotation = null;
      }
      annotationType = AnnotationType.classAnnotation;
    }
    final String script;
    if (testDatabaseAnnotation != null) {
      script = getScript(testDatabaseAnnotation);
    } else {
      script = "";
    }
    return new AnnotationInfo(script, annotationType);
  }

  private DataSource newDataSource() {
    final HikariDataSource ds = new HikariDataSource();
    ds.setJdbcUrl(testDatabase.getConnectionUrl());
    ds.setUsername("sa");
    ds.setPassword("");
    ds.setMaximumPoolSize(10);

    return ds;
  }
}
